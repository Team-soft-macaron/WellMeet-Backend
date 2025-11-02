# Phase 5: BFF 전환 + 테스트 마이그레이션 완전 계획

## 전략 결정사항
- 🎯 **순차 진행**: api-owner → api-user
- 🧪 **Service 테스트**: Mock 기반 단위 테스트 (Mockito)
- 🎭 **Controller 테스트**: MockBean으로 Service Mock
- 🗑️ **testFixtures**: 완전히 제거
- ⏳ **Contract Testing**: Phase 6 이후 도입

---

# Phase 5-1: api-owner BFF 전환 + 테스트 마이그레이션

## 예상 시간: 6-7시간

---

## 1단계: Feign 인프라 구축 (1시간)

### 1.1 build.gradle 수정
**파일**: `api-owner/build.gradle`

```gradle
dependencies {
    // Spring Cloud OpenFeign 추가
    implementation 'org.springframework.cloud:spring-cloud-starter-openfeign'
    implementation 'org.springframework.cloud:spring-cloud-starter-netflix-eureka-client'

    // 기존 유지
    implementation project(':domain-common')
    implementation project(':infra-redis')
    implementation project(':infra-kafka')

    // 프로덕션 의존성 - 단계적 제거 예정
    implementation project(':domain-reservation')
    implementation project(':domain-member')
    implementation project(':domain-owner')
    implementation project(':domain-restaurant')

    // ❌ testFixtures 제거 (완전 삭제)
    // testImplementation(testFixtures(project(':domain-reservation')))
    // testImplementation(testFixtures(project(':domain-member')))
    // testImplementation(testFixtures(project(':domain-owner')))
    // testImplementation(testFixtures(project(':domain-restaurant')))

    // 테스트 의존성
    testImplementation 'io.rest-assured:rest-assured'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}
```

### 1.2 Application 클래스 수정
**파일**: `api-owner/src/main/java/com/wellmeet/ApiOwnerApplication.java`

```java
@SpringBootApplication
@EnableFeignClients  // 추가
public class ApiOwnerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiOwnerApplication.class, args);
    }
}
```

### 1.3 application.yml 수정
**파일**: `api-owner/src/main/resources/application.yml`

```yaml
spring:
  application:
    name: api-owner-service

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/

feign:
  client:
    config:
      default:
        connectTimeout: 5000
        readTimeout: 5000
        loggerLevel: BASIC
```

---

## 2단계: Feign Client DTO 생성 (30분)

**디렉토리**: `api-owner/src/main/java/com/wellmeet/client/dto/`

**신규 파일** (5개):
1. `OwnerDTO.java`
2. `RestaurantDTO.java`
3. `ReservationDTO.java`
4. `MemberDTO.java`
5. `AvailableDateDTO.java`

**Request DTO** (3개):
1. `dto/request/MemberIdsRequest.java`
2. `dto/request/UpdateRestaurantRequest.java`
3. `dto/request/CreateReservationDTO.java`

---

## 3단계: Feign Client 인터페이스 생성 (1시간)

**디렉토리**: `api-owner/src/main/java/com/wellmeet/client/`

**신규 파일** (4개):
1. `OwnerClient.java`
2. `RestaurantClient.java`
3. `ReservationClient.java`
4. `MemberClient.java`

---

## 4단계: Feign 설정 클래스 생성 (30분)

**신규 파일** (2개):
1. `api-owner/src/main/java/com/wellmeet/config/FeignConfig.java`
2. `api-owner/src/main/java/com/wellmeet/config/FeignErrorDecoder.java`

---

## 5단계: Service 리팩토링 (1시간)

### 5.1 ReservationService
**파일**: `api-owner/src/main/java/com/wellmeet/reservation/ReservationService.java`

**변경 사항**:
- `ReservationDomainService` → `ReservationClient`
- `MemberDomainService` → `MemberClient`
- `RestaurantDomainService` → `RestaurantClient`
- `EventPublishService` 유지 (Kafka)

### 5.2 RestaurantService
**파일**: `api-owner/src/main/java/com/wellmeet/restaurant/RestaurantService.java`

**변경 사항**:
- `RestaurantDomainService` → `RestaurantClient`
- `EventPublishService` 유지

---

## 6단계: 테스트 마이그레이션 ⭐ (2.5시간)

