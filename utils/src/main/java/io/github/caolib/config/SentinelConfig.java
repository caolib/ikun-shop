package io.github.caolib.config;


import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import io.github.caolib.domain.R;
import io.github.caolib.enums.E;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static io.github.caolib.utils.LogUtil.logErr;


/**
 * sentinel配置
 */
@Slf4j
@Configuration
public class SentinelConfig implements BlockExceptionHandler {
    /**
     * 统一处理限流返回响应
     */
    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, BlockException e) throws Exception {
        String msg = E.FLOW_LIMIT;
        logErr(e, msg);
        httpServletResponse.setStatus(429);
        R<Object> error = R.error(429, msg);
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        httpServletResponse.getWriter().write(JSON.toJSONString(error));
    }
}
