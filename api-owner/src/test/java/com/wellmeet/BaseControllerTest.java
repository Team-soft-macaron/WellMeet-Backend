package com.wellmeet;

import com.wellmeet.domain.fixture.AvailableDateGenerator;
import com.wellmeet.domain.fixture.BusinessHourGenerator;
import com.wellmeet.domain.fixture.MemberGenerator;
import com.wellmeet.domain.fixture.OwnerGenerator;
import com.wellmeet.domain.fixture.ReservationGenerator;
import com.wellmeet.domain.fixture.RestaurantGenerator;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(DataBaseCleaner.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseControllerTest {

    @Autowired
    protected OwnerGenerator ownerGenerator;

    @Autowired
    protected RestaurantGenerator restaurantGenerator;

    @Autowired
    protected ReservationGenerator reservationGenerator;

    @Autowired
    protected MemberGenerator memberGenerator;

    @Autowired
    protected AvailableDateGenerator availableDateGenerator;

    @Autowired
    protected BusinessHourGenerator businessHourGenerator;

    @LocalServerPort
    private int port;

    private RequestSpecification spec;

    @BeforeEach
    void setEnvironment() {
        RestAssured.port = port;
        spec = new RequestSpecBuilder()
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .build();
    }

    protected RequestSpecification given() {
        return RestAssured.given(spec);
    }
}
