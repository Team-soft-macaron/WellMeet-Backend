package com.wellmeet.global.controller;

import com.wellmeet.BaseControllerTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class HealthCheckControllerTest extends BaseControllerTest {

    @Nested
    class HealthCheck {

        @Test
        void 헬스체크_엔드포인트가_정상적으로_응답한다() {
            given()
                    .when().get("/health")
                    .then().statusCode(HttpStatus.OK.value());
        }
    }
}
