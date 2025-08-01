package com.wellmeet.profile;

import com.wellmeet.domain.owner.OwnerDomainService;
import com.wellmeet.domain.owner.entity.Owner;
import com.wellmeet.exception.ErrorCode;
import com.wellmeet.exception.WellMeetException;
import com.wellmeet.auth.dto.MessageResponse;
import com.wellmeet.profile.dto.ProfileImageUploadResponse;
import com.wellmeet.profile.dto.ProfileResponse;
import com.wellmeet.profile.dto.UpdateProfileRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final OwnerDomainService ownerDomainService;

    @Transactional(readOnly = true)
    public ProfileResponse getProfile(Long ownerId) {
        Owner owner = ownerDomainService.getById(ownerId);

        ProfileResponse.Address address = new ProfileResponse.Address(
                "서울시 강남구", // TODO: Owner 엔티티에 주소 필드 추가
                "서울시",
                "강남구", 
                "06234"
        );

        ProfileResponse.Profile profile = new ProfileResponse.Profile(
                owner.getId(),
                owner.getName(),
                owner.getEmail(),
                "010-0000-0000", // TODO: Owner 엔티티에 전화번호 필드 추가
                "대표", // TODO: Owner 엔티티에 직책 필드 추가
                Long.valueOf(owner.getRestaurant().getId()),
                owner.getRestaurant().getName(),
                null, // TODO: Owner 엔티티에 프로필 이미지 필드 추가
                address,
                owner.getCreatedAt(),
                owner.getUpdatedAt()
        );

        return new ProfileResponse(profile);
    }

    @Transactional
    public ProfileResponse updateProfile(Long ownerId, UpdateProfileRequest request) {
        Owner owner = ownerDomainService.getById(ownerId);
        
        // TODO: Owner 엔티티에 프로필 업데이트 메서드 추가
        // owner.updateProfile(request);

        return getProfile(ownerId);
    }

    @Transactional
    public ProfileImageUploadResponse uploadProfileImage(Long ownerId, MultipartFile image) {
        Owner owner = ownerDomainService.getById(ownerId);
        
        // TODO: 실제 파일 업로드 로직 구현
        // 1. 파일 검증 (크기 5MB 이하, 이미지 파일만)
        // 2. 기존 프로필 이미지 삭제
        // 3. 새 이미지 업로드
        // 4. Owner 엔티티에 이미지 URL 저장

        if (image.isEmpty()) {
            throw new WellMeetException(ErrorCode.BAD_REQUEST);
        }

        // 파일 크기 검증 (5MB)
        if (image.getSize() > 5 * 1024 * 1024) {
            throw new WellMeetException(ErrorCode.BAD_REQUEST);
        }

        // 임시 응답
        String imageUrl = "https://example.com/profiles/" + ownerId + "_" + System.currentTimeMillis() + ".jpg";
        return new ProfileImageUploadResponse(imageUrl);
    }

    @Transactional
    public MessageResponse deleteProfileImage(Long ownerId) {
        Owner owner = ownerDomainService.getById(ownerId);
        
        // TODO: 프로필 이미지 삭제 로직 구현
        // 1. Owner에서 현재 이미지 URL 조회
        // 2. 실제 파일 삭제
        // 3. Owner 엔티티에서 이미지 URL 제거
        
        return new MessageResponse("프로필 이미지가 삭제되었습니다.");
    }
}