DROP PROCEDURE IF EXISTS GetLatestTourneyDate;
DELIMITER //
CREATE PROCEDURE GetLatestTourneyDate
(
OUT latest DATE
)
BEGIN
SELECT date_started FROM tournaments ORDER BY date_started LIMIT 1;
END //
DELIMITER ;
