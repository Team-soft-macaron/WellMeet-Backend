package com.wellmeet.customer.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PagedCustomerResponse {

    private List<CustomerListResponse> customers;
    private long total;
    private int page;
    private int totalPages;
}
