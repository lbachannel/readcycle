package com.anlb.readcycle.utils.response;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.anlb.readcycle.domain.response.ResultResponse;

import jakarta.servlet.http.HttpServletResponse;

@ControllerAdvice
public class FormatResultResponse implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
            ServerHttpResponse response) {
        HttpServletResponse servletResponse = ((ServletServerHttpResponse) response).getServletResponse();
        int status = servletResponse.getStatus();

        ResultResponse<Object> restResponse = new ResultResponse<>();
        restResponse.setStatusCode(status);
        if (body instanceof String) {
            return body;
        }
        if (status >= 400) {
            return body;
        }
        restResponse.setMessage("Call API successfully!");
        restResponse.setData(body);
        return restResponse;
    }
    
}
