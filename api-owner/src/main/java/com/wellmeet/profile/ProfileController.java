package com.wellmeet.profile;

import com.wellmeet.auth.dto.MessageResponse;
import com.wellmeet.profile.dto.ProfileImageUploadResponse;
import com.wellmeet.profile.dto.ProfileResponse;
import com.wellmeet.profile.dto.UpdateProfileRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/owner/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    public ProfileResponse getProfile(@RequestParam Long ownerId) { // TODO: JWT에서 추출
        return profileService.getProfile(ownerId);
    }

    @PatchMapping
    public ProfileResponse updateProfile(
            @RequestParam Long ownerId, // TODO: JWT에서 추출
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        return profileService.updateProfile(ownerId, request);
    }

    @PostMapping("/image")
    public ProfileImageUploadResponse uploadProfileImage(
            @RequestParam Long ownerId, // TODO: JWT에서 추출
            @RequestParam("image") MultipartFile image
    ) {
        return profileService.uploadProfileImage(ownerId, image);
    }

    @DeleteMapping("/image")
    public MessageResponse deleteProfileImage(@RequestParam Long ownerId) { // TODO: JWT에서 추출
        return profileService.deleteProfileImage(ownerId);
    }
}
