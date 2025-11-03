package com.wellmeet.config;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        log.error("Feign error occurred - method: {}, status: {}, reason: {}",
                methodKey, response.status(), response.reason());

        return switch (response.status()) {
            case 400 -> new IllegalArgumentException("잘못된 요청입니다: " + response.reason());
            case 404 -> new IllegalArgumentException("요청한 리소스를 찾을 수 없습니다: " + response.reason());
            case 500 -> new RuntimeException("서버 내부 오류가 발생했습니다: " + response.reason());
            case 503 -> new RuntimeException("서비스를 사용할 수 없습니다: " + response.reason());
            default -> defaultDecoder.decode(methodKey, response);
        };
    }
}
