package com.wellmeet.restaurant.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ImageUploadResponse {

    private String imageUrl;    // 업로드된 이미지 URL
    private Long id;            // 이미지 ID
}