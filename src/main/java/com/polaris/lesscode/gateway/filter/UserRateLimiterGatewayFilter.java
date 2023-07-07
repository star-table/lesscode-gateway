/**
 * 
 */
package com.polaris.lesscode.gateway.filter;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;

import com.polaris.lesscode.consts.RequestContextConsts;
import com.polaris.lesscode.exception.BusinessException;
import com.polaris.lesscode.redis.limiter.RedisRateLimiter;
import com.polaris.lesscode.util.GsonUtils;

import reactor.core.publisher.Mono;

/**
 * @author Bomb.
 *
 */
@Component
public class UserRateLimiterGatewayFilter implements GatewayFilter, Ordered {
    
    @Value("${openapi.user.ratelimiter:2000}")
    private Integer userRateLimiter;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private RedisRateLimiter redisRateLimiter = new RedisRateLimiter();

    @PostConstruct
    public void init() {
        redisRateLimiter.setTemplate(stringRedisTemplate);
    }

    @Override
    public int getOrder() {
        return 1;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Mono<Void> mono = null;
        HttpHeaders headers = exchange.getRequest().getHeaders();
        List<String> orgIds = headers.get(RequestContextConsts.IDENTITY_ORG_HEADER);
        List<String> userIds = headers.get(RequestContextConsts.IDENTITY_USER_HEADER);
        if (CollectionUtils.isEmpty(orgIds) || CollectionUtils.isEmpty(userIds)) {
            return chain.filter(exchange);
        }
        String key = orgIds.get(0) + ":" + userIds.get(0);
        try {
            mono = redisRateLimiter.rateLimitedExecute(chain::filter, exchange, key, userRateLimiter, userRateLimiter);
        }catch(BusinessException be) {
            //Result<?> result = Result.error(be.getCode(),be.getMessage());
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("code", be.getCode());
            result.put("message", be.getMessage());
            String resultStr = GsonUtils.toJson(result);
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.OK);
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            DataBuffer buffer = response.bufferFactory().wrap(resultStr.getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(buffer));
        }
        return mono;
    }
}
