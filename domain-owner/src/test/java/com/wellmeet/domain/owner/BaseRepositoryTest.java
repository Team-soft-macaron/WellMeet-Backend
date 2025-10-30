package com.wellmeet.domain.owner;

import com.wellmeet.domain.config.JpaAuditingConfig;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Import({
        JpaAuditingConfig.class,
})
@ExtendWith(DataBaseCleaner.class)
@DataJpaTest
@ActiveProfiles("domain-test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class BaseRepositoryTest {
}
