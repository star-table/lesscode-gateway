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
public class UserRateLimiterGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {
    
    @Autowired
    private UserRateLimiterGatewayFilter filter;
    
    @Override
    public GatewayFilter apply(Object obj) {
        return filter;
    }

}
