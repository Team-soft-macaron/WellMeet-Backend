-- 회원 데이터 (테이블이 비어있을 때만 실행)
INSERT INTO member (nickname)
SELECT * FROM (
    SELECT '맛집탐험가' AS nickname
    UNION ALL SELECT '공덕미식가'
    UNION ALL SELECT '마포구민'
    UNION ALL SELECT '직장인점심러'
    UNION ALL SELECT '소맥애호가'
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM member LIMIT 1);

-- 레스토랑 데이터 (테이블이 비어있을 때만 실행)
INSERT INTO restaurant (name, address, main_image, latitude, longitude)
SELECT * FROM (
    SELECT '어랑생선구이' AS name, '서울 마포구 마포대로 109 롯데캐슬프레지던트 지하1층' AS address, 'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=800' AS main_image, 37.5445 AS latitude, 126.9517 AS longitude
    UNION ALL SELECT '공덕족발', '서울 마포구 백범로 96', 'https://images.unsplash.com/photo-1552566626-52f8b828add9?w=800', 37.5451, 126.9513
    UNION ALL SELECT '명동교자 공덕점', '서울 마포구 마포대로 92', 'https://images.unsplash.com/photo-1550966871-3ed3cdb5ed0c?w=800', 37.5448, 126.9521
    UNION ALL SELECT '스시도쿠', '서울 마포구 도화길 21', 'https://images.unsplash.com/photo-1579584425555-c3ce17fd4351?w=800', 37.5456, 126.9502
    UNION ALL SELECT '화로상회', '서울 마포구 백범로 125', 'https://images.unsplash.com/photo-1544025162-d76694265947?w=800', 37.5463, 126.9498
    UNION ALL SELECT '봉추찜닭 공덕점', '서울 마포구 마포대로11길 12', 'https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=800', 37.5442, 126.9525
    UNION ALL SELECT '육대장', '서울 마포구 마포대로 109 지하1층', 'https://images.unsplash.com/photo-1590846406792-0adc7f938f1d?w=800', 37.5445, 126.9517
    UNION ALL SELECT '제주몬트락', '서울 마포구 백범로 23', 'https://images.unsplash.com/photo-1514933651103-005eec06c04b?w=800', 37.5438, 126.9530
    UNION ALL SELECT '텐동요츠야', '서울 마포구 마포대로 92 효성해링턴스퀘어 B동', 'https://images.unsplash.com/photo-1592861956120-e524fc739696?w=800', 37.5448, 126.9521
    UNION ALL SELECT '더플레이스', '서울 마포구 독막로 9', 'https://images.unsplash.com/photo-1554679665-f5537f187268?w=800', 37.5454, 126.9509
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM restaurant LIMIT 1);

-- 분위기(vibe) 데이터 (테이블이 비어있을 때만 실행)
INSERT INTO vibe (name)
SELECT * FROM (
    SELECT 'LUXURIOUS' AS name
    UNION ALL SELECT 'QUIET'
    UNION ALL SELECT 'LIVELY'
    UNION ALL SELECT 'CLASSIC'
    UNION ALL SELECT 'MODERN'
    UNION ALL SELECT 'CLEAN'
    UNION ALL SELECT 'ROMANTIC'
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM vibe LIMIT 1);

