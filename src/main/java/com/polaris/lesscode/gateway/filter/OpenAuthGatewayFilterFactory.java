/**
 * 
 */
package com.polaris.lesscode.gateway.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

@Component
public class OpenAuthGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    @Autowired
    private OpenAuthGatewayFilter openAuthGatewayFilter;

    @Override
    public GatewayFilter apply(Object config) {
        return openAuthGatewayFilter;
    }
}
