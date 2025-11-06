package com.wellmeet.saga.dlq;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeadLetterQueueRepository extends JpaRepository<DeadLetterQueueEntity, Long> {

    List<DeadLetterQueueEntity> findBySagaIdOrderByCreatedAtDesc(String sagaId);

    List<DeadLetterQueueEntity> findByCreatedAtAfterOrderByCreatedAtDesc(LocalDateTime after);

    List<DeadLetterQueueEntity> findByRetryCountLessThanOrderByCreatedAtAsc(int maxRetryCount);
}
