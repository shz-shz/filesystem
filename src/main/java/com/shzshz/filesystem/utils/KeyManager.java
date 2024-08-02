package com.shzshz.filesystem.utils;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Component
public class KeyManager {

    @Getter
    private PrivateKey privateKey;

    @Value("${security.privateKeyPath}")
    private String privateKeyPath;

    @PostConstruct
    public void init() throws Exception {
        String key = "-----BEGIN PRIVATE KEY-----\n" +
                "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDa8Fnm6aS3Vrgi\n" +
                "Xt447e1vKsxjWqEs4/4qGFmeO8M/pQKgo2mJqzyrnhZOE7SpFfrcuWiVNU3hX6aS\n" +
                "nFLMMiE8ldctfTHThZM0S+LIktM7A231SOZfzy+nlNwWEjXnnJ0ETC6KLgtuXM+5\n" +
                "NUp3lrbmEIgQqHJQhvAYjCJ72YNXFl3XLwNYZKX6lLGlX2n/ckxIhYib3g9BL888\n" +
                "PnaT4QL8wZz206ScAAsuV/HjXphjMuOLaDcX58C6KhPFi83/0nd/BmjfBjNDc5SI\n" +
                "+XDPM8HGvxLtOpbIFQCWafj1tzSiTmTBAIcb1koT19NQSR3xxD4LuFWn1oEFiZyv\n" +
                "iRstNEcJAgMBAAECggEAF0BPS+akR5Q+GehZUVJzVMsGycZmN44+/9l2KwfNxISR\n" +
                "st2n56b32oH/fGs7VHmxGp/WK4JtsvGljwxq/qSUQwrmvz38fKpC59rnSsXNCQdk\n" +
                "Nhh33tTyCqoUdYOZle9b/YjZqHMbY60XomO2dNzcr7IxwRMikMrD1NeNEfoXqLfV\n" +
                "AnhHLOhTi1tb3YTespc8LtnLXEE039M/2/47zZqdL2DDR3IrVa8kU2CPGDzLsa1e\n" +
                "NFeqjYydVpzIvSxq2VmBhlbJI4r2hDxLZvo5zw3G0UkCbkbezEEXDIYAlIA2GdsZ\n" +
                "w8t53J0mP3RZJlwRMKMjz0kSrZta7ux8h/BmjgKXAQKBgQD6TTKm4RA3j7o4xfWj\n" +
                "GPcBk5V0CxcL6mtM6OwwVtToyr6MmBzZdhOl6FhbWnxo2xF1ZlKZms9F1JoV/qBh\n" +
                "uJtO9GSz9kIaRkofwyXTjaZbz2Z0weT1/CW97hUw/XMKmCGipTpWSvTPeDaE9Wrp\n" +
                "9q0O2LwPlj8d23AQj6ayZywvNQKBgQDf7F2yySaC4S7zizrV7VNRi4XGEl/7poFu\n" +
                "WBl9tgsd8odhSCaB4nLLlK2x3vTfjRgEvUqfD/VbllSTetAOfl/0K5OaC+zsSr3F\n" +
                "3b+Tf6eqRHJgG7D0P9/WddQBQczlZx1S6+mWVHK8DpZlgFcoVPkBy4XpbXh4j6Ns\n" +
                "xV4NBm5PBQKBgQCjRMneev52D6W8NLy2x6kPeTJKwJUm66FUSLcQDQPSWbWTxgFD\n" +
                "hDbqyLUh2dM/Hm6z8qu8kHPVrAlagX4iYCXGibpKfT6/HsxpDYVWCpbSWmYNkRCF\n" +
                "uBTyYCOokhqBdZyzGC4GbdxknJiLf/5eYM6f2ZZKYIoJMsNVui5VPuFeZQKBgBwQ\n" +
                "rfW4jGEfBBl2O9uQMT8pM40NxIXv6oz/8cH1zRLl0N36gwkIbsbhTZvbi+lw76+4\n" +
                "Yd/pyiC+iL8rR2RppnEd/RPzprA+9eYU0uwTtdn7VjKBhf42+0EP55B4xMuDzwMT\n" +
                "mmVDFFZJpcP2//WIIOV+srv9Anpp1lvNqOoNAkpZAoGBAMS6I4d8ObBfMa0SJHSL\n" +
                "yE174xJpjuWKftMMwVwMI1Xg1/Saq0UKVYjy2l+/cR0zehJMEugGXtFUzbBdf7d/\n" +
                "qFGt4JZ731qR+omFSgwoXcU1FGGPgp7PxzranGt892uo4Nlv5blINm0H+vUdJ7cP\n" +
                "oe3orJMM/P5zGKBVABuPIRyr\n" +
                "-----END PRIVATE KEY-----";
        key = key.replaceAll("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        byte[] keyBytes = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        privateKey = kf.generatePrivate(spec);
    }
}
