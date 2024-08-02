package com.shzshz.filesystem.controller;

import com.shzshz.filesystem.pojo.Result;
import com.shzshz.filesystem.pojo.User;
import com.shzshz.filesystem.service.UserService;
import com.shzshz.filesystem.utils.JwtUtils;
import com.shzshz.filesystem.utils.KeyManager;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.Cipher;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;

@Slf4j
@RestController
public class FileController {
    @Autowired
    private KeyManager keyManager;

    @Autowired
    private UserService userService;

    @Value("${file.upload-dir}")
    private String uploadDir;


    @PostMapping("/setkey")
    public Result setKey(@RequestBody User user2,@RequestHeader("token") String token) {
        Claims claims = JwtUtils.parseJWT(token);
        Integer userId = claims.get("id", Integer.class);

        User user = userService.getById(userId);
        try {
            // 获取私钥
            PrivateKey privateKey = keyManager.getPrivateKey();

            // 解密对称密钥
            byte[] encryptedKey = Base64.getDecoder().decode(user2.getUserkey());
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedKey = cipher.doFinal(encryptedKey);
            String symmetricKey = Base64.getEncoder().encodeToString(decryptedKey);

            user.setUserkey(symmetricKey);
            userService.setKey(user);
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/files")
    public Result getAllFiles() {
        List<String> fileNames = new ArrayList<>();

        try (Stream<Path> paths = Files.walk(Paths.get(uploadDir))) {
            fileNames = paths
                    .filter(Files::isRegularFile)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            Result.error(e.getMessage());
        }
        return Result.success(fileNames);
    }

    @PostMapping("/upload")
    public Result uploadFile(@RequestParam MultipartFile file, @RequestParam String filename, @RequestParam String type, @RequestParam String hash,@RequestHeader("token") String token) throws Exception {
        Claims claims = JwtUtils.parseJWT(token);
        Integer userId = claims.get("id", Integer.class);

        User user = userService.getById(userId);
        if (Objects.equals(user.getRole(), "admin")) {
            String base64EncodedKey = user.getUserkey();
            base64EncodedKey = base64EncodedKey.replaceAll("\\r|\\n", "");
            byte[] decodedHexKey = new String(Base64.getDecoder().decode(base64EncodedKey)).replaceAll("\\r|\\n", "").getBytes("utf-8");
            byte[] decodedKey = hexStringToByteArray(new String(decodedHexKey));
            log.info("decodedKey:{}", decodedKey.length);

            // 获取加密文件数据
            byte[] encryptedFileData = file.getBytes();

            // 提取 IV
            byte[] iv = new byte[16];
            System.arraycopy(encryptedFileData, 0, iv, 0, 16);

            // 提取密文
            byte[] ciphertext = new byte[encryptedFileData.length - 16];
            System.arraycopy(encryptedFileData, 16, ciphertext, 0, ciphertext.length);

            // 解密文件内容
            SecretKeySpec keySpec = new SecretKeySpec(decodedKey, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            byte[] decryptedFileData = cipher.doFinal(ciphertext);

            // 计算解密后的文件哈希值
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] calculatedHash = digest.digest(decryptedFileData);
            String calculatedHashHex = bytesToHex(calculatedHash);

            // 比较哈希值
            if (!calculatedHashHex.equalsIgnoreCase(hash)) {
                return Result.error("File hash mismatch, upload failed.");
            }

            String uuid = UUID.randomUUID().toString();
            Path uploadPath = Paths.get(uploadDir);
            File uploadDirFile = uploadPath.toFile();
            File destinationFile = new File(uploadDirFile, filename + "_" + uuid + "." + type);

            try (FileOutputStream fos = new FileOutputStream(destinationFile)) {
                fos.write(decryptedFileData);
                fos.flush();
                return Result.success();
            } catch (IOException e) {
                e.printStackTrace();
                return Result.error(e.getMessage());
            }
        } else {
            return Result.error("Access denied: insufficient permissions.");
        }
    }

    @PostMapping("/download")
    public ResponseEntity<byte[]> downloadFile(@RequestParam String filename, @RequestParam String type, @RequestHeader("token") String token) throws Exception {
        Claims claims = JwtUtils.parseJWT(token);
        Integer userId = claims.get("id", Integer.class);

        User user = userService.getById(userId);
        String base64EncodedKey = user.getUserkey();
        base64EncodedKey = base64EncodedKey.replaceAll("\\r|\\n", "");
        byte[] decodedHexKey = new String(Base64.getDecoder().decode(base64EncodedKey)).replaceAll("\\r|\\n", "").getBytes("utf-8");
        byte[] decodedKey = hexStringToByteArray(new String(decodedHexKey));

        // 要加密的文件路径
        Path filePath = Paths.get(uploadDir, filename + "." + type);
        byte[] fileData = Files.readAllBytes(filePath);

        // 计算文件的哈希值
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] fileHash = digest.digest(fileData);
        String fileHashHex = bytesToHex(fileHash);


        // 创建随机 IV
        byte[] iv = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        // 用对称密钥和 IV 加密文件内容
        SecretKeySpec keySpec = new SecretKeySpec(decodedKey, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        byte[] encryptedFileData = cipher.doFinal(fileData);

        // 将 IV 和加密数据组合在一起
        byte[] combinedData = new byte[iv.length + encryptedFileData.length];
        System.arraycopy(iv, 0, combinedData, 0, iv.length);
        System.arraycopy(encryptedFileData, 0, combinedData, iv.length, encryptedFileData.length);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", filename + "." + type);

        // 在响应头中添加文件哈希值
        headers.add("hash", fileHashHex);
        return new ResponseEntity<>(combinedData, headers, HttpStatus.OK);
    }

    @DeleteMapping("/files")
    public Result deleteFiles(@RequestBody List<String> filenames,@RequestHeader("token") String token) {
        Claims claims = JwtUtils.parseJWT(token);
        Integer userId = claims.get("id", Integer.class);
        User user = userService.getById(userId);

        if (Objects.equals(user.getRole(), "admin")) {
            StringBuilder result = new StringBuilder();

            for (String filename : filenames) {
                File file = new File(uploadDir + "\\" + filename);
                log.info("file:{}", uploadDir + filename);
                if (file.exists()) {
                    if (file.delete()) {
                        result.append("Deleted: ").append(filename).append("\n");
                    } else {
                        result.append("Failed to delete: ").append(filename).append("\n");
                    }
                } else {
                    result.append("File not found: ").append(filename).append("\n");
                }
            }
            return Result.success(result.toString());
        } else {
            return Result.error("Access denied: insufficient permissions.");
        }
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    // 辅助方法：将字节数组转换为十六进制字符串
    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
