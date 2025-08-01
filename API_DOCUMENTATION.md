# WellMeet 레스토랑 사장님 앱 API 문서

## 개요

이 문서는 WellMeet 레스토랑 사장님용 웹 애플리케이션에서 필요한 모든 API 엔드포인트를 정리한 것입니다.

## API 엔드포인트 목록

### 1. 인증 (Authentication)

#### POST `/owner/auth/login`

**설명**: 사장님 로그인

**Request Body**:

```typescript
{
    email: string;       // 이메일 주소 (예: "owner@restaurant.com")
    password: string;    // 비밀번호
}
```

**Response**:

```typescript
{
    token: string;       // JWT 액세스 토큰
    refreshToken: string; // 리프레시 토큰
    user: {
        id: number;        // 사용자 ID
        name: string;      // 사용자 이름
        email: string;     // 이메일
        restaurantId: number; // 레스토랑 ID
        role: string;      // 권한 (예: "owner", "manager")
    }
}
```

#### POST `/owner/auth/logout`

**설명**: 로그아웃

**Response**:

```typescript
{
    message: string;     // "로그아웃되었습니다."
}
```

#### POST `/owner/auth/refresh`

**설명**: 액세스 토큰 갱신

**Request Body**:

```typescript
{
    refreshToken: string; // 리프레시 토큰
}
```

**Response**:

```typescript
{
    token: string;       // 새로운 JWT 액세스 토큰
    refreshToken: string; // 새로운 리프레시 토큰
}
```

### 2. 대시보드 (Dashboard)

#### GET `/owner/dashboard/today-stats`

**설명**: 오늘의 예약 통계 조회

**Response**:

```typescript
{
    todayBookings: number;      // 오늘 전체 예약 수
    confirmedBookings: number;  // 확정된 예약 수
    pendingBookings: number;    // 대기 중인 예약 수
    expectedRevenue: number;    // 예상 매출 (원)
}
```

#### GET `/owner/dashboard/recent-bookings`

**설명**: 최근 예약 목록 조회

**Query Parameters**:

```typescript
{
    limit ? : number;  // 조회할 예약 수 (기본값: 10, 최대: 50)
}
```

**Response**:

```typescript
[
    {
        id: number;              // 예약 ID
        customer: {
            id: number;            // 고객 ID
            name: string;          // 고객명
            phone: string;         // 전화번호
            isVip: boolean;        // VIP 여부
        };
        time: string;            // 예약 시간 (ISO 8601)
        party: number;           // 인원 수
        status: "pending" | "confirmed" | "completed" | "cancelled"; // 예약 상태
        special: string | null;  // 특별 요청사항
        tableNumber: number | null; // 배정된 테이블 번호
    }
]
```

#### GET `/owner/dashboard/kpi`

**설명**: 주요 성과 지표 조회

**Response**:

```typescript
{
    bookingCount: {
        value: number;           // 이번 달 예약 수
        change: number;          // 전월 대비 변화율 (%)
    }
    ;
    revenue: {
        value: number;           // 이번 달 매출 (원)
        change: number;          // 전월 대비 변화율 (%)
    }
    ;
    avgPartySize: {
        value: number;           // 평균 예약 인원
        change: number;          // 전월 대비 변화율 (%)
    }
    ;
    satisfactionScore: {
        value: number;           // 고객 만족도 (0-5)
        change: number;          // 전월 대비 변화율 (%)
    }
    ;
}
```

#### GET `/owner/dashboard/time-slots`

**설명**: 특정 날짜의 시간대별 예약 현황

**Query Parameters**:

```typescript
{
    date: string;  // 조회할 날짜 (YYYY-MM-DD)
}
```

**Response**:

```typescript
[
    {
        time: string;            // 시간 (HH:mm)
        reservations: number;    // 예약 수
        capacity: number;        // 최대 수용 가능 수
        available: boolean;      // 예약 가능 여부
    }
]
```

### 3. 예약 관리 (Booking Management)

#### GET `/owner/bookings`

**설명**: 예약 목록 조회

**Query Parameters**:

```typescript
{
    date ? : string;     // 예약 날짜 (YYYY-MM-DD)
    status ? : "pending" | "confirmed" | "completed" | "cancelled"; // 예약 상태
    page ? : number;     // 페이지 번호 (기본값: 1)
    limit ? : number;    // 페이지당 항목 수 (기본값: 20)
}
```

**Response**:

