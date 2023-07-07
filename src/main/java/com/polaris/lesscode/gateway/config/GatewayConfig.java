package com.polaris.lesscode.gateway.config;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.netty.NettyServerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.Data;
import reactor.netty.channel.BootstrapHandlers;

@EnableConfigurationProperties
@Configuration
@ConfigurationProperties(prefix = "spring.cloud.gateway")
@Data
public class GatewayConfig {

	private Set<String> whiteList;

	private Set<String> notStatusList;


	@Bean
	public NettyServerCustomizer nettyServerCustomizer() {
		return httpServer -> httpServer.idleTimeout(Duration.ofSeconds(30));
	}

}