### 6.1 BaseControllerTest 수정 (30분)
**파일**: `api-owner/src/test/java/com/wellmeet/BaseControllerTest.java`

**변경 전**:
```java
@ExtendWith(DataBaseCleaner.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = RANDOM_PORT)
public abstract class BaseControllerTest {
    @Autowired protected AvailableDateGenerator availableDateGenerator;  // ❌ 제거
    @Autowired protected ReservationGenerator reservationGenerator;      // ❌ 제거
    @Autowired protected MemberGenerator memberGenerator;                // ❌ 제거
    @Autowired protected OwnerGenerator ownerGenerator;                  // ❌ 제거
    @Autowired protected RestaurantGenerator restaurantGenerator;        // ❌ 제거

    @LocalServerPort private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }
}
```

**변경 후**:
```java
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
public abstract class BaseControllerTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    protected RequestSpecification given() {
        return RestAssured.given()
                .port(port)
                .contentType("application/json");
    }
}
```

### 6.2 Service 테스트 마이그레이션 - Mock 패턴 (1시간)

#### ReservationServiceTest 재작성
**파일**: `api-owner/src/test/java/com/wellmeet/reservation/ReservationServiceTest.java`

**변경 전** (testFixtures 사용):
```java
class ReservationServiceTest extends BaseServiceTest {
    @Autowired private ReservationService reservationService;

    @Test
    void 식당_아이디에_해당하는_예약목록을_불러온다() {
        // testFixtures로 실제 데이터 생성
        Owner owner = ownerGenerator.generate("owner1");
        Restaurant restaurant = restaurantGenerator.generate("restaurant1", owner.getId());
        Member member = memberGenerator.generate("member1");
        reservationGenerator.generate(...);

        List<ReservationResponse> result = reservationService.getReservations(restaurant.getId());
        assertThat(result).hasSize(2);
    }
}
```

**변경 후** (Mock 기반):
```java
@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {
    @Mock private ReservationClient reservationClient;
    @Mock private MemberClient memberClient;
    @Mock private RestaurantClient restaurantClient;
    @Mock private EventPublishService eventPublishService;
    @InjectMocks private ReservationService reservationService;

    @Nested
    class GetReservations {

        @Test
        void 식당_아이디에_해당하는_예약목록을_불러온다() {
            String restaurantId = "restaurant-1";

            // Mock 데이터 준비
            List<ReservationDTO> mockReservations = List.of(
                new ReservationDTO(1L, "member-1", restaurantId, 1L, 4, "request", PENDING, now()),
                new ReservationDTO(2L, "member-2", restaurantId, 2L, 2, "request", PENDING, now())
            );

            List<MemberDTO> mockMembers = List.of(
                new MemberDTO("member-1", "name1", "nick1", "email1", "phone1"),
                new MemberDTO("member-2", "name2", "nick2", "email2", "phone2")
            );

            // Mock 동작 설정
            when(reservationClient.getReservationsByRestaurant(restaurantId))
                    .thenReturn(mockReservations);
            when(memberClient.getMembersByIds(any(MemberIdsRequest.class)))
                    .thenReturn(mockMembers);

            // 실행
            List<ReservationResponse> result = reservationService.getReservations(restaurantId);

            // 검증
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getMemberName()).isEqualTo("name1");

            verify(reservationClient).getReservationsByRestaurant(restaurantId);
            verify(memberClient).getMembersByIds(any(MemberIdsRequest.class));
        }
    }

    @Nested
    class ConfirmReservation {

        @Test
        void 예약을_확정한다() {
            Long reservationId = 1L;

            ReservationDTO mockReservation = new ReservationDTO(
                reservationId, "member-1", "restaurant-1", 1L, 4, "request", CONFIRMED, now()
            );
            MemberDTO mockMember = new MemberDTO("member-1", "name", "nick", "email", "phone");
            RestaurantDTO mockRestaurant = new RestaurantDTO("restaurant-1", "name", "address",
                                                              37.5, 127.0, "phone", "thumbnail", "owner-1");

            when(reservationClient.confirmReservation(reservationId))
                    .thenReturn(mockReservation);
            when(memberClient.getMember("member-1"))
                    .thenReturn(mockMember);
            when(restaurantClient.getRestaurant("restaurant-1"))
                    .thenReturn(mockRestaurant);

            reservationService.confirmReservation(reservationId);

            verify(reservationClient).confirmReservation(reservationId);
            verify(eventPublishService).publishReservationConfirmed(any());
        }
    }
}
```

