package com.wellmeet;

import com.wellmeet.member.repository.MemberRepository;
import com.wellmeet.member.repository.MemberRestaurantRepository;
import com.wellmeet.recommend.crawlingreview.repository.VibeRepository;
import com.wellmeet.recommend.menu.repository.MenuRepository;
import com.wellmeet.recommend.restaurant.repository.RestaurantRepository;
import com.wellmeet.recommend.restaurant.tool.CrawlingReviewGenerator;
import com.wellmeet.recommend.review.repository.ReviewRepository;
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

@ExtendWith(DataBaseCleaner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseControllerTest {

    @Autowired
    protected RestaurantRepository restaurantRepository;

    @Autowired
    protected VibeRepository vibeRepository;

    @Autowired
    protected MenuRepository menuRepository;

    @Autowired
    protected ReviewRepository reviewRepository;

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected MemberRestaurantRepository memberRestaurantRepository;

    @Autowired
    protected CrawlingReviewGenerator crawlingReviewGenerator;

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
