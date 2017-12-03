DROP PROCEDURE IF EXISTS GetCountOfMethod;
DELIMITER //
CREATE PROCEDURE GetCountOfMethod
(
IN inMethod VARCHAR(255)
)
BEGIN
SELECT COUNT(*) 
FROM event_log 
WHERE method = inMethod 
  and time_elapsed = -2 
  and DATE(date_occurred) = CURDATE();
END //
DELIMITER ;