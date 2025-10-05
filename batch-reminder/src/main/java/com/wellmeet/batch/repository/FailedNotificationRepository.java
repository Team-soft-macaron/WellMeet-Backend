package com.wellmeet.batch.repository;

import com.wellmeet.batch.entity.FailedNotification;
import com.wellmeet.batch.entity.FailedNotificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FailedNotificationRepository extends JpaRepository<FailedNotification, Long> {

    Page<FailedNotification> findByStatusOrderByFailureTimeAsc(
            FailedNotificationStatus status,
            Pageable pageable
    );
}
