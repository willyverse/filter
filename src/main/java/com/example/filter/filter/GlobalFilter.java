package com.example.filter.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@WebFilter(urlPatterns = "/api/user/*")
public class GlobalFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 전처리 (Request)

        // 그냥 HttpServletRequest 클래스를 사용해도 되지만, 이런 경우 BufferedReader에서 오류가 발생함 (이미 한 번 읽었기 때문에 안된다나 뭐라나,,)
        // 그래서 이걸 캐싱해주는 클래스가 ContentCachingRequestWrapper이고, 그냥 이걸 사용하면 문제 없음
        ContentCachingRequestWrapper httpServletRequest = new ContentCachingRequestWrapper((HttpServletRequest) request);
        ContentCachingResponseWrapper httpServletResponse = new ContentCachingResponseWrapper((HttpServletResponse) response);

        chain.doFilter(httpServletRequest, httpServletResponse); // 이걸 기준으로 위쪽이 전처리, 아래쪽이 후처리

        String url = httpServletRequest.getRequestURI();

        String reqContent = new String(httpServletRequest.getContentAsByteArray());
        log.info("request url: {}, request body: {}", url, reqContent);

        String resContent = new String(httpServletResponse.getContentAsByteArray());
        int httpStatusCode = httpServletResponse.getStatus();

        httpServletResponse.copyBodyToResponse();

        log.info("response status: {}, response body: {}", httpStatusCode, resContent);
    }
}
