DROP PROCEDURE IF EXISTS GetLatestTourneyDate;
DELIMITER //
CREATE PROCEDURE GetLatestTourneyDate
(
OUT latest DATE
)
BEGIN
SELECT date_started FROM tournaments ORDER BY date_started DESC LIMIT 1;
END //
DELIMITER ;
