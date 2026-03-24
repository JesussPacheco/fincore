package com.fincore.gateway.config;

import com.fincore.gateway.filter.JwtAuthenticationFilter;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class GatewayConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public GatewayConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                // Public routes
                .route("auth-register", r -> r
                        .path("/api/v1/auth/register")
                        .and().method("POST")
                        .uri("http://localhost:8081"))

                .route("auth-login", r -> r
                        .path("/api/v1/auth/login")
                        .and().method("POST")
                        .uri("http://localhost:8081"))

                // Protected routes
                .route("account-service", r -> r
                        .path("/api/v1/accounts/**")
                        .filters(f -> f.filter(jwtFilter))
                        .uri("http://localhost:8082"))

                .route("transaction-service", r -> r
                        .path("/api/v1/transfers/**")
                        .filters(f -> f.filter(jwtFilter))
                        .uri("http://localhost:8083"))
                .build();
    }

    // Rate limit key by userId — falls back to IP for public routes
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> Mono.justOrEmpty(
                exchange.getRequest().getHeaders().getFirst("X-User-Id")
        ).switchIfEmpty(Mono.just(
                exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
        ));
    }

    @Bean
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(10, 20, 1);
    }
}