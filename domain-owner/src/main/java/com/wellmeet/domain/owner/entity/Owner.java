package com.wellmeet.domain.owner.entity;

import com.wellmeet.domain.common.BaseEntity;
import com.wellmeet.domain.exception.OwnerErrorCode;
import com.wellmeet.domain.exception.OwnerException;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Owner extends BaseEntity {

    protected static final int MAX_NAME_LENGTH = 10;

    @Id
    private String id;

    @NotBlank
    private String name;

    @NotBlank
    private String email;

    private boolean reservationEnabled;
    private boolean reviewEnabled;

    public Owner(String name, String email) {
        validateName(name);

        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.email = email;
        this.reservationEnabled = true;
        this.reviewEnabled = true;
    }

    private void validateName(String name) {
        if (name == null || name.isBlank() || name.length() > MAX_NAME_LENGTH) {
            throw new OwnerException(OwnerErrorCode.OWNER_NAME_INVALID);
        }
    }
}
