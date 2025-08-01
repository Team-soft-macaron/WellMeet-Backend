package com.wellmeet.customer;

import com.wellmeet.customer.dto.CreateCustomerRequest;
import com.wellmeet.customer.dto.CustomerBookingHistoryResponse;
import com.wellmeet.customer.dto.CustomerDetailResponse;
import com.wellmeet.customer.dto.CustomerReviewHistoryResponse;
import com.wellmeet.customer.dto.PagedCustomerResponse;
import com.wellmeet.customer.dto.UpdateCustomerRequest;
import com.wellmeet.customer.dto.UpdateVipStatusRequest;
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

@RestController
@RequestMapping("/owner/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public PagedCustomerResponse getCustomers(
            @RequestParam Long ownerId, // TODO: JWT에서 추출
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) Boolean isVip
    ) {
        return customerService.getCustomers(ownerId, page, Math.min(limit, 100), sort, isVip);
    }

    @GetMapping("/{id}")
    public CustomerDetailResponse getCustomerDetail(
            @RequestParam Long ownerId, // TODO: JWT에서 추출
            @PathVariable Long id
    ) {
        return customerService.getCustomerDetail(ownerId, id);
    }

    @PostMapping
    public CustomerDetailResponse createCustomer(
            @RequestParam Long ownerId, // TODO: JWT에서 추출
            @Valid @RequestBody CreateCustomerRequest request
    ) {
        return customerService.createCustomer(ownerId, request);
    }

    @PatchMapping("/{id}")
    public CustomerDetailResponse updateCustomer(
            @RequestParam Long ownerId, // TODO: JWT에서 추출
            @PathVariable Long id,
            @Valid @RequestBody UpdateCustomerRequest request
    ) {
        return customerService.updateCustomer(ownerId, id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCustomer(
            @RequestParam Long ownerId, // TODO: JWT에서 추출
            @PathVariable Long id
    ) {
        customerService.deleteCustomer(ownerId, id);
    }

    @GetMapping("/{id}/bookings")
    public CustomerBookingHistoryResponse getCustomerBookings(
            @RequestParam Long ownerId, // TODO: JWT에서 추출
            @PathVariable Long id
    ) {
        return customerService.getCustomerBookings(ownerId, id);
    }

    @GetMapping("/{id}/reviews")
    public CustomerReviewHistoryResponse getCustomerReviews(
            @RequestParam Long ownerId, // TODO: JWT에서 추출
            @PathVariable Long id
    ) {
        return customerService.getCustomerReviews(ownerId, id);
    }

    @PatchMapping("/{id}/vip")
    public CustomerDetailResponse updateVipStatus(
            @RequestParam Long ownerId, // TODO: JWT에서 추출
            @PathVariable Long id,
            @Valid @RequestBody UpdateVipStatusRequest request
    ) {
        return customerService.updateVipStatus(ownerId, id, request);
    }
}
