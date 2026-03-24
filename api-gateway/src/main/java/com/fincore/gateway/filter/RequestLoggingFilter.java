package com.fincore.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class RequestLoggingFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long startTime = System.currentTimeMillis();

        log.info("REQUEST → method={} path={}",
                exchange.getRequest().getMethod(),
                exchange.getRequest().getPath()
        );

        return chain.filter(exchange).doFinally(signal -> {
            long duration = System.currentTimeMillis() - startTime;
            log.info("RESPONSE ← status={} path={} duration={}ms",
                    exchange.getResponse().getStatusCode(),
                    exchange.getRequest().getPath(),
                    duration
            );
        });
    }

    @Override
    public int getOrder() {
        return -2;
    }
}