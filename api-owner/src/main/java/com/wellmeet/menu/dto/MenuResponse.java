package com.wellmeet.menu.dto;

import com.wellmeet.domain.restaurant.menu.entity.Menu;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MenuResponse {

    private Long id;
    private String category;        // 카테고리
    private String name;            // 메뉴명
    private String description;     // 설명
    private int price;              // 가격
    private String image;           // 이미지 URL
    private boolean available;      // 판매 가능 여부
    private boolean isPopular;      // 인기 메뉴 여부
    private boolean isNew;          // 신메뉴 여부
    private List<String> allergens; // 알레르기 성분
    private Integer spicyLevel;     // 매운 정도 (0-5)
    private int order;              // 표시 순서
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public MenuResponse(Menu menu) {
        this.id = menu.getId();
        this.category = "메인"; // TODO: Menu 엔티티에 category 필드 추가 필요
        this.name = menu.getName();
        this.description = menu.getDescription();
        this.price = menu.getPrice();
        this.image = null; // TODO: Menu 엔티티에 image 필드 추가 필요
        this.available = true; // TODO: Menu 엔티티에 available 필드 추가 필요
        this.isPopular = false; // TODO: Menu 엔티티에 isPopular 필드 추가 필요
        this.isNew = false; // TODO: Menu 엔티티에 isNew 필드 추가 필요
        this.allergens = List.of(); // TODO: Menu 엔티티에 allergens 필드 추가 필요
        this.spicyLevel = null; // TODO: Menu 엔티티에 spicyLevel 필드 추가 필요
        this.order = 0; // TODO: Menu 엔티티에 order 필드 추가 필요
        this.createdAt = menu.getCreatedAt();
        this.updatedAt = menu.getUpdatedAt();
    }
}
