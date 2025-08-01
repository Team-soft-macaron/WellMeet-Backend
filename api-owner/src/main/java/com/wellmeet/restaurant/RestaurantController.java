package com.wellmeet.restaurant;

import com.wellmeet.auth.dto.MessageResponse;
import com.wellmeet.restaurant.dto.ImageUploadResponse;
import com.wellmeet.restaurant.dto.OperatingHoursResponse;
import com.wellmeet.restaurant.dto.RestaurantInfoResponse;
import com.wellmeet.restaurant.dto.UpdateOperatingHoursRequest;
import com.wellmeet.restaurant.dto.UpdateRestaurantInfoRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/owner/restaurant")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    @GetMapping("/info")
    public RestaurantInfoResponse getRestaurantInfo(@RequestParam Long ownerId) { // TODO: JWT에서 추출
        return restaurantService.getRestaurantInfo(ownerId);
    }

    @PatchMapping("/info")
    public RestaurantInfoResponse updateRestaurantInfo(
            @RequestParam Long ownerId, // TODO: JWT에서 추출
            @Valid @RequestBody UpdateRestaurantInfoRequest request
    ) {
        return restaurantService.updateRestaurantInfo(ownerId, request);
    }

    @PostMapping("/images")
    public ImageUploadResponse uploadImage(
            @RequestParam Long ownerId, // TODO: JWT에서 추출
            @RequestParam("image") MultipartFile image,
            @RequestParam(required = false) String caption,
            @RequestParam(required = false, defaultValue = "0") Integer order
    ) {
        return restaurantService.uploadImage(ownerId, image, caption, order);
    }

    @DeleteMapping("/images/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteImage(
            @RequestParam Long ownerId, // TODO: JWT에서 추출
            @PathVariable Long id
    ) {
        restaurantService.deleteImage(ownerId, id);
    }

    @GetMapping("/operating-hours")
    public OperatingHoursResponse getOperatingHours(@RequestParam Long ownerId) { // TODO: JWT에서 추출
        return restaurantService.getOperatingHours(ownerId);
    }

    @PatchMapping("/operating-hours")
    public OperatingHoursResponse updateOperatingHours(
            @RequestParam Long ownerId, // TODO: JWT에서 추출
            @Valid @RequestBody UpdateOperatingHoursRequest request
    ) {
        return restaurantService.updateOperatingHours(ownerId, request);
    }
}