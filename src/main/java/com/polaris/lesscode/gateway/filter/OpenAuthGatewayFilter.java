package com.polaris.lesscode.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.polaris.lesscode.consts.RequestContextConsts;
import com.polaris.lesscode.gateway.provider.UserCenterProvider;
import com.polaris.lesscode.uc.internal.resp.UserAuthResp;
import com.polaris.lesscode.vo.BaseResultCode;
import com.polaris.lesscode.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;

@Component
@Slf4j
public class OpenAuthGatewayFilter implements GatewayFilter, Ordered {

    @Autowired
    @Lazy
    private UserCenterProvider userProvider;
    
    @Override
    public int getOrder() {
        return 0;
    }

    private static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("Open Auth Gateway");
        if(HttpMethod.OPTIONS.equals(exchange.getRequest().getMethod())) {
            return chain.filter(exchange);
        }
        String authorization = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (! StringUtils.isEmpty(authorization) && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);
            Result<UserAuthResp> result = userProvider.apiKeyAuth(token);
            if(BaseResultCode.OK.equals(result.getCode()) && result.getData() != null) {
                UserAuthResp userAuth = result.getData();
                ServerHttpRequest request = exchange.getRequest()
                        .mutate()
                        .header(RequestContextConsts.IDENTITY_ORG_HEADER, String.valueOf(userAuth.getOrgId()))
                        .header(RequestContextConsts.IDENTITY_USER_HEADER, String.valueOf(userAuth.getUserId()))
                        .build();
                return chain.filter(exchange.mutate().request(request).build());
            }else {
                try {
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    exchange.getResponse().getHeaders().add("Content-Type", "application/json");
                    return exchange.getResponse().writeAndFlushWith(Flux.just(ByteBufFlux.just(exchange.getResponse().bufferFactory().wrap(objectMapper.writeValueAsBytes(result)))));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
    
}