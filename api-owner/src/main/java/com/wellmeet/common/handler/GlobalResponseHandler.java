package com.wellmeet.common.handler;

import com.wellmeet.common.dto.ApiResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 전역 응답 처리기
 * 모든 REST API 응답을 ApiResponse로 자동 래핑합니다.
 */
@RestControllerAdvice(basePackages = "com.wellmeet")
public class GlobalResponseHandler implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // ApiResponse로 이미 래핑된 응답이나 에러 응답은 제외
        return !returnType.getParameterType().equals(ApiResponse.class) &&
               !returnType.getDeclaringClass().getPackage().getName().contains("error");
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        
        // 이미 ApiResponse인 경우는 그대로 반환
        if (body instanceof ApiResponse) {
            return body;
        }
        
        // String 응답은 래핑하지 않음 (Swagger 등의 응답)
        if (body instanceof String) {
            return body;
        }
        
        // 성공 응답으로 래핑
        return ApiResponse.success(body);
    }
}