#### RestaurantServiceTest 재작성
**파일**: `api-owner/src/test/java/com/wellmeet/restaurant/RestaurantServiceTest.java`

**새로운 Mock 패턴**:
```java
@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {
    @Mock private RestaurantClient restaurantClient;
    @Mock private EventPublishService eventPublishService;
    @InjectMocks private RestaurantService restaurantService;

    @Test
    void 식당_정보를_수정한다() {
        String restaurantId = "restaurant-1";
        UpdateRestaurantRequest request = new UpdateRestaurantRequest("New Name", "New Address");
        RestaurantDTO updatedRestaurant = new RestaurantDTO(
            restaurantId, "New Name", "New Address", 37.5, 127.0, "phone", "thumbnail", "owner-1"
        );

        when(restaurantClient.updateRestaurant(restaurantId, request))
                .thenReturn(updatedRestaurant);

        RestaurantResponse result = restaurantService.updateRestaurant(restaurantId, request);

        assertThat(result.getName()).isEqualTo("New Name");
        verify(restaurantClient).updateRestaurant(restaurantId, request);
        verify(eventPublishService).publishRestaurantUpdated(any());
    }
}
```

### 6.3 Controller 테스트 마이그레이션 - MockBean 패턴 (1시간)

#### ReservationControllerTest 재작성
**파일**: `api-owner/src/test/java/com/wellmeet/reservation/ReservationControllerTest.java`

**변경 전** (testFixtures 사용):
```java
class ReservationControllerTest extends BaseControllerTest {
    @Test
    void 식당_아이디에_해당하는_예약목록을_불러온다() {
        // testFixtures로 실제 데이터 생성
        Owner owner = ownerGenerator.generate("owner1");
        Restaurant restaurant = restaurantGenerator.generate("restaurant1", owner.getId());

        ReservationResponse[] responses = given()
                .pathParam("restaurantId", restaurant.getId())
                .when().get("/owner/reservation/{restaurantId}")
                .then().statusCode(200)
                .extract().as(ReservationResponse[].class);

        assertThat(responses).hasSize(2);
    }
}
```

**변경 후** (MockBean 패턴):
```java
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
class ReservationControllerTest {

    @LocalServerPort
    private int port;

    @MockBean  // Service를 Mock으로 교체
    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Nested
    class GetReservations {

        @Test
        void 식당_아이디에_해당하는_예약목록을_불러온다() {
            String restaurantId = "restaurant-1";

            // Service Mock 동작 설정
            List<ReservationResponse> mockResponses = List.of(
                createReservationResponse(1L, "member-1"),
                createReservationResponse(2L, "member-2")
            );
            when(reservationService.getReservations(restaurantId))
                    .thenReturn(mockResponses);

            // REST API 호출
            ReservationResponse[] responses = RestAssured.given()
                    .pathParam("restaurantId", restaurantId)
                    .when().get("/owner/reservation/{restaurantId}")
                    .then().statusCode(200)
                    .extract().as(ReservationResponse[].class);

            // 검증
            assertThat(responses).hasSize(2);
            assertThat(responses[0].getId()).isEqualTo(1L);

            verify(reservationService).getReservations(restaurantId);
        }
    }

    private ReservationResponse createReservationResponse(Long id, String memberId) {
        return ReservationResponse.builder()
                .id(id)
                .memberId(memberId)
                .restaurantId("restaurant-1")
                .partySize(4)
                .build();
    }
}
```

#### RestaurantControllerTest 재작성
**파일**: `api-owner/src/test/java/com/wellmeet/restaurant/RestaurantControllerTest.java`

```java
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
class RestaurantControllerTest {

    @LocalServerPort
    private int port;

    @MockBean
    private RestaurantService restaurantService;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void 식당_정보를_수정한다() {
        String restaurantId = "restaurant-1";
        UpdateRestaurantRequest request = new UpdateRestaurantRequest("New Name", "New Address");
        RestaurantResponse mockResponse = RestaurantResponse.builder()
                .id(restaurantId)
                .name("New Name")
                .address("New Address")
                .build();

        when(restaurantService.updateRestaurant(eq(restaurantId), any()))
                .thenReturn(mockResponse);

        RestaurantResponse response = RestAssured.given()
                .contentType("application/json")
                .pathParam("restaurantId", restaurantId)
                .body(request)
                .when().put("/owner/restaurant/{restaurantId}")
                .then().statusCode(200)
                .extract().as(RestaurantResponse.class);

        assertThat(response.getName()).isEqualTo("New Name");
        verify(restaurantService).updateRestaurant(eq(restaurantId), any());
    }
}
```

