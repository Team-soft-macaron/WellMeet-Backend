package com.wellmeet.saga.store.repository;

import com.wellmeet.saga.store.entity.SagaStepEntity;
import com.wellmeet.saga.store.entity.StepStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SagaStepRepository extends JpaRepository<SagaStepEntity, Long> {

    List<SagaStepEntity> findBySagaIdOrderByStepOrderAsc(String sagaId);

    Optional<SagaStepEntity> findBySagaIdAndStepName(String sagaId, String stepName);

    List<SagaStepEntity> findByStatus(StepStatus status);

    List<SagaStepEntity> findBySagaIdAndStatusOrderByStepOrderDesc(String sagaId, StepStatus status);
}
