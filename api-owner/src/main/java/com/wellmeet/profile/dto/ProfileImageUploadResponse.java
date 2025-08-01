package com.wellmeet.profile.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileImageUploadResponse {

    private String imageUrl;  // 업로드된 이미지 URL
}