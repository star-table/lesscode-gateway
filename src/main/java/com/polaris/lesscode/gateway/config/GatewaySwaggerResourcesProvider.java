package com.polaris.lesscode.gateway.config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.annotation.Configuration;

import com.polaris.lesscode.consts.ApplicationConsts;

import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

@Configuration
public class GatewaySwaggerResourcesProvider implements SwaggerResourcesProvider {
    
    private static final String SWAGGER2URL = "/v2/api-docs";
    private static final String GOLANG_SWAGGER2URL = "/swagger/v2/api-docs";

    @Autowired
    RouteLocator routeLocator;

    @Value("${spring.application.name}")
    private String self;

    @Override
    public List<SwaggerResource> get() {
        List<SwaggerResource> resources = new ArrayList<>();
        List<String> routeHosts = new ArrayList<>();
        routeLocator.getRoutes().filter(route -> route.getUri().getHost() != null)
                .filter(route -> !self.equals(route.getUri().getHost()))
                .subscribe(route -> routeHosts.add(route.getUri().getHost()));

        Set<String> dealed = new HashSet<>();
        routeHosts.forEach(instance -> {
            String url = "/" + instance + SWAGGER2URL;
            if(ApplicationConsts.APPLICATION_USERCENTER.equals(instance)) {
            	url = "/" + instance + "/swagger/doc.json";
            }
            if (!dealed.contains(url)) {
                dealed.add(url);
                SwaggerResource swaggerResource = new SwaggerResource();
                swaggerResource.setUrl(url);
                swaggerResource.setName(instance);
                resources.add(swaggerResource);
            }
        });
        return resources;
    }
}
