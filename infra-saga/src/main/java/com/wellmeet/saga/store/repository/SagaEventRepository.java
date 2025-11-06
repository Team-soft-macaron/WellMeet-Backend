package com.wellmeet.saga.store.repository;

import com.wellmeet.saga.store.entity.SagaEventEntity;
import com.wellmeet.saga.store.entity.SagaStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SagaEventRepository extends JpaRepository<SagaEventEntity, Long> {

    List<SagaEventEntity> findBySagaIdOrderByEventSequenceAsc(String sagaId);

    List<SagaEventEntity> findBySagaType(String sagaType);

    List<SagaEventEntity> findByStatus(SagaStatus status);

    List<SagaEventEntity> findByAggregateTypeAndAggregateId(String aggregateType, String aggregateId);
}
