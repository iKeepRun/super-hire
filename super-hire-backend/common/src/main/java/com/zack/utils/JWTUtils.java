package com.zack.utils;

import com.zack.exceptions.ErrorCode;
import com.zack.exceptions.ThrowUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Encoder;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;

@Component
@Slf4j
@RefreshScope
public class JWTUtils {

    public static final String at = "@";

    @Autowired
    private JWTProperties jwtProperties;

    @Value("${jwt.key}")
    public String JWT_KEY;

    @PostConstruct
    public void init() {
        log.info("JWT_KEY from nacos: {}", JWT_KEY);
    }

    public String createJWTWithPrefix(String body, Long expireTimes, String prefix) {
        ThrowUtil.throwIf(expireTimes == null, ErrorCode.PARAMS_ERROR);
        return prefix + at + createJWT(body, expireTimes);
    }

    public String createJWTWithPrefix(String body, String prefix) {
        return prefix + at + createJWT(body);
    }

    public String createJWT(String body) {
        return dealJWT(body, null);
    }

    public String createJWT(String body, Long expireTimes) {

        ThrowUtil.throwIf(expireTimes == null, ErrorCode.PARAMS_ERROR);
        return dealJWT(body, expireTimes);
    }

    public String dealJWT(String body, Long expireTimes) {

//        String userKey = jwtProperties.getKey();
        String userKey = JWT_KEY;
        log.info("Nacos jwt key = " + JWT_KEY);

        // 1. 对秘钥进行base64编码
        String base64 = new BASE64Encoder().encode(userKey.getBytes());

        // 2. 对base64生成一个秘钥的对象
        SecretKey secretKey = Keys.hmacShaKeyFor(base64.getBytes());

        String jwt = "";
        if (expireTimes != null) {
            jwt = generatorJWT(body, expireTimes, secretKey);
        } else {
            jwt = generatorJWT(body, secretKey);
        }
        log.info("JWTUtils - dealJWT: generatorJWT = " + jwt);

        return jwt;
    }

    public String generatorJWT(String body, SecretKey secretKey) {
        String jwtToken = Jwts.builder()
                .setSubject(body)
                .signWith(secretKey)
                .compact();
        return jwtToken;
    }

    public String generatorJWT(String body, Long expireTimes, SecretKey secretKey) {
        // 定义过期时间
        Date expireDate = new Date(System.currentTimeMillis() + expireTimes);
        String jwtToken = Jwts.builder()
                .setSubject(body)
                .signWith(secretKey)
                .setExpiration(expireDate)
                .compact();
        return jwtToken;
    }

    public String checkJWT(String pendingJWT) {

//        String userKey = jwtProperties.getKey();
        String userKey = JWT_KEY;
        log.info("Nacos jwt key = " + JWT_KEY);

        // 1. 对秘钥进行base64编码
        String base64 = new BASE64Encoder().encode(userKey.getBytes());

        // 2. 对base64生成一个秘钥的对象
        SecretKey secretKey = Keys.hmacShaKeyFor(base64.getBytes());

        // 3. 校验jwt
        JwtParser jwtParser = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build();       // 构造解析器
        // 解析成功，可以获得Claims，从而去get相关的数据，如果此处抛出异常，则说明解析不通过，也就是token失效或者被篡改
        Jws<Claims> jws = jwtParser.parseClaimsJws(pendingJWT);      // 解析jwt

        String body = jws.getBody().getSubject();

        return body;
    }

//    public static void main(String[] args) {
//
//            // 创建一个安全的随机数生成器
//            SecureRandom secureRandom = new SecureRandom();
//            byte[] keyBytes = new byte[32]; // 32字节 = 256位
//            secureRandom.nextBytes(keyBytes);
//
//            // 将随机字节转换为Base64编码的字符串
//            String base64Key = Base64.getEncoder().encodeToString(keyBytes);
//
//            // 输出生成的密钥
//            System.out.println("Generated JWT Key: " + base64Key);
//
//    }
}
