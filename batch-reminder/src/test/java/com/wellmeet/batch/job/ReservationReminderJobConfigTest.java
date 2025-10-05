package com.wellmeet.batch.job;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.wellmeet.batch.config.TestClockConfiguration;
import com.wellmeet.domain.member.entity.Member;
import com.wellmeet.domain.member.repository.MemberRepository;
import com.wellmeet.domain.owner.entity.Owner;
import com.wellmeet.domain.owner.repository.OwnerRepository;
import com.wellmeet.domain.reservation.entity.Reservation;
import com.wellmeet.domain.reservation.repository.ReservationRepository;
import com.wellmeet.domain.restaurant.availabledate.entity.AvailableDate;
import com.wellmeet.domain.restaurant.availabledate.repository.AvailableDateRepository;
import com.wellmeet.domain.restaurant.entity.Restaurant;
import com.wellmeet.domain.restaurant.repository.RestaurantRepository;
import com.wellmeet.kafka.service.KafkaProducerService;
import java.time.Clock;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBatchTest
@SpringBootTest
@ActiveProfiles("test")
@Import(TestClockConfiguration.class)
class ReservationReminderJobConfigTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    @Qualifier("reservationReminderJob")
    private Job reservationReminderJob;

    @Autowired
    private Clock clock;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private AvailableDateRepository availableDateRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @MockitoBean(name = "kafkaProducerService")
    private KafkaProducerService kafkaProducerService;

    private Member member;
    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        jobLauncherTestUtils.setJob(reservationReminderJob);

        reservationRepository.deleteAll();
        availableDateRepository.deleteAll();
        memberRepository.deleteAll();
        restaurantRepository.deleteAll();
        ownerRepository.deleteAll();

        member = new Member("홍길동", "길동이", "test@example.com", "010-1234-5678");
        memberRepository.save(member);

        Owner owner = new Owner("김사장", "010-9999-9999");
        ownerRepository.save(owner);

        restaurant = new Restaurant("rest-123", "맛집", "서울시 강남구", 37.5, 127.0, "thumbnail.jpg", owner);
        restaurantRepository.save(restaurant);
    }

    @Test
    void Job이_성공적으로_실행된다() throws Exception {
        LocalDateTime threeHoursLater = LocalDateTime.now(clock).plusHours(3).plusMinutes(5);
        createConfirmedReservations(threeHoursLater, 5);

        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
        verify(kafkaProducerService, times(5)).sendNotificationMessage(any(), any());
    }

    @Test
    void Step이_올바른_청크_사이즈로_동작한다() throws Exception {
        LocalDateTime threeHoursLater = LocalDateTime.now(clock).plusHours(3).plusMinutes(5);
        createConfirmedReservations(threeHoursLater, 6);

        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
        verify(kafkaProducerService, times(6)).sendNotificationMessage(any(), any());
    }

    @Test
    void 예약이_없을_때_정상_종료된다() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
        verify(kafkaProducerService, times(0)).sendNotificationMessage(any(), any());
    }

    @Test
    void 세_시간_이후_예약만_처리한다() throws Exception {
        LocalDateTime twoHoursLater = LocalDateTime.now(clock).plusHours(2);
        LocalDateTime threeHoursLater = LocalDateTime.now(clock).plusHours(3).plusMinutes(5);
        LocalDateTime fourHoursLater = LocalDateTime.now(clock).plusHours(4);

        createConfirmedReservations(twoHoursLater, 3);
        createConfirmedReservations(threeHoursLater, 5);
        createConfirmedReservations(fourHoursLater, 2);

        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
        verify(kafkaProducerService, times(5)).sendNotificationMessage(any(), any());
    }

    private void createConfirmedReservations(LocalDateTime reservationTime, int count) {
        for (int i = 0; i < count; i++) {
            AvailableDate availableDate = new AvailableDate(
                    reservationTime.toLocalDate(),
                    reservationTime.toLocalTime().plusMinutes(i),
                    10,
                    restaurant
            );
            availableDateRepository.save(availableDate);

            Reservation reservation = new Reservation(
                    restaurant,
                    availableDate,
                    member,
                    4,
                    "요청사항 " + i
            );
            reservation.confirm();
            reservationRepository.save(reservation);
        }
    }
}
