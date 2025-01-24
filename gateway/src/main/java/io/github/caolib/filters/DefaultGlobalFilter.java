package io.github.caolib.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class DefaultGlobalFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        String path = request.getPath().toString();
        String referer = request.getHeaders().getFirst("Referer");
        if(!path.endsWith("/health")) {
            log.debug("{} ==> {}", referer,path);
        }

        // 放行
        return chain.filter(exchange);
    }

    // 过滤器的优先级，数字越小，优先级越高
    @Override
    public int getOrder() {
        return 100;
    }
}
