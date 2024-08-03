package com.shzshz.filesystem.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.shzshz.filesystem.pojo.Result;
import com.shzshz.filesystem.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class LoginCheckInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1.获取请求URL
        String url = request.getRequestURL().toString();

        //2.获取请求头中的token
        String jwt = request.getHeader("token");

        //3.判断令牌是否存在
        if(!StringUtils.hasLength(jwt)){ //判断字符串非空且非null
            Result error = Result.error("NOT_LOGIN");
            String notLogin = JSONObject.toJSONString(error); //利用fastjson包将Java对象转换成json字符串
            response.getWriter().write(notLogin); //获取写流返回数据
            return false;
        }

        //4.解析token
        try { //解析token无异常说明token有效
            JwtUtils.parseJWT(jwt);
        }catch (Exception e){
            e.printStackTrace();
            Result error = Result.error("NOT_LOGIN");
            String notLogin = JSONObject.toJSONString(error);
            response.getWriter().write(notLogin);
            return false;
        }

        //5.放行
        return true; //返回值为true->放行，返回值为false->不放行
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        System.out.println("post handle");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        System.out.println("after completion");
    }
}
