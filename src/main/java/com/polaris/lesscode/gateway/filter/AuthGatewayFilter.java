package com.polaris.lesscode.gateway.filter;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.polaris.lesscode.consts.RequestContextConsts;
import com.polaris.lesscode.gateway.config.GatewayConfig;
import com.polaris.lesscode.gateway.provider.UserCenterProvider;
import com.polaris.lesscode.uc.internal.resp.UserAuthResp;
import com.polaris.lesscode.vo.BaseResultCode;
import com.polaris.lesscode.vo.Result;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;

@Slf4j
@Component
public class AuthGatewayFilter implements GatewayFilter, Ordered	{

	@Autowired
	@Lazy
	private UserCenterProvider userProvider;
	
	@Autowired
	private GatewayConfig gatewayConfig;
	
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	@Override
	public int getOrder() {
		return 0;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		if(HttpMethod.OPTIONS.equals(exchange.getRequest().getMethod())) {
			return chain.filter(exchange);
		}
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
		if (StringUtils.isEmpty(token)){
			token = exchange.getRequest().getHeaders().getFirst("token");
		}
		if (StringUtils.isEmpty(token)){
			token = exchange.getRequest().getHeaders().getFirst("pm-token");
		}
        String uri = exchange.getRequest().getURI().getPath();
		HttpMethod method = exchange.getRequest().getMethod();
		boolean isWhiteList = isWhiteList(stripPrefix(uri, 1), method);
		log.info("uri {} is whiteList {}", uri, isWhiteList);
		if (StringUtils.isEmpty(token) && ! isWhiteList){
			exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
			return exchange.getResponse().setComplete();
		}

		Result<UserAuthResp> result = null;
		if (! StringUtils.isEmpty(token)){
			if(isNotStatusList(stripPrefix(uri, 1))) {
				result = userProvider.auth(token);
			}else {
				result = userProvider.authCheckStatus(token);
			}
		}
		if (! isWhiteList && (result == null || ! BaseResultCode.OK.equals(result.getCode()))){
			try {
				exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
				exchange.getResponse().getHeaders().add("Content-Type", "application/json");
				return exchange.getResponse().writeAndFlushWith(Flux.just(ByteBufFlux.just(exchange.getResponse().bufferFactory().wrap(objectMapper.writeValueAsBytes(result)))));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
				return exchange.getResponse().setComplete();
			}
		}

		Long orgId = 0L;
		Long userId = 0L;
		if (result != null && result.getData() != null){
			UserAuthResp userAuth = result.getData();
			orgId = userAuth.getOrgId();
			userId = userAuth.getUserId();
		}

		ServerHttpRequest request = exchange.getRequest()
				.mutate()
				.header(RequestContextConsts.IDENTITY_ORG_HEADER, String.valueOf(orgId))
				.header(RequestContextConsts.IDENTITY_USER_HEADER, String.valueOf(userId))
				.build();
		return chain.filter(exchange.mutate().request(request).build());
	}
	
	private boolean isWhiteList(String uri, HttpMethod method) {
		Set<String> whiteList = gatewayConfig.getWhiteList();
		if(! CollectionUtils.isEmpty(whiteList)) {
			for(String whiteUrl: whiteList) {
				boolean methodAccess = true;
				if (whiteUrl.contains(" ")){
					String[] infos = whiteUrl.split(" ");
					methodAccess = Objects.equals(infos[1], method.toString());
					whiteUrl = infos[0];
				}
				if(uri.startsWith(whiteUrl) && methodAccess) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isNotStatusList(String uri) {
		Set<String> notStatusList = gatewayConfig.getNotStatusList();
		if(! CollectionUtils.isEmpty(notStatusList)) {
			for(String url: notStatusList) {
				if(uri.startsWith(url)) {
					return true;
				}
			}
		}
		return false;
	}
	
	private String stripPrefix(String uri, int i) {
		 return "/" + Arrays.stream(StringUtils.tokenizeToStringArray(uri, "/"))
         .skip(i).collect(Collectors.joining("/"));
	}

}