```typescript
{
    bookings: [
        {
            id: number;
            customer: {
                id: number;
                name: string;
                phone: string;
                email? : string;
                isVip: boolean;
            };
            date: string;          // 예약 날짜 (YYYY-MM-DD)
            time: string;          // 예약 시간 (HH:mm)
            party: number;         // 인원 수
            status: string;        // 예약 상태
            tableNumber? : number;  // 배정된 테이블
            note? : string;         // 메모
            createdAt: string;     // 생성일시 (ISO 8601)
            updatedAt: string;     // 수정일시 (ISO 8601)
        }
    ];
    total: number;             // 전체 예약 수
    page: number;              // 현재 페이지
    totalPages: number;        // 전체 페이지 수
}
```

#### GET `/owner/bookings/:id`

**설명**: 예약 상세 조회

**Path Parameters**:

```typescript
{
    id: number;  // 예약 ID
}
```

**Response**:

```typescript
{
    booking: {
        id: number;
        customer: {
            id: number;
            name: string;
            phone: string;
            email ? : string;
            isVip: boolean;
            visitCount: number;      // 방문 횟수
            lastVisit ? : string;      // 마지막 방문일
            preferences ? : string;    // 선호사항
            allergies ? : string;      // 알레르기 정보
        }
        ;
        date: string;
        time: string;
        party: number;
        status: string;
        tableNumber ? : number;
        specialRequests ? : string;
        orderHistory ? : [          // 주문 내역
            {
                menuItem: string;
                quantity: number;
                price: number;
            }
        ];
        totalAmount ? : number;      // 총 주문 금액
        deposit ? : number;          // 예약금
        createdAt: string;
        updatedAt: string;
        createdBy ? : string;        // 예약 생성자
    }
}
```

#### PATCH `/owner/bookings/:id/status`

**설명**: 예약 상태 변경

**Path Parameters**:

```typescript
{
    id: number;  // 예약 ID
}
```

**Request Body**:

```typescript
{
    status: "pending" | "confirmed" | "completed" | "cancelled"; // 변경할 상태
    reason ? : string;     // 상태 변경 사유 (취소 시 필수)
}
```

**Response**:

```typescript
{
    booking: {
        // 업데이트된 예약 정보 (GET /owner/bookings/:id와 동일한 구조)
    }
}
```

#### PATCH `/owner/bookings/:id`

**설명**: 예약 정보 수정

**Path Parameters**:

```typescript
{
    id: number;  // 예약 ID
}
```

**Request Body**:

```typescript
{
    time ? : string;       // 시간 (HH:mm)
    party ? : number;      // 인원 수
    tableNumber ? : number; // 테이블 번호
    note ? : string;       // 메모
}
```

**Response**:

```typescript
{
    booking: {
        // 업데이트된 예약 정보
    }
}
```

#### DELETE `/owner/bookings/:id`

**설명**: 예약 취소

**Path Parameters**:

```typescript
{
    id: number;  // 예약 ID
}
```

**Response**:

```typescript
{
    message: string;  // "예약이 취소되었습니다."
}
```

#### GET `/owner/bookings/search`

**설명**: 예약 검색

**Query Parameters**:

```typescript
{
    q: string;  // 검색어 (고객명, 전화번호)
}
```

**Response**:

```typescript
{
    bookings: [
        // 예약 목록 (GET /owner/bookings와 동일한 구조)
    ]
}
```

### 4. 고객 관리 (Customer Management)

#### GET `/owner/customers`

**설명**: 고객 목록 조회

**Query Parameters**:

```typescript
{
    page ? : number;     // 페이지 번호 (기본값: 1)
    limit ? : number;    // 페이지당 항목 수 (기본값: 20)
    sort ? : string;     // 정렬 기준 (예: "visitCount:desc", "name:asc")
    isVip ? : boolean;   // VIP 고객만 필터링
}
```

**Response**:

```typescript
{
    customers: [
        {
            id: number;
            name: string;
            phone: string;
            email? : string;
            isVip: boolean;
            visitCount: number;
            lastVisit? : string;      // ISO 8601
            totalSpent: number;      // 총 사용 금액
            averagePartySize: number; // 평균 예약 인원
            tags? : string[];         // 태그 (예: ["단골", "알레르기"])
            createdAt: string;
        }
    ];
    total: number;
    page: number;
    totalPages: number;
}
```

#### GET `/owner/customers/:id`

**설명**: 고객 상세 조회

**Path Parameters**:

```typescript
{
    id: number;  // 고객 ID
}
```

**Response**:

```typescript
{
    customer: {
        id: number;
        name: string;
        phone: string;
        email ? : string;
        isVip: boolean;
        visitCount: number;
        lastVisit ? : string;
        totalSpent: number;
        averagePartySize: number;
        preferences ? : string;      // 선호사항
        allergies ? : string;        // 알레르기 정보
        birthday ? : string;         // 생일 (YYYY-MM-DD)
        notes ? : string;            // 메모
        tags ? : string[];
        createdAt: string;
        updatedAt: string;
    }
}
```

#### POST `/owner/customers`

