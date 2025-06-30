package com.wellmeet;


import com.wellmeet.config.JpaAuditingConfig;
import com.wellmeet.restaurant.domain.crawlingreview.domain.Vibe;
import com.wellmeet.restaurant.domain.crawlingreview.domain.VibeName;
import com.wellmeet.restaurant.repository.crawlingreview.repository.VibeRepository;
import com.wellmeet.restaurant.tool.CrawlingReviewGenerator;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
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

    @Autowired
    protected VibeRepository vibeRepository;

    @BeforeEach
    void setEnvironment() {
        Arrays.stream(VibeName.values())
                .forEach(vibeName -> vibeRepository.save(new Vibe(vibeName.name())));
    }
}
