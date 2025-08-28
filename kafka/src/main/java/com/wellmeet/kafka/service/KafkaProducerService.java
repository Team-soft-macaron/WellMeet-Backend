package com.wellmeet.kafka.service;

import com.wellmeet.kafka.dto.MessageHeader;
import com.wellmeet.kafka.dto.NotificationInfo;
import com.wellmeet.kafka.dto.NotificationMessage;
import com.wellmeet.kafka.dto.NotificationPayload;
import com.wellmeet.kafka.dto.NotificationType;
import com.wellmeet.kafka.exception.KafkaErrorCode;
import com.wellmeet.kafka.exception.WellMeetKafkaException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private static final String MESSAGE_VERSION = "1.0";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendNotificationMessage(String recipient, NotificationPayload payload) {
        NotificationType type = NotificationType.RESERVATION_CREATED;
        MessageHeader header = new MessageHeader(MESSAGE_VERSION, type.getSource());

        NotificationInfo notification = new NotificationInfo(type.getName(), type.getCategory(), recipient);

        NotificationMessage message = new NotificationMessage(header, notification, payload);
        sendMessage(type.getTopic(), recipient, message);
    }

    public void sendMessage(String topic, String key, NotificationMessage message) {
        try {
            SendResult<String, Object> result = kafkaTemplate.send(topic, key, message).get();
            log.info("동기 메시지 전송 성공: topic={}, key={}, partition={}, offset={}",
                    topic, key, result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
        } catch (Exception e) {
            log.error("동기 메시지 전송 실패: topic={}, key={}, error={}", topic, key, e.getMessage());
            throw new WellMeetKafkaException(KafkaErrorCode.KAFKA_PRODUCER_ERROR);
        }
    }
}