**설명**: 고객 등록

**Request Body**:

```typescript
{
    name: string;        // 고객명 (필수)
    phone: string;       // 전화번호 (필수)
    email ? : string;      // 이메일
    birthday ? : string;   // 생일 (YYYY-MM-DD)
    preferences ? : string; // 선호사항
    allergies ? : string;  // 알레르기 정보
    notes ? : string;      // 메모
    tags ? : string[];     // 태그
}
```

**Response**:

```typescript
{
    customer: {
        // 생성된 고객 정보
    }
}
```

#### PATCH `/owner/customers/:id`

**설명**: 고객 정보 수정

**Path Parameters**:

```typescript
{
    id: number;  // 고객 ID
}
```

**Request Body**:

```typescript
{
    name ? : string;
    phone ? : string;
    email ? : string;
    preferences ? : string;
    allergies ? : string;
    birthday ? : string;
    notes ? : string;
    tags ? : string[];
}
```

**Response**:

```typescript
{
    customer: {
        // 업데이트된 고객 정보
    }
}
```

#### DELETE `/owner/customers/:id`

**설명**: 고객 삭제

**Path Parameters**:

```typescript
{
    id: number;  // 고객 ID
}
```

**Response**:

```typescript
{
    message: string;  // "고객이 삭제되었습니다."
}
```

#### GET `/owner/customers/:id/bookings`

**설명**: 고객 예약 이력 조회

**Path Parameters**:

```typescript
{
    id: number;  // 고객 ID
}
```

**Response**:

```typescript
{
    bookings: [
        {
            id: number;
            date: string;
            time: string;
            party: number;
            status: string;
            tableNumber? : number;
            totalAmount? : number;
            createdAt: string;
        }
    ]
}
```

#### GET `/owner/customers/:id/reviews`

**설명**: 고객 리뷰 이력 조회

**Path Parameters**:

```typescript
{
    id: number;  // 고객 ID
}
```

**Response**:

```typescript
{
    reviews: [
        {
            id: number;
            rating: number;          // 1-5
            comment? : string;
            reply? : string;          // 사장님 답글
            createdAt: string;
            bookingId: number;       // 관련 예약 ID
        }
    ]
}
```

#### PATCH `/owner/customers/:id/vip`

**설명**: VIP 상태 변경

**Path Parameters**:

```typescript
{
    id: number;  // 고객 ID
}
```

**Request Body**:

```typescript
{
    isVip: boolean;  // VIP 상태
}
```

**Response**:

```typescript
{
    customer: {
        // 업데이트된 고객 정보
    }
}
```

### 5. 리뷰 관리 (Review Management)

#### GET `/owner/reviews`

**설명**: 리뷰 목록 조회

**Query Parameters**:

```typescript
{
    page ? : number;     // 페이지 번호 (기본값: 1)
    limit ? : number;    // 페이지당 항목 수 (기본값: 20)
    rating ? : number;   // 별점 필터 (1-5)
    hasReply ? : boolean; // 답글 유무 필터
}
```

**Response**:

```typescript
{
    reviews: [
        {
            id: number;
            customer: {
                id: number;
                name: string;
            };
            booking: {
                id: number;
                date: string;
                party: number;
            };
            rating: number;          // 1-5
            comment? : string;
            reply? : {
                content: string;
                createdAt: string;
                updatedAt? : string;
            };
            images? : string[];       // 리뷰 이미지 URL 배열
            createdAt: string;
        }
    ];
    total: number;
    page: number;
    totalPages: number;
}
```

#### GET `/owner/reviews/stats`

**설명**: 리뷰 통계 조회

**Response**:

```typescript
{
    avgRating: number;           // 평균 별점 (소수점 1자리)
    totalCount: number;          // 전체 리뷰 수
    distribution: {              // 별점별 분포
        "1"
    :
        number;
        "2"
    :
        number;
        "3"
    :
        number;
        "4"
    :
        number;
        "5"
    :
        number;
    }
    ;
    replyRate: number;           // 답글 작성률 (%)
    recentTrend: "up" | "down" | "stable"; // 최근 추세
}
```

#### POST `/owner/reviews/:id/reply`

**설명**: 리뷰 답글 작성

**Path Parameters**:

```typescript
{
    id: number;  // 리뷰 ID
}
```

**Request Body**:

```typescript
{
    reply: string;  // 답글 내용 (최대 500자)
}
```

**Response**:

```typescript
{
    review: {
        // 업데이트된 리뷰 정보
    }
}
```

#### PATCH `/owner/reviews/:id/reply`

**설명**: 리뷰 답글 수정

**Path Parameters**:

```typescript
{
    id: number;  // 리뷰 ID
}
```

**Request Body**:

```typescript
{
    reply: string;  // 수정할 답글 내용
}
```

**Response**:

```typescript
{
    review: {
        // 업데이트된 리뷰 정보
    }
}
```

#### DELETE `/owner/reviews/:id/reply`

**설명**: 리뷰 답글 삭제

**Path Parameters**:

```typescript
{
    id: number;  // 리뷰 ID
}
```

**Response**:

```typescript
{
    message: string;  // "답글이 삭제되었습니다."
}
```

### 6. 통계 분석 (Analytics)

#### GET `/owner/analytics/dashboard`

**설명**: 대시보드 통계 데이터

**Query Parameters**:

```typescript
{
    period: "week" | "month" | "quarter";  // 조회 기간
    startDate ? : string;  // 시작일 (YYYY-MM-DD)
    endDate ? : string;    // 종료일 (YYYY-MM-DD)
}
```

**Response**:

```typescript
{
    stats: {
        totalBookings: number;
        totalRevenue: number;
        avgBookingValue: number;
        cancelRate: number;        // 취소율 (%)
        peakTime: string;          // 피크 시간대
        popularTable: number;      // 인기 테이블 번호
        returningCustomerRate: number; // 재방문율 (%)
    }
}
```

#### GET `/owner/analytics/weekly-data`

**설명**: 요일별 예약 및 매출 데이터

**Query Parameters**:

```typescript
{
    startDate: string;  // 시작일 (YYYY-MM-DD)
    endDate: string;    // 종료일 (YYYY-MM-DD)
}
```

**Response**:

```typescript
[
    {
        day: string;               // 요일 (예: "월")
        date: string;              // 날짜 (YYYY-MM-DD)
        reservations: number;      // 예약 수
        revenue: number;           // 매출
        avgPartySize: number;      // 평균 인원
    }
]
```

#### GET `/owner/analytics/customer-types`

**설명**: 고객 유형 분포

**Response**:

```typescript
[
    {
        name: string;      // 유형명 (예: "신규", "단골", "VIP")
        value: number;     // 고객 수
        percentage: number; // 비율 (%)
        color: string;     // 차트 색상 (hex)
    }
]
```

#### GET `/owner/analytics/time-slots`

**설명**: 시간대별 예약 분포

**Response**:

```typescript
[
    {
        time: string;      // 시간대 (예: "11:00-12:00")
        bookings: number;  // 예약 수
        percentage: number; // 비율 (%)
    }
]
```

#### GET `/owner/analytics/revenue-trend`

**설명**: 매출 추이

**Query Parameters**:

```typescript
{
    period: "day" | "week" | "month";  // 집계 단위
    months ? : number;  // 조회할 개월 수 (기본값: 6)
}
```

**Response**:

```typescript
[
    {
        date: string;      // 날짜
        revenue: number;   // 매출
        bookings: number;  // 예약 수
        avgValue: number;  // 평균 객단가
    }
]
```

### 7. 매장 관리 (Restaurant Management)

#### GET `/owner/restaurant/info`

**설명**: 매장 정보 조회

**Response**:

```typescript
{
    restaurant: {
        id: number;
        name: string;
        category: string;          // 음식 카테고리
        address: {
            street: string;
            city: string;
            state: string;
            zipCode: string;
        }
        ;
        phone: string;
        email: string;
        description: string;
        images: string[];          // 이미지 URL 배열
        amenities: string[];       // 편의시설 (예: ["주차장", "와이파이"])
        capacity: number;          // 수용 인원
        tables: number;            // 테이블 수
        createdAt: string;
        updatedAt: string;
    }
}
```

#### PATCH `/owner/restaurant/info`

**설명**: 매장 정보 수정

**Request Body**:

```typescript
{
    name ? : string;
    category ? : string;
    address ? : {
        street? : string;
        city? : string;
        state? : string;
        zipCode? : string;
    };
    phone ? : string;
    email ? : string;
    description ? : string;
    amenities ? : string[];
    capacity ? : number;
    tables ? : number;
}
```

**Response**:

```typescript
{
    restaurant: {
        // 업데이트된 매장 정보
    }
}
```

#### POST `/owner/restaurant/images`

**설명**: 매장 이미지 업로드

**Request Body**: FormData

```typescript
{
    image: File;         // 이미지 파일
    caption ? : string;    // 이미지 설명
    order ? : number;      // 표시 순서
}
```

**Response**:

```typescript
{
    imageUrl: string;    // 업로드된 이미지 URL
    id: number;          // 이미지 ID
}
```

#### DELETE `/owner/restaurant/images/:id`

**설명**: 매장 이미지 삭제

**Path Parameters**:

```typescript
{
    id: number;  // 이미지 ID
}
```

**Response**:

```typescript
{
    message: string;  // "이미지가 삭제되었습니다."
}
```

