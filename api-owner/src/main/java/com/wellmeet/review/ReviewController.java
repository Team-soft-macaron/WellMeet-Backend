package com.wellmeet.review;

import com.wellmeet.auth.dto.MessageResponse;
import com.wellmeet.review.dto.CreateReplyRequest;
import com.wellmeet.review.dto.PagedReviewResponse;
import com.wellmeet.review.dto.ReviewListResponse;
import com.wellmeet.review.dto.ReviewStatsResponse;
import com.wellmeet.review.dto.UpdateReplyRequest;
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
@RequestMapping("/owner/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public PagedReviewResponse getReviews(
            @RequestParam Long ownerId, // TODO: JWT에서 추출
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) Boolean hasReply
    ) {
        return reviewService.getReviews(ownerId, page, Math.min(limit, 100), rating, hasReply);
    }

    @GetMapping("/stats")
    public ReviewStatsResponse getReviewStats(@RequestParam Long ownerId) { // TODO: JWT에서 추출
        return reviewService.getReviewStats(ownerId);
    }

    @PostMapping("/{id}/reply")
    public ReviewListResponse createReply(
            @RequestParam Long ownerId, // TODO: JWT에서 추출
            @PathVariable Long id,
            @Valid @RequestBody CreateReplyRequest request
    ) {
        return reviewService.createReply(ownerId, id, request);
    }

    @PatchMapping("/{id}/reply")
    public ReviewListResponse updateReply(
            @RequestParam Long ownerId, // TODO: JWT에서 추출
            @PathVariable Long id,
            @Valid @RequestBody UpdateReplyRequest request
    ) {
        return reviewService.updateReply(ownerId, id, request);
    }

    @DeleteMapping("/{id}/reply")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReply(
            @RequestParam Long ownerId, // TODO: JWT에서 추출
            @PathVariable Long id
    ) {
        reviewService.deleteReply(ownerId, id);
    }
}
