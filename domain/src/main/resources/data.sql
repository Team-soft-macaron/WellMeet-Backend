-- 회원 데이터 (테이블이 비어있을 때만 실행)
INSERT INTO member (nickname)
SELECT *
FROM (SELECT '맛집탐험가' AS nickname
      UNION ALL
      SELECT '공덕미식가'
      UNION ALL
      SELECT '마포구민'
      UNION ALL
      SELECT '직장인점심러'
      UNION ALL
      SELECT '소맥애호가') AS tmp
WHERE NOT EXISTS (SELECT 1 FROM member LIMIT 1);
