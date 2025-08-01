package com.wellmeet.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 페이징된 응답을 위한 표준 DTO
 * 목록 조회 API에서 공통으로 사용됩니다.
 *
 * @param <T> 실제 데이터 아이템 타입
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponse<T> {
    
    private List<T> content;        // 실제 데이터 목록
    private PageInfo pageInfo;      // 페이징 정보
    
    /**
     * 페이징 정보 클래스
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PageInfo {
        private int page;           // 현재 페이지 번호 (1부터 시작)
        private int size;           // 페이지당 아이템 수
        private long totalElements; // 전체 아이템 수
        private int totalPages;     // 전체 페이지 수
        private boolean first;      // 첫 번째 페이지 여부
        private boolean last;       // 마지막 페이지 여부
        private boolean hasNext;    // 다음 페이지 존재 여부
        private boolean hasPrevious; // 이전 페이지 존재 여부
    }
    
    /**
     * 페이징된 응답 생성
     */
    public static <T> PagedResponse<T> of(List<T> content, int page, int size, long totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / size);
        boolean first = page == 1;
        boolean last = page >= totalPages;
        boolean hasNext = page < totalPages;
        boolean hasPrevious = page > 1;
        
        PageInfo pageInfo = new PageInfo(
            page, size, totalElements, totalPages, 
            first, last, hasNext, hasPrevious
        );
        
        return new PagedResponse<>(content, pageInfo);
    }
    
    /**
     * 빈 페이징된 응답 생성
     */
    public static <T> PagedResponse<T> empty(int page, int size) {
        return of(List.of(), page, size, 0L);
    }
}