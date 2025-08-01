package com.wellmeet.review;

import com.wellmeet.auth.dto.MessageResponse;
import com.wellmeet.domain.member.entity.Member;
import com.wellmeet.domain.owner.OwnerDomainService;
import com.wellmeet.domain.owner.entity.Owner;
import com.wellmeet.domain.restaurant.review.entity.Review;
import com.wellmeet.domain.restaurant.review.repository.ReviewRepository;
import com.wellmeet.exception.ErrorCode;
import com.wellmeet.exception.WellMeetException;
import com.wellmeet.review.dto.CreateReplyRequest;
import com.wellmeet.review.dto.PagedReviewResponse;
import com.wellmeet.review.dto.ReviewListResponse;
import com.wellmeet.review.dto.ReviewStatsResponse;
import com.wellmeet.review.dto.UpdateReplyRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OwnerDomainService ownerDomainService;

    @Transactional(readOnly = true)
    public PagedReviewResponse getReviews(Long ownerId, int page, int limit,
                                          Integer rating, Boolean hasReply) {
        Owner owner = ownerDomainService.getById(ownerId);

        // TODO: 페이징과 필터링을 지원하는 리포지토리 메서드 추가 필요
        List<Review> allReviews = reviewRepository.findByRestaurantId(owner.getRestaurant().getId());

        // 필터링
        List<Review> filteredReviews = allReviews.stream()
                .filter(r -> rating == null || (int) r.getRating() == rating)
                // TODO: hasReply 필터링 (답글 기능 구현 후)
                .toList();

        // 페이징
        int start = (page - 1) * limit;
        int end = Math.min(start + limit, filteredReviews.size());
        List<Review> pagedReviews = filteredReviews.subList(start, end);

        List<ReviewListResponse> reviewResponses = pagedReviews.stream()
                .map(this::convertToReviewListResponse)
                .toList();

        int totalPages = (int) Math.ceil((double) filteredReviews.size() / limit);

        return new PagedReviewResponse(reviewResponses, filteredReviews.size(), page, totalPages);
    }

    @Transactional(readOnly = true)
    public ReviewStatsResponse getReviewStats(Long ownerId) {
        Owner owner = ownerDomainService.getById(ownerId);
        List<Review> reviews = reviewRepository.findByRestaurantId(owner.getRestaurant().getId());

        if (reviews.isEmpty()) {
            return new ReviewStatsResponse(0.0, 0, Map.of(), 0.0, "stable");
        }

        // 평균 별점
        double avgRating = reviews.stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0.0);

        // 별점별 분포
        Map<String, Long> distribution = reviews.stream()
                .collect(Collectors.groupingBy(
                        r -> String.valueOf((int) r.getRating()),
                        Collectors.counting()
                ));

        // TODO: 답글 작성률 계산 (답글 기능 구현 후)
        double replyRate = 0.0;

        // TODO: 최근 추세 계산 (이전 기간과 비교)
        String recentTrend = "stable";

        return new ReviewStatsResponse(
                Math.round(avgRating * 10.0) / 10.0, // 소수점 1자리
                reviews.size(),
                distribution,
                replyRate,
                recentTrend
        );
    }

    @Transactional
    public ReviewListResponse createReply(Long ownerId, Long reviewId, CreateReplyRequest request) {
        Owner owner = ownerDomainService.getById(ownerId);
        Review review = getReviewByIdAndOwner(reviewId, owner);

        // TODO: Review 엔티티에 답글 기능 추가 또는 별도의 Reply 엔티티 생성
        // review.addReply(request.getReply());

        return convertToReviewListResponse(review);
    }

    @Transactional
    public ReviewListResponse updateReply(Long ownerId, Long reviewId, UpdateReplyRequest request) {
        Owner owner = ownerDomainService.getById(ownerId);
        Review review = getReviewByIdAndOwner(reviewId, owner);

        // TODO: 답글 수정 로직
        // review.updateReply(request.getReply());

        return convertToReviewListResponse(review);
    }

    @Transactional
    public void deleteReply(Long ownerId, Long reviewId) {
        Owner owner = ownerDomainService.getById(ownerId);
        Review review = getReviewByIdAndOwner(reviewId, owner);

        // TODO: 답글 삭제 로직
        // review.deleteReply();
    }

    private Review getReviewByIdAndOwner(Long reviewId, Owner owner) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new WellMeetException(ErrorCode.REVIEW_NOT_FOUND));

        // 해당 사장님의 레스토랑 리뷰인지 확인
        if (!review.getRestaurant().getId().equals(owner.getRestaurant().getId())) {
            throw new WellMeetException(ErrorCode.FORBIDDEN);
        }

        return review;
    }

    private ReviewListResponse convertToReviewListResponse(Review review) {
        Member member = review.getMember();

        ReviewListResponse.CustomerInfo customerInfo = new ReviewListResponse.CustomerInfo(
                member.getId(),
                member.getName()
        );

        // TODO: 예약 정보 연결 (Review와 Reservation 간의 관계 설정 필요)
        ReviewListResponse.BookingInfo bookingInfo = new ReviewListResponse.BookingInfo(
                null, // 예약 ID
                review.getCreatedAt().toLocalDate().toString(),
                2 // 임시 인원수
        );

        // TODO: 답글 정보 (Reply 엔티티 구현 후)
        ReviewListResponse.ReplyInfo replyInfo = null;

        return new ReviewListResponse(
                review.getId(),
                customerInfo,
                bookingInfo,
                review.getRating(),
                review.getContent(),
                replyInfo,
                List.of(), // TODO: 리뷰 이미지
                review.getCreatedAt()
        );
    }
}
