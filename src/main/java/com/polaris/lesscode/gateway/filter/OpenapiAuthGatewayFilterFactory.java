/**
 * 
 */
package com.polaris.lesscode.gateway.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

/**
 * @author Bomb.
 *
 */
@Component
public class OpenapiAuthGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    @Autowired
    private OpenapiAuthGatewayFilter openapiAuthGatewayFilter;

    @Override
    public GatewayFilter apply(Object config) {
        return openapiAuthGatewayFilter;
    }
}
