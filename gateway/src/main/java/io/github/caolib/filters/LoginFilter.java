package io.github.caolib.filters;

import io.github.caolib.config.AuthProperties;
import io.github.caolib.domain.dto.UserInfo;
import io.github.caolib.enums.Auth;
import io.github.caolib.exception.UnauthorizedException;
import io.github.caolib.utils.JwtTool;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class LoginFilter implements GlobalFilter, Ordered {
    private final AuthProperties authProperties;
    private final JwtTool jwtTool;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取请求路径
        ServerHttpRequest request = exchange.getRequest();
        RequestPath path = request.getPath();

        // 如果是不需要校验的路径则直接放行
        if (isExcludePath(path.toString())) return chain.filter(exchange);

        // 取出请求头中的token
        String token = request.getHeaders().getFirst(Auth.KEY);

        UserInfo userInfo;
        try {
            userInfo = jwtTool.parseToken(token); // 解析token
        } catch (UnauthorizedException e) {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED); // 设置状态码为401
            return response.setComplete(); // 拦截请求
        }

        // 将用户信息放入请求头中
        String userId = userInfo.getUserId().toString();
        String identity = userInfo.getIdentity();

        ServerWebExchange newRequest = exchange.mutate()
                .request(builder -> builder.header(Auth.USER_ID, userId).header(Auth.USER_IDENTITY, identity))
                .build();

        return chain.filter(newRequest); // 放行
    }

    // 判断路径匹配
    private boolean isExcludePath(String path) {
        for (String excludePath : authProperties.getExcludePaths())
            if (pathMatcher.match(excludePath, path)) return true;
        return false;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
