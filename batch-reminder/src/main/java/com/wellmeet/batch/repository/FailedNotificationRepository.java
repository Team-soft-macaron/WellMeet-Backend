package com.wellmeet.batch.repository;

import com.wellmeet.batch.entity.FailedNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FailedNotificationRepository extends JpaRepository<FailedNotification, Long> {
}
