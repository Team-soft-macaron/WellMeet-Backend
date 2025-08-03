package com.wellmeet.domain.owner.entity;

import com.wellmeet.domain.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Owner extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String email;

    private boolean reservationEnabled;
    private boolean reviewEnabled;

    public Owner(String name, String email) {
        this.name = name;
        this.email = email;
        this.reservationEnabled = true;
        this.reviewEnabled = true;
    }
}
