package com.zack.filter;

import com.google.gson.Gson;
import com.zack.base.BaseInfoProperties;
import com.zack.common.CommonResult;
import com.zack.config.ExcludeUrlProperties;
import com.zack.exceptions.ErrorCode;
import com.zack.utils.IPUtil;
import com.zack.utils.JWTUtils;
import com.zack.utils.RedisOperator;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Order(1)
@Component
@Slf4j
public class IPLimitFilter extends BaseInfoProperties implements GlobalFilter {
    @Autowired
    private RedisOperator redisOperator;
    @Autowired
    private ExcludeUrlProperties excludeUrlProperties;

    @Value("${blackIP.continueCounts}")
    private Integer continueCounts;
    @Value("${blackIP.timeInterval}")
    private Integer timeInterval;
    @Value("${blackIP.limitTimes}")
    private Integer limitTimes;

    // 路径匹配的规则器
    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // 1. 获取当前的请求路径
        String url = exchange.getRequest().getURI().getPath();
        log.info("SecurityFilterJWT url=" + url);

        //校验是否在黑名单中
        // 2. 获得所有的需要排除校验的url list
        List<String> limitUrls = excludeUrlProperties.getLimitUrls();

        // 3. 校验并且排除excludeList
        if (limitUrls != null && !limitUrls.isEmpty()) {
            for (String excludeUrl : limitUrls) {
                if (antPathMatcher.matchStart(excludeUrl, url)) {
                    // 如果匹配到，进行ip拦截
                    return doLimit(exchange,chain);
                }
            }
        }
      //默认直接放行
      return  chain.filter(exchange);
    }


    public Mono<Void> doLimit(ServerWebExchange exchange, GatewayFilterChain chain){
        //1.获取请求ip
        String ip = IPUtil.getIP(exchange.getRequest());
        /**
         * 需求：
         * 判断ip在20秒内请求的次数是否超过3次
         * 如果超过，则限制访问30秒
         * 等待30秒静默以后，才能够回复访问
         */
        final String ipKey = "gateway-ip:"+ip;
        final String limitedKey = "gateway-ip:limited:"+ip;

        //先判断是否已经被限制
        if(redisOperator.ttl(limitedKey)>0){
            //已经被限制，直接返回错误信息
            return renderErrorMsg(exchange, ErrorCode.SYSTEM_ERROR_BLACK_IP);
        }

        long increment = redisOperator.increment(ipKey, 1);
        if (increment == 1) {
            //第一次访问，设置过期时间
            redisOperator.expire(ipKey, timeInterval);
        }
        if(increment>=continueCounts){
            //超过限制次数，进行限制
            redisOperator.set(limitedKey,limitedKey,limitTimes);
            return renderErrorMsg(exchange, ErrorCode.SYSTEM_ERROR_BLACK_IP);
        }

        return chain.filter(exchange);
    }



    /**
     * 重新包装并且返回错误信息
     * @param exchange
     * @param
     * @return
     */
    public Mono<Void> renderErrorMsg(ServerWebExchange exchange,
                                     ErrorCode errorCode) {
        // 1. 获得response
        ServerHttpResponse response = exchange.getResponse();

        // 2. 构建jsonResult
        CommonResult jsonResult = CommonResult.error(errorCode);

        // 3. 修改response的code为500
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);

        // 4. 设定header类型
        if (!response.getHeaders().containsKey("Content-Type"))
            response.getHeaders().add("Content-Type", MimeTypeUtils.APPLICATION_JSON_VALUE);

        // 5. 转换json并且向response中写入数据
        String resultJson = new Gson().toJson(jsonResult);
        DataBuffer dataBuffer = response
                .bufferFactory()
                .wrap(resultJson.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(dataBuffer));
    }

}