---

## 7단계: build.gradle 의존성 제거 (10분)

**파일**: `api-owner/build.gradle`

```gradle
dependencies {
    // Feign Client
    implementation 'org.springframework.cloud:spring-cloud-starter-openfeign'
    implementation 'org.springframework.cloud:spring-cloud-starter-netflix-eureka-client'

    // KEEP
    implementation project(':domain-common')
    implementation project(':infra-redis')
    implementation project(':infra-kafka')

    // ❌ REMOVE (4개 프로덕션 의존성 제거)
    // implementation project(':domain-reservation')
    // implementation project(':domain-member')
    // implementation project(':domain-owner')
    // implementation project(':domain-restaurant')

    // ✅ testFixtures 이미 제거됨

    // 테스트
    testImplementation 'io.rest-assured:rest-assured'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}
```

---

## 8단계: 테스트 및 검증 (30분)

```bash
# 빌드
./gradlew :api-owner:clean :api-owner:build

# 테스트
./gradlew :api-owner:test

# Docker Compose 전체 실행
docker-compose up -d

# Eureka 확인
curl http://localhost:8761

# API 테스트
curl http://localhost:8087/owner/reservation/{restaurantId}
```

---

## Phase 5-1 체크리스트

### 인프라
- [ ] build.gradle: OpenFeign 의존성 추가
- [ ] build.gradle: testFixtures 의존성 제거
- [ ] ApiOwnerApplication: @EnableFeignClients
- [ ] application.yml: Eureka 설정

### DTO (5개 + 3개 Request)
- [ ] OwnerDTO, RestaurantDTO, ReservationDTO
- [ ] MemberDTO, AvailableDateDTO
- [ ] MemberIdsRequest, UpdateRestaurantRequest, CreateReservationDTO

### Feign Client (4개)
- [ ] OwnerClient, RestaurantClient
- [ ] ReservationClient, MemberClient

### 설정 (2개)
- [ ] FeignConfig, FeignErrorDecoder

### Service 리팩토링 (2개)
- [ ] ReservationService (Feign Client 사용)
- [ ] RestaurantService (Feign Client 사용)

### 테스트 마이그레이션 ⭐
- [ ] BaseControllerTest: Generator 제거
- [ ] BaseServiceTest: 삭제 또는 완전히 재작성
- [ ] ReservationServiceTest: Mock 패턴으로 재작성
- [ ] RestaurantServiceTest: Mock 패턴으로 재작성
- [ ] ReservationControllerTest: MockBean 패턴으로 재작성
- [ ] RestaurantControllerTest: MockBean 패턴으로 재작성

### 의존성 정리
- [ ] build.gradle: domain-* 4개 제거
- [ ] build.gradle: testFixtures 4개 제거

### 검증
- [ ] 빌드 성공 (domain-* 의존성 없이)
- [ ] 모든 테스트 통과 (Mock 기반)
- [ ] Docker Compose 전체 서비스 작동
- [ ] Eureka 등록 확인
- [ ] Feign 호출 성공

---

# Phase 5-2: api-user BFF 전환 + 테스트 마이그레이션

## 예상 시간: 8-9시간

동일한 패턴이지만 복잡도가 높음 (분산 락, 보상 트랜잭션)

---

## 1단계: Feign 인프라 구축 (1시간)
- build.gradle 수정
- ApiUserApplication: @EnableFeignClients
- application.yml: Eureka 설정
- testFixtures 의존성 제거

---

## 2단계: Feign Client DTO 생성 (1시간)
**신규 파일** (8개 + Request):
- MemberDTO, RestaurantDTO, AvailableDateDTO
- ReservationDTO, FavoriteRestaurantDTO
- ReviewDTO, MenuDTO, BusinessHourDTO
- DecreaseCapacityRequest, IncreaseCapacityRequest 등

---

## 3단계: Feign Client 인터페이스 생성 (1.5시간)
**신규 파일** (5개):
- MemberClient, RestaurantClient
- AvailableDateClient, ReservationClient
- FavoriteRestaurantClient

