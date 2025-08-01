package com.wellmeet.restaurant;

import com.wellmeet.domain.owner.OwnerDomainService;
import com.wellmeet.domain.owner.entity.Owner;
import com.wellmeet.domain.restaurant.entity.Restaurant;
import com.wellmeet.exception.ErrorCode;
import com.wellmeet.exception.WellMeetException;
import com.wellmeet.restaurant.dto.ImageUploadResponse;
import com.wellmeet.restaurant.dto.OperatingHoursResponse;
import com.wellmeet.restaurant.dto.RestaurantInfoResponse;
import com.wellmeet.restaurant.dto.UpdateOperatingHoursRequest;
import com.wellmeet.restaurant.dto.UpdateRestaurantInfoRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final OwnerDomainService ownerDomainService;

    @Transactional(readOnly = true)
    public RestaurantInfoResponse getRestaurantInfo(Long ownerId) {
        Owner owner = ownerDomainService.getById(ownerId);
        Restaurant restaurant = owner.getRestaurant();

        RestaurantInfoResponse.Address address = new RestaurantInfoResponse.Address(
                restaurant.getAddress(), // TODO: Restaurant 엔티티에 주소 필드들 추가 필요
                "서울시", // TODO: 실제 주소 필드들 
                "강남구",
                "06234"
        );

        RestaurantInfoResponse.RestaurantInfo restaurantInfo = new RestaurantInfoResponse.RestaurantInfo(
                Long.valueOf(restaurant.getId()),
                restaurant.getName(),
                "한식", // TODO: Restaurant 엔티티에 카테고리 필드 추가
                address,
                "010-0000-0000", // TODO: Restaurant 엔티티에 전화번호 필드 추가
                "info@restaurant.com", // TODO: Restaurant 엔티티에 이메일 필드 추가
                "매장 설명", // TODO: Restaurant 엔티티에 설명 필드 추가
                List.of(), // TODO: 이미지 관리 기능 구현
                List.of("주차장", "와이파이"), // TODO: 편의시설 필드 추가
                100, // TODO: Restaurant 엔티티에 수용인원 필드 추가
                20,  // TODO: Restaurant 엔티티에 테이블 수 필드 추가
                restaurant.getCreatedAt(),
                restaurant.getUpdatedAt()
        );

        return new RestaurantInfoResponse(restaurantInfo);
    }

    @Transactional
    public RestaurantInfoResponse updateRestaurantInfo(Long ownerId, UpdateRestaurantInfoRequest request) {
        Owner owner = ownerDomainService.getById(ownerId);
        Restaurant restaurant = owner.getRestaurant();

        // TODO: Restaurant 엔티티에 업데이트 메서드 추가 필요
        // restaurant.updateInfo(request);

        return getRestaurantInfo(ownerId);
    }

    @Transactional
    public ImageUploadResponse uploadImage(Long ownerId, MultipartFile image, String caption, Integer order) {
        Owner owner = ownerDomainService.getById(ownerId);
        
        // TODO: 실제 파일 업로드 로직 구현
        // 1. 파일 검증 (크기, 형식 등)
        // 2. 파일 저장 (S3, 로컬 스토리지 등)
        // 3. RestaurantImage 엔티티 생성 및 저장

        if (image.isEmpty()) {
            throw new WellMeetException(ErrorCode.BAD_REQUEST);
        }

        // 임시 응답
        String imageUrl = "https://example.com/images/" + System.currentTimeMillis() + ".jpg";
        return new ImageUploadResponse(imageUrl, 1L);
    }

    @Transactional
    public void deleteImage(Long ownerId, Long imageId) {
        Owner owner = ownerDomainService.getById(ownerId);
        
        // TODO: RestaurantImage 엔티티 및 실제 파일 삭제 로직 구현
        // 1. imageId로 RestaurantImage 조회
        // 2. 해당 사장님의 레스토랑 이미지인지 권한 확인
        // 3. 실제 파일 삭제
        // 4. DB에서 레코드 삭제

        throw new WellMeetException(ErrorCode.INTERNAL_SERVER_ERROR); // 임시
    }

    @Transactional(readOnly = true)
    public OperatingHoursResponse getOperatingHours(Long ownerId) {
        Owner owner = ownerDomainService.getById(ownerId);
        
        // TODO: BusinessHour 엔티티에서 운영시간 조회
        // 현재는 기본값 반환
        
        OperatingHoursResponse.DayHours defaultHours = new OperatingHoursResponse.DayHours(
                "09:00", "22:00", false, 
                new OperatingHoursResponse.BreakTime("15:00", "17:00")
        );
        
        OperatingHoursResponse.DayHours closedDay = new OperatingHoursResponse.DayHours(
                null, null, true, null
        );

        OperatingHoursResponse.OperatingHours operatingHours = new OperatingHoursResponse.OperatingHours(
                defaultHours,  // monday
                defaultHours,  // tuesday  
                defaultHours,  // wednesday
                defaultHours,  // thursday
                defaultHours,  // friday
                defaultHours,  // saturday
                closedDay,     // sunday
                new OperatingHoursResponse.HolidayHours(false, null) // holidays  
        );

        return new OperatingHoursResponse(operatingHours);
    }

    @Transactional
    public OperatingHoursResponse updateOperatingHours(Long ownerId, UpdateOperatingHoursRequest request) {
        Owner owner = ownerDomainService.getById(ownerId);
        
        // TODO: BusinessHour 엔티티 업데이트 로직 구현
        // 1. 각 요일별 운영시간 업데이트
        // 2. 공휴일 운영 정책 업데이트
        // 3. 유효성 검증 (오픈 시간 < 마감 시간 등)

        return getOperatingHours(ownerId);
    }
}