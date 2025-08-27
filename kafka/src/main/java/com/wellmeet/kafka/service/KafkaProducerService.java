package com.wellmeet.kafka.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMessage(String topic, String key, Object message) {
        try {
            SendResult<String, Object> result = kafkaTemplate.send(topic, key, message).get();
            log.info("동기 메시지 전송 성공: topic={}, key={}, partition={}, offset={}",
                    topic, key, result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
        } catch (Exception e) {
            log.error("동기 메시지 전송 실패: topic={}, key={}, error={}", topic, key, e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Kafka 메시지 전송 실패", e);
        }
    }
}
