package com.wellmeet.restaurant.domain.crawlingreview.domain;

import com.wellmeet.common.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Atmosphere extends BaseEntity {

    @Id
    private Long id;
    private String name;

    public Atmosphere(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
