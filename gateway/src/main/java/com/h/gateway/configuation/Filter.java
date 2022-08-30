package com.h.gateway.configuation;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

//解决请求头丢失问题
@Component
public class Filter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        System.out.println("filter");
        ServerHttpRequest request = exchange.getRequest();
        String token = request.getHeaders().getFirst("token");
        ServerHttpRequest req = request.mutate().build();
        ServerWebExchange build = exchange.mutate().request(req).build();
        System.out.println(token);
        if (token != null) {
            req = request.mutate().headers(header -> header.add("authorize", token)).build();
            build = exchange.mutate().request(req).build();
        }
        return chain.filter(exchange);
    }


}