#### GET `/owner/restaurant/operating-hours`

**설명**: 운영시간 조회

**Response**:

```typescript
{
    operatingHours: {
        monday: {
            open: string;     // 오픈 시간 (HH:mm)
            close: string;    // 마감 시간 (HH:mm)
            isClosed: boolean; // 휴무일 여부
            breakTime ? : {
                start: string;
                end: string;
            };
        }
        ;
        tuesday: { /* 동일 구조 */
        }
        ;
        wednesday: { /* 동일 구조 */
        }
        ;
        thursday: { /* 동일 구조 */
        }
        ;
        friday: { /* 동일 구조 */
        }
        ;
        saturday: { /* 동일 구조 */
        }
        ;
        sunday: { /* 동일 구조 */
        }
        ;
        holidays: {         // 공휴일 운영
            isOpen: boolean;
            hours ? : {
                open: string;
                close: string;
            };
        }
        ;
    }
}
```

#### PATCH `/owner/restaurant/operating-hours`

**설명**: 운영시간 수정

**Request Body**:

```typescript
{
    monday ? : {
        open? : string;
        close? : string;
        isClosed? : boolean;
        breakTime? : {
            start: string;
            end: string;
        };
    };
    // 다른 요일도 동일한 구조
    holidays ? : {
        isOpen: boolean;
        hours? : {
            open: string;
            close: string;
        };
    };
}
```

**Response**:

```typescript
{
    operatingHours: {
        // 업데이트된 운영시간 정보
    }
}
```

### 8. 메뉴 관리 (Menu Management)

#### GET `/owner/restaurant/menu`

**설명**: 메뉴 목록 조회

**Response**:

```typescript
{
    items: [
        {
            id: number;
            category: string;        // 카테고리 (예: "메인", "사이드", "음료")
            name: string;            // 메뉴명
            description? : string;    // 설명
            price: number;           // 가격
            image? : string;          // 이미지 URL
            available: boolean;      // 판매 가능 여부
            isPopular: boolean;      // 인기 메뉴 여부
            isNew: boolean;          // 신메뉴 여부
            allergens? : string[];    // 알레르기 성분
            spicyLevel? : number;     // 매운 정도 (0-5)
            order: number;           // 표시 순서
            createdAt: string;
            updatedAt: string;
        }
    ]
}
```

#### POST `/owner/restaurant/menu`

**설명**: 메뉴 추가

**Request Body**:

```typescript
{
    category: string;      // 카테고리 (필수)
    name: string;          // 메뉴명 (필수)
    description ? : string;  // 설명
    price: number;         // 가격 (필수)
    image ? : string;        // 이미지 URL
    allergens ? : string[];  // 알레르기 성분
    spicyLevel ? : number;   // 매운 정도 (0-5)
}
```

**Response**:

```typescript
{
    menuItem: {
        // 생성된 메뉴 정보
    }
}
```

#### PATCH `/owner/restaurant/menu/:id`

**설명**: 메뉴 수정

**Path Parameters**:

```typescript
{
    id: number;  // 메뉴 ID
}
```

**Request Body**:

```typescript
{
    name ? : string;
    description ? : string;
    price ? : number;
    category ? : string;
    image ? : string;
    allergens ? : string[];
    spicyLevel ? : number;
    isPopular ? : boolean;
    isNew ? : boolean;
    order ? : number;
}
```

**Response**:

```typescript
{
    menuItem: {
        // 업데이트된 메뉴 정보
    }
}
```

#### DELETE `/owner/restaurant/menu/:id`

**설명**: 메뉴 삭제

**Path Parameters**:

```typescript
{
    id: number;  // 메뉴 ID
}
```

**Response**:

```typescript
{
    message: string;  // "메뉴가 삭제되었습니다."
}
```

#### PATCH `/owner/restaurant/menu/:id/availability`

**설명**: 메뉴 판매 상태 변경

**Path Parameters**:

```typescript
{
    id: number;  // 메뉴 ID
}
```

**Request Body**:

```typescript
{
    available: boolean;  // 판매 가능 여부
}
```

**Response**:

```typescript
{
    menuItem: {
        // 업데이트된 메뉴 정보
    }
}
```

### 9. 알림 관리 (Notification Management)

#### GET `/owner/notifications`

**설명**: 알림 목록 조회

**Query Parameters**:

```typescript
{
    type ? : "booking" | "review" | "system" | "marketing"; // 알림 유형
    unreadOnly ? : boolean;  // 읽지 않은 알림만 조회
    page ? : number;         // 페이지 번호
    limit ? : number;        // 페이지당 항목 수
}
```

**Response**:

```typescript
{
    notifications: [
        {
            id: number;
            type: string;            // 알림 유형
            title: string;           // 제목
            message: string;         // 내용
            data? : {                 // 관련 데이터
                bookingId? : number;
                reviewId? : number;
                customerId? : number;
            };
            isRead: boolean;         // 읽음 여부
            createdAt: string;
        }
    ];
    unreadCount: number;         // 읽지 않은 알림 수
    total: number;
    page: number;
    totalPages: number;
}
```

#### PATCH `/owner/notifications/:id/read`

**설명**: 알림 읽음 처리

**Path Parameters**:

```typescript
{
    id: number;  // 알림 ID
}
```

**Response**:

```typescript
{
    notification: {
        // 업데이트된 알림 정보
    }
}
```

#### PATCH `/owner/notifications/read-all`

**설명**: 모든 알림 읽음 처리

**Response**:

```typescript
{
    message: string;      // "모든 알림을 읽음 처리했습니다."
    updatedCount: number; // 업데이트된 알림 수
}
```

#### DELETE `/owner/notifications/:id`

**설명**: 알림 삭제

**Path Parameters**:

```typescript
{
    id: number;  // 알림 ID
}
```

**Response**:

```typescript
{
    message: string;  // "알림이 삭제되었습니다."
}
```

#### GET `/owner/notifications/settings`

**설명**: 알림 설정 조회

**Response**:

```typescript
{
    settings: {
        booking: {
            new
        :
            boolean;          // 새 예약 알림
            cancelled: boolean;    // 예약 취소 알림
            modified: boolean;     // 예약 변경 알림
        }
        ;
        review: {
            new
        :
            boolean;          // 새 리뷰 알림
            lowRating: boolean;    // 낮은 평점 알림 (3점 이하)
        }
        ;
        system: {
            updates: boolean;      // 시스템 업데이트 알림
            maintenance: boolean;  // 점검 알림
        }
        ;
        marketing: {
            promotions: boolean;   // 프로모션 알림
            newsletter: boolean;   // 뉴스레터
        }
        ;
        channels: {
            email: boolean;        // 이메일 알림
            sms: boolean;          // SMS 알림
            push: boolean;         // 푸시 알림
        }
        ;
    }
}
```

#### PATCH `/owner/notifications/settings`

**설명**: 알림 설정 변경

**Request Body**:

```typescript
{
    booking ? : {
        new? : boolean;
        cancelled? : boolean;
        modified? : boolean;
    };
    review ? : {
        new? : boolean;
        lowRating? : boolean;
    };
    system ? : {
        updates? : boolean;
        maintenance? : boolean;
    };
    marketing ? : {
        promotions? : boolean;
        newsletter? : boolean;
    };
    channels ? : {
        email? : boolean;
        sms? : boolean;
        push? : boolean;
    };
}
```

**Response**:

```typescript
{
    settings: {
        // 업데이트된 설정 정보
    }
}
```

### 10. 프로필 관리 (Profile Management)

#### GET `/owner/profile`

**설명**: 프로필 정보 조회

**Response**:

```typescript
{
    profile: {
        id: number;
        name: string;
        email: string;
        phone: string;
        position: string;          // 직책 (예: "대표", "매니저")
        restaurantId: number;
        restaurantName: string;
        profileImage ? : string;     // 프로필 이미지 URL
        address ? : {
            street: string;
            city: string;
            state: string;
            zipCode: string;
        };
        createdAt: string;
        updatedAt: string;
    }
}
```

#### PATCH `/owner/profile`

**설명**: 프로필 정보 수정

**Request Body**:

```typescript
{
    name ? : string;
    email ? : string;
    phone ? : string;
    position ? : string;
    address ? : {
        street? : string;
        city? : string;
        state? : string;
        zipCode? : string;
    };
}
```

**Response**:

```typescript
{
    profile: {
        // 업데이트된 프로필 정보
    }
}
```

#### POST `/owner/profile/image`

**설명**: 프로필 이미지 업로드

**Request Body**: FormData

```typescript
{
    image: File;  // 이미지 파일 (최대 5MB)
}
```

**Response**:

```typescript
{
    imageUrl: string;  // 업로드된 이미지 URL
}
```

#### DELETE `/owner/profile/image`

**설명**: 프로필 이미지 삭제

**Response**:

```typescript
{
    message: string;  // "프로필 이미지가 삭제되었습니다."
}
```

### 11. 계정 관리 (Account Management)

#### PATCH `/owner/account/password`

**설명**: 비밀번호 변경

**Request Body**:

```typescript
{
    currentPassword: string;  // 현재 비밀번호
    newPassword: string;      // 새 비밀번호 (최소 8자, 영문+숫자+특수문자)
}
```

**Response**:

```typescript
{
    message: string;  // "비밀번호가 변경되었습니다."
}
```

