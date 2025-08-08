-- 회원 데이터 (테이블이 비어있을 때만 실행)
INSERT INTO member (name, nickname, email, phone, reservation_enabled, remind_enabled, review_enabled, is_vip, created_at, updated_at)
SELECT *
FROM (SELECT '김철수' AS name, '맛집탐험가' AS nickname, 'foodexplorer@example.com' AS email, '010-1234-5678' AS phone, 
             TRUE AS reservation_enabled, TRUE AS remind_enabled, TRUE AS review_enabled, FALSE AS is_vip,
             NOW() AS created_at, NOW() AS updated_at
      UNION ALL
      SELECT '이영희', '공덕미식가', 'gongdeok@example.com', '010-2345-6789', TRUE, TRUE, TRUE, FALSE, NOW(), NOW()
      UNION ALL
      SELECT '박민수', '마포구민', 'mapo@example.com', '010-3456-7890', TRUE, TRUE, TRUE, FALSE, NOW(), NOW()
      UNION ALL
      SELECT '정서연', '직장인점심러', 'lunch@example.com', '010-4567-8901', TRUE, TRUE, TRUE, FALSE, NOW(), NOW()
      UNION ALL
      SELECT '최준호', '소맥애호가', 'somac@example.com', '010-5678-9012', TRUE, TRUE, TRUE, TRUE, NOW(), NOW()) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM member LIMIT 1);
