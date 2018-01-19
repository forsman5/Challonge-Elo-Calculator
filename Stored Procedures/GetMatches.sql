DROP PROCEDURE IF EXISTS GetMatches;
DELIMITER //
CREATE PROCEDURE GetMatches
(
IN id INT
)
BEGIN
SELECT * FROM matches
WHERE winner_id = id OR loser_id = id;
END //
DELIMITER ;