---

## 4단계: Feign 설정 클래스 (30분)
- FeignConfig, FeignErrorDecoder

---

## 5단계: Service 리팩토링 (1.5시간)

### 5.1 FavoriteService (LOW 복잡도)
- FavoriteRestaurantDomainService → FavoriteRestaurantClient
- RestaurantDomainService → RestaurantClient

### 5.2 RestaurantService (MEDIUM)
- RestaurantDomainService → RestaurantClient
- Batch 조회 최적화

### 5.3 ReservationService (HIGH) ⚠️ CRITICAL
**특별 사항**:
- Redis 분산 락 유지
- Kafka 이벤트 발행 유지
- 보상 트랜잭션 추가

```java
@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationClient reservationClient;
    private final RestaurantClient restaurantClient;
    private final AvailableDateClient availableDateClient;
    private final MemberClient memberClient;
    private final ReservationRedisService redisService;  // KEEP
    private final EventPublishService eventPublishService;  // KEEP

    public CreateReservationResponse reserve(
        String memberId,
        CreateReservationRequest request
    ) {
        // 1. Redis 락
        if (!redisService.isReserving(...)) {
            throw new AlreadyReservingException();
        }

        try {
            // 2. Feign 호출
            MemberDTO member = memberClient.getMember(memberId);
            availableDateClient.decreaseCapacity(...);
            ReservationDTO reservation = reservationClient.create(...);

            // 3. 이벤트 발행
            eventPublishService.publishReservationCreated(reservation);

            return buildResponse(reservation, member);

        } catch (Exception e) {
            // 보상 트랜잭션
            availableDateClient.increaseCapacity(...);
            throw e;
        }
    }
}
```

---

## 6단계: 테스트 마이그레이션 ⭐ (3시간)

### 6.1 BaseControllerTest 수정 (30분)
- Generator 모두 제거
- 간단한 헬퍼 메소드만 유지

### 6.2 BaseServiceTest 삭제 또는 재작성 (30분)
- Mock 기반으로 완전히 재작성
- 또는 삭제하고 개별 테스트에서 직접 Mock 설정

### 6.3 Service 테스트 재작성 - Mock 패턴 (1.5시간)

#### ReservationServiceTest
```java
@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {
    @Mock private ReservationClient reservationClient;
    @Mock private MemberClient memberClient;
    @Mock private AvailableDateClient availableDateClient;
    @Mock private RestaurantClient restaurantClient;
    @Mock private ReservationRedisService redisService;
    @Mock private EventPublishService eventPublishService;
    @InjectMocks private ReservationService reservationService;

    @Test
    void 예약을_생성한다() {
        // Redis 락 Mock
        when(redisService.isReserving(...)).thenReturn(true);
        when(memberClient.getMember("member-1")).thenReturn(mockMember);
        when(availableDateClient.decreaseCapacity(...)).thenReturn(success());
        when(reservationClient.create(...)).thenReturn(mockReservation);

        CreateReservationResponse response = reservationService.reserve("member-1", request);

        verify(redisService).isReserving(...);
        verify(availableDateClient).decreaseCapacity(...);
        verify(reservationClient).create(...);
        verify(eventPublishService).publishReservationCreated(...);
    }

    @Test
    void 예약_실패_시_보상_트랜잭션이_실행된다() {
        when(redisService.isReserving(...)).thenReturn(true);
        when(memberClient.getMember(...)).thenReturn(mockMember);
        when(availableDateClient.decreaseCapacity(...)).thenReturn(success());
        when(reservationClient.create(...)).thenThrow(new RuntimeException());

        assertThatThrownBy(() -> reservationService.reserve("member-1", request))
                .isInstanceOf(RuntimeException.class);

        // 보상 트랜잭션 검증
        verify(availableDateClient).increaseCapacity(...);
    }
}
```

#### RestaurantServiceTest, FavoriteServiceTest
- 동일한 Mock 패턴으로 재작성

### 6.4 Controller 테스트 재작성 - MockBean 패턴 (30분)

#### ReservationControllerTest
```java
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
class ReservationControllerTest {
    @MockBean private ReservationService reservationService;

    @Test
    void 예약을_생성한다() {
        CreateReservationRequest request = new CreateReservationRequest(...);
        CreateReservationResponse mockResponse = CreateReservationResponse.builder()...build();

        when(reservationService.reserve(eq("member-1"), any()))
                .thenReturn(mockResponse);

        CreateReservationResponse response = RestAssured.given()
                .contentType("application/json")
                .header("X-Member-Id", "member-1")
                .body(request)
                .when().post("/user/reservation")
                .then().statusCode(201)
                .extract().as(CreateReservationResponse.class);

        assertThat(response.getId()).isNotNull();
        verify(reservationService).reserve(eq("member-1"), any());
    }
}
```

---

## 7단계: build.gradle 의존성 제거 (10분)

```gradle
dependencies {
    // Feign
    implementation 'org.springframework.cloud:spring-cloud-starter-openfeign'
    implementation 'org.springframework.cloud:spring-cloud-starter-netflix-eureka-client'

    // KEEP
    implementation project(':domain-common')
    implementation project(':infra-redis')
    implementation project(':infra-kafka')

    // ❌ REMOVE
    // implementation project(':domain-reservation')
    // implementation project(':domain-member')
    // implementation project(':domain-owner')
    // implementation project(':domain-restaurant')

    // ❌ testFixtures REMOVE
}
```

---

## 8단계: 테스트 및 검증 (1시간)

```bash
# 빌드 및 테스트
./gradlew :api-user:clean :api-user:build :api-user:test

# Docker Compose 전체
docker-compose up -d

# E2E 테스트
curl -X POST http://localhost:8086/user/reservation \
  -H "Content-Type: application/json" \
  -H "X-Member-Id: member-1" \
  -d '{"restaurantId":"...","availableDateId":1,"partySize":4}'
```

---

## Phase 5-2 체크리스트

### 인프라
- [ ] build.gradle: OpenFeign, testFixtures 제거
- [ ] ApiUserApplication: @EnableFeignClients
- [ ] application.yml: Eureka 설정

### DTO (8개 + Request)
- [ ] 모든 DTO 생성 완료

### Feign Client (5개)
- [ ] 모든 Client 생성 완료

### 설정
- [ ] FeignConfig, FeignErrorDecoder

### Service 리팩토링 (3개)
- [ ] FavoriteService
- [ ] RestaurantService
- [ ] ReservationService (분산 락, 보상 트랜잭션)

### 테스트 마이그레이션
- [ ] BaseControllerTest: Generator 제거
- [ ] BaseServiceTest: 삭제
- [ ] ReservationServiceTest: Mock + 보상 트랜잭션 테스트
- [ ] RestaurantServiceTest: Mock
- [ ] FavoriteServiceTest: Mock
- [ ] Controller 테스트: MockBean 패턴

### 의존성 정리
- [ ] build.gradle: domain-* 4개 제거
- [ ] build.gradle: testFixtures 4개 제거

### 검증
- [ ] 빌드 성공
- [ ] 모든 테스트 통과
- [ ] Redis 분산 락 작동
- [ ] Kafka 이벤트 발행
- [ ] 보상 트랜잭션 작동

---

## 전체 Phase 5 완료 기준

✅ **api-owner, api-user 모두**:
- domain-* 프로덕션 의존성 제거 완료
- testFixtures 테스트 의존성 제거 완료
- Feign Client로 완전 전환
- 모든 테스트 Mock 기반으로 전환

✅ **테스트**:
- Service: Mock 기반 단위 테스트
- Controller: MockBean + REST Assured
- 총 테스트 수: 기존과 동일 (약 20개)
- 실행 속도: 3-5배 빠름 (DB 접근 없음)

✅ **시스템**:
- Docker Compose 전체 정상 작동
- Eureka 등록 확인
- Feign 호출 성공
- Redis, Kafka 정상 작동

---

## 예상 소요 시간

| Phase | 시간 |
|-------|------|
| Phase 5-1 (api-owner) | 6-7시간 |
| Phase 5-2 (api-user) | 8-9시간 |
| **총 예상 시간** | **14-16시간 (약 2일)** |

---

## 문서 업데이트
- [ ] claudedocs/microservices-migration-plan.md: Phase 5 완료 표시
- [ ] CLAUDE.md: BFF 테스트 전략 업데이트 (Mock 패턴)

---

**문서 작성일**: 2025-11-02
**작성자**: Claude Code Agent
**프로젝트**: WellMeet-Backend Phase 5 BFF Migration