-- 크롤링 리뷰 데이터 (테이블이 비어있을 때만 실행)
INSERT INTO crawling_review (content, restaurant_id, created_at)
SELECT * FROM (
    SELECT '생선구이 정식이 정말 맛있어요! 반찬도 깔끔하고 점심특선 가성비 최고입니다.' AS content, 1 AS restaurant_id, '2024-12-01 12:30:00' AS created_at
    UNION ALL SELECT '족발이 쫄깃하고 냄새도 안나요. 직원분들도 친절하시고 포장도 가능해요.', 2, '2024-12-02 14:20:00'
    UNION ALL SELECT '만두가 정말 크고 실해요. 칼국수도 시원하고 점심시간엔 항상 줄서요.', 3, '2024-12-03 18:45:00'
    UNION ALL SELECT '스시 오마카세 퀄리티가 좋아요. 가격대비 만족스럽고 예약은 필수입니다.', 4, '2024-12-04 13:00:00'
    UNION ALL SELECT '직화구이 삼겹살이 일품! 고기 질도 좋고 밑반찬도 맛있어요.', 5, '2024-12-05 08:30:00'
    UNION ALL SELECT '찜닭이 달지 않고 맛있어요. 양도 많고 당면 추가는 꼭 하세요!', 6, '2024-12-06 19:00:00'
    UNION ALL SELECT '육개장이 진짜 얼큰하고 시원해요. 해장하기 딱이에요.', 7, '2024-12-07 09:15:00'
    UNION ALL SELECT '흑돼지 구이가 부드럽고 맛있어요. 제주도 느낌 물씬 나는 인테리어도 좋아요.', 8, '2024-12-08 11:30:00'
    UNION ALL SELECT '텐동이 바삭하고 맛있어요. 점심시간엔 웨이팅이 있지만 회전율이 빨라요.', 9, '2024-12-09 12:45:00'
    UNION ALL SELECT '파스타와 피자가 맛있는 브런치 카페! 분위기도 좋고 데이트하기 좋아요.', 10, '2024-12-10 20:00:00'
    UNION ALL SELECT '회사 근처라 자주 가는데 항상 맛있어요. 점심특선 추천!', 1, '2024-12-11 13:30:00'
    UNION ALL SELECT '족발 포장해서 집에서 먹었는데도 맛있었어요. 쌈무가 아삭아삭!', 2, '2024-12-12 12:00:00'
    UNION ALL SELECT '아이들도 좋아하는 만두! 김치만두도 맛있어요.', 3, '2024-12-13 18:00:00'
    UNION ALL SELECT '스시가 신선하고 셰프님이 친절하게 설명해주세요.', 4, '2024-12-14 13:15:00'
    UNION ALL SELECT '회식장소로 좋아요. 룸도 있고 고기 질이 좋아요.', 5, '2024-12-15 07:45:00'
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM crawling_review LIMIT 1);

-- 일반 리뷰 데이터 (테이블이 비어있을 때만 실행)
INSERT INTO review (content, rating, restaurant_id, member_id)
SELECT * FROM (
    SELECT '점심특선 생선구이 정식 강추! 가성비 최고에요.' AS content, 4.5 AS rating, 1 AS restaurant_id, 1 AS member_id
    UNION ALL SELECT '족발이 쫄깃하고 냄새 안나요. 막국수도 맛있어요.', 4.0, 2, 2
    UNION ALL SELECT '만두가 정말 크고 실해요. 줄 서서 먹을만 해요.', 4.5, 3, 3
    UNION ALL SELECT '오마카세 코스 만족스러웠어요. 예약 필수!', 5.0, 4, 4
    UNION ALL SELECT '삼겹살 맛집! 직원분들도 친절해요.', 4.0, 5, 5
    UNION ALL SELECT '찜닭 양이 많아서 남녀 둘이 먹기 충분해요.', 4.5, 6, 1
    UNION ALL SELECT '육개장 진짜 얼큰해요. 해장에 최고!', 4.0, 7, 2
    UNION ALL SELECT '제주도 분위기 물씬! 흑돼지도 맛있어요.', 4.0, 8, 3
    UNION ALL SELECT '텐동 바삭하고 맛있어요. 점심시간 피해서 가세요.', 4.5, 9, 4
    UNION ALL SELECT '브런치 맛집! 커피도 맛있고 분위기도 좋아요.', 4.5, 10, 5
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM review LIMIT 1);

-- 크롤링 리뷰와 분위기 연결 (테이블이 비어있을 때만 실행)
INSERT INTO crawling_review_vibe (crawling_review_id, vibe_id)
SELECT * FROM (
    SELECT 1 AS crawling_review_id, 6 AS vibe_id
    UNION ALL SELECT 1, 2
    UNION ALL SELECT 2, 3
    UNION ALL SELECT 2, 4
    UNION ALL SELECT 3, 3
    UNION ALL SELECT 3, 6
    UNION ALL SELECT 4, 1
    UNION ALL SELECT 4, 5
    UNION ALL SELECT 5, 3
    UNION ALL SELECT 5, 4
    UNION ALL SELECT 6, 3
    UNION ALL SELECT 6, 6
    UNION ALL SELECT 7, 2
    UNION ALL SELECT 7, 6
    UNION ALL SELECT 8, 5
    UNION ALL SELECT 8, 6
    UNION ALL SELECT 9, 6
    UNION ALL SELECT 9, 5
    UNION ALL SELECT 10, 7
    UNION ALL SELECT 10, 5
    UNION ALL SELECT 11, 6
    UNION ALL SELECT 11, 2
    UNION ALL SELECT 12, 3
    UNION ALL SELECT 12, 4
    UNION ALL SELECT 13, 3
    UNION ALL SELECT 13, 6
    UNION ALL SELECT 14, 1
    UNION ALL SELECT 14, 5
    UNION ALL SELECT 15, 3
    UNION ALL SELECT 15, 1
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM crawling_review_vibe LIMIT 1);