#### PATCH `/owner/account/two-factor`

**설명**: 2단계 인증 설정

**Request Body**:

```typescript
{
    enabled: boolean;     // 2단계 인증 활성화 여부
    method ? : "sms" | "email" | "app"; // 인증 방법 (enabled가 true일 때 필수)
}
```

**Response**:

```typescript
{
    settings: {
        twoFactorEnabled: boolean;
        method ? : string;
        qrCode ? : string;    // TOTP 앱 사용 시 QR 코드 URL
        secret ? : string;    // TOTP 시크릿 키
    }
}
```

#### GET `/owner/account/sessions`

**설명**: 활성 세션 조회

**Response**:

```typescript
{
    sessions: [
        {
            id: string;              // 세션 ID
            device: string;          // 기기 정보
            browser: string;         // 브라우저
            ipAddress: string;       // IP 주소
            location? : string;       // 위치 (도시, 국가)
            lastActive: string;      // 마지막 활동 시간
            isCurrent: boolean;      // 현재 세션 여부
        }
    ]
}
```

#### DELETE `/owner/account/sessions/:id`

**설명**: 특정 세션 종료

**Path Parameters**:

```typescript
{
    id: string;  // 세션 ID
}
```

**Response**:

```typescript
{
    message: string;  // "세션이 종료되었습니다."
}
```

#### DELETE `/owner/account/sessions/others`

**설명**: 다른 모든 세션 종료

**Response**:

```typescript
{
    message: string;         // "다른 모든 세션이 종료되었습니다."
    terminatedCount: number; // 종료된 세션 수
}
```

#### GET `/owner/account/login-history`

**설명**: 로그인 기록 조회

**Query Parameters**:

```typescript
{
    page ? : number;    // 페이지 번호 (기본값: 1)
    limit ? : number;   // 페이지당 항목 수 (기본값: 20)
}
```

**Response**:

```typescript
{
    history: [
        {
            id: number;
            timestamp: string;       // 로그인 시간
            ipAddress: string;       // IP 주소
            location? : string;       // 위치
            device: string;          // 기기 정보
            browser: string;         // 브라우저
            success: boolean;        // 로그인 성공 여부
            failureReason? : string;  // 실패 사유
        }
    ];
    total: number;
    page: number;
    totalPages: number;
}
```

#### DELETE `/owner/account`

**설명**: 계정 삭제

**Request Body**:

```typescript
{
    password: string;        // 비밀번호 확인
    reason ? : string;         // 탈퇴 사유
    feedback ? : string;       // 피드백
}
```

**Response**:

```typescript
{
    message: string;  // "계정이 삭제되었습니다."
}
```

### 12. 설정 (Settings)

#### GET `/owner/settings/booking-policy`

**설명**: 예약 정책 조회

**Response**:

```typescript
{
    policy: {
        maxPartySize: number;           // 최대 예약 인원
        minPartySize: number;           // 최소 예약 인원
        advanceBookingDays: number;     // 사전 예약 가능 일수
        cancellationHours: number;      // 취소 가능 시간 (예약 시간 기준)
        depositRequired: boolean;       // 예약금 필요 여부
        depositAmount ? : number;         // 예약금 액수
        depositPolicy ? : string;         // 예약금 정책 설명
        noShowPenalty ? : number;         // 노쇼 패널티 금액
        specialRequestsAllowed: boolean; // 특별 요청 허용 여부
        bookingInterval: number;        // 예약 시간 간격 (분)
        maxBookingsPerDay: number;      // 일일 최대 예약 수
        blackoutDates: string[];        // 예약 불가 날짜 (YYYY-MM-DD)
    }
}
```

#### PATCH `/owner/settings/booking-policy`

**설명**: 예약 정책 수정

**Request Body**:

```typescript
{
    maxPartySize ? : number;
    minPartySize ? : number;
    advanceBookingDays ? : number;
    cancellationHours ? : number;
    depositRequired ? : boolean;
    depositAmount ? : number;
    depositPolicy ? : string;
    noShowPenalty ? : number;
    specialRequestsAllowed ? : boolean;
    bookingInterval ? : number;
    maxBookingsPerDay ? : number;
    blackoutDates ? : string[];
}
```

**Response**:

```typescript
{
    policy: {
        // 업데이트된 정책 정보
    }
}
```

#### GET `/owner/settings/access-logs`

**설명**: 접근 로그 조회

**Query Parameters**:

```typescript
{
    page ? : number;     // 페이지 번호
    limit ? : number;    // 페이지당 항목 수
    userId ? : number;   // 특정 사용자 필터
    action ? : string;   // 특정 액션 필터
}
```

**Response**:

```typescript
{
    logs: [
        {
            id: number;
            userId: number;
            userName: string;
            action: string;          // 수행한 작업
            resource: string;        // 대상 리소스
            details? : any;           // 상세 정보
            ipAddress: string;
            timestamp: string;
        }
    ];
    total: number;
    page: number;
    totalPages: number;
}
```

### 13. 외부 서비스 연동 (Integrations)

#### GET `/owner/settings/integrations`

**설명**: 연동 서비스 상태 조회

**Response**:

```typescript
{
    integrations: {
        pos: {                     // POS 시스템
            connected: boolean;
            provider ? : string;       // 제공업체명
            lastSync ? : string;       // 마지막 동기화
        }
        ;
        payment: {                 // 결제 시스템
            connected: boolean;
            provider ? : string;
            methods: string[];       // 지원 결제 수단
        }
        ;
        delivery: {                // 배달 서비스
            connected: boolean;
            platforms: string[];     // 연동된 플랫폼
        }
        ;
        marketing: {               // 마케팅 도구
            connected: boolean;
            provider ? : string;
        }
        ;
        accounting: {              // 회계 시스템
            connected: boolean;
            provider ? : string;
        }
        ;
    }
}
```

#### POST `/owner/settings/integrations/:service/connect`

**설명**: 서비스 연결

**Path Parameters**:

```typescript
{
    service: "pos" | "payment" | "delivery" | "marketing" | "accounting"; // 서비스 종류
}
```

**Request Body**:

```typescript
{
    provider: string;      // 제공업체
    apiKey ? : string;       // API 키
    apiSecret ? : string;    // API 시크릿
    webhookUrl ? : string;   // 웹훅 URL
    config ? : {             // 추가 설정
        [key: string
]:
    any;
}
    ;
}
```

**Response**:

```typescript
{
    status: "connected" | "pending" | "failed";
    message ? : string;
    requiresAction ? : {     // 추가 작업 필요 시
        type: string;
        url? : string;
        instructions? : string;
    };
}
```

#### DELETE `/owner/settings/integrations/:service/disconnect`

**설명**: 서비스 연결 해제

**Path Parameters**:

```typescript
{
    service: string;  // 서비스 종류
}
```

**Response**:

```typescript
{
    message: string;  // "서비스 연결이 해제되었습니다."
}
```

#### POST `/owner/settings/integrations/:service/test`

**설명**: 연결 테스트

**Path Parameters**:

```typescript
{
    service: string;  // 서비스 종류
}
```

**Response**:

```typescript
{
    success: boolean;
    message: string;
    details ? : {
        latency: number;     // 응답 시간 (ms)
        version? : string;    // API 버전
        features? : string[]; // 지원 기능
    };
}
```

## 공통 응답 형식

### 성공 응답

```typescript
{
    success: true;
    data: any;           // API별 응답 데이터
    message ? : string;    // 선택적 성공 메시지
}
```

### 에러 응답

```typescript
{
    success: false;
    error: {
        code: string;      // 에러 코드 (예: "UNAUTHORIZED", "NOT_FOUND")
        message: string;   // 에러 메시지
        details ? : any;     // 추가 에러 정보
    }
}
```

### 에러 코드

- `UNAUTHORIZED`: 인증되지 않음
- `FORBIDDEN`: 권한 없음
- `NOT_FOUND`: 리소스를 찾을 수 없음
- `BAD_REQUEST`: 잘못된 요청
- `CONFLICT`: 충돌 (중복 등)
- `INTERNAL_ERROR`: 서버 내부 오류
- `RATE_LIMIT`: 요청 한도 초과
- `MAINTENANCE`: 서비스 점검 중

## 인증

모든 API 요청 (로그인 제외)은 Authorization 헤더에 Bearer 토큰이 필요합니다.

```
Authorization: Bearer {token}
```

### 토큰 만료

- 액세스 토큰: 1시간
- 리프레시 토큰: 30일

## 페이지네이션

목록 조회 API는 다음 쿼리 파라미터를 지원합니다:

- `page`: 페이지 번호 (기본값: 1)
- `limit`: 페이지당 항목 수 (기본값: 20, 최대: 100)
- `sort`: 정렬 기준 및 방향 (예: `createdAt:desc`, `name:asc`)

## 날짜 형식

모든 날짜는 ISO 8601 형식을 사용합니다:

- 날짜와 시간: `YYYY-MM-DDTHH:mm:ss.sssZ`
- 날짜만: `YYYY-MM-DD`
- 시간만: `HH:mm`

## 요청 제한

- 일반 API: 분당 100회
- 인증 API: 분당 10회
- 파일 업로드: 분당 20회

## 파일 업로드

- 최대 파일 크기: 5MB
- 지원 형식: JPG, PNG, GIF, PDF
- Content-Type: `multipart/form-data`
