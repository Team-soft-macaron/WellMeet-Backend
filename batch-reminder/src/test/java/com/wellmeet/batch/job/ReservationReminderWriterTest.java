package com.wellmeet.batch.job;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.wellmeet.kafka.dto.payload.ReservationReminderPayload;
import com.wellmeet.kafka.service.KafkaProducerService;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.Chunk;

@ExtendWith(MockitoExtension.class)
class ReservationReminderWriterTest {

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private ReservationReminderWriter writer;

    @Nested
    class Write {

        @Test
        void Kafka로_알림_메시지를_발송한다() {
            ReservationReminderPayload payload = new ReservationReminderPayload(
                    1L,
                    "member-123",
                    "홍길동",
                    "맛집",
                    LocalDateTime.of(2025, 10, 5, 18, 0),
                    4
            );

            Chunk<ReservationReminderPayload> chunk = new Chunk<>(List.of(payload));

            writer.write(chunk);

            verify(kafkaProducerService, times(1))
                    .sendNotificationMessage("member-123", payload);
        }

        @Test
        void 청크의_모든_예약에_대해_알림을_발송한다() {
            ReservationReminderPayload payload1 = new ReservationReminderPayload(
                    1L, "member-1", "김철수", "한식당", LocalDateTime.now(), 2
            );
            ReservationReminderPayload payload2 = new ReservationReminderPayload(
                    2L, "member-2", "이영희", "양식당", LocalDateTime.now(), 4
            );
            ReservationReminderPayload payload3 = new ReservationReminderPayload(
                    3L, "member-3", "박민수", "일식당", LocalDateTime.now(), 6
            );

            Chunk<ReservationReminderPayload> chunk = new Chunk<>(Arrays.asList(payload1, payload2, payload3));

            writer.write(chunk);

            verify(kafkaProducerService, times(3)).sendNotificationMessage(any(), any());
            verify(kafkaProducerService).sendNotificationMessage("member-1", payload1);
            verify(kafkaProducerService).sendNotificationMessage("member-2", payload2);
            verify(kafkaProducerService).sendNotificationMessage("member-3", payload3);
        }

        @Test
        void 발송_실패시_로그만_남기고_계속_진행한다() {
            ReservationReminderPayload payload1 = new ReservationReminderPayload(
                    1L, "member-1", "김철수", "한식당", LocalDateTime.now(), 2
            );
            ReservationReminderPayload payload2 = new ReservationReminderPayload(
                    2L, "member-2", "이영희", "양식당", LocalDateTime.now(), 4
            );

            doThrow(new RuntimeException("Kafka error"))
                    .when(kafkaProducerService).sendNotificationMessage(eq("member-1"), any());

            Chunk<ReservationReminderPayload> chunk = new Chunk<>(Arrays.asList(payload1, payload2));

            writer.write(chunk);

            verify(kafkaProducerService, times(2)).sendNotificationMessage(any(), any());
        }

        @Test
        void 빈_청크에_대해서도_정상적으로_처리한다() {
            Chunk<ReservationReminderPayload> emptyChunk = new Chunk<>();

            writer.write(emptyChunk);

            verify(kafkaProducerService, times(0)).sendNotificationMessage(any(), any());
        }

        @Test
        void 열_개_청크를_모두_발송한다() {
            Chunk<ReservationReminderPayload> chunk = new Chunk<>();
            for (int i = 1; i <= 10; i++) {
                chunk.add(new ReservationReminderPayload(
                        (long) i,
                        "member-" + i,
                        "고객" + i,
                        "식당" + i,
                        LocalDateTime.now(),
                        2
                ));
            }

            writer.write(chunk);

            verify(kafkaProducerService, times(10)).sendNotificationMessage(any(), any());
        }
    }
}
