package com.wellmeet;


import com.wellmeet.config.JpaAuditingConfig;
import com.wellmeet.restaurant.tool.CrawlingReviewGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@Import({
        JpaAuditingConfig.class,
        CrawlingReviewGenerator.class
})
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class BaseRepositoryTest {

    @Autowired
    protected CrawlingReviewGenerator crawlingReviewGenerator;
}
