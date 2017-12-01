DROP PROCEDURE IF EXISTS DeleteMatches;
DELIMITER //
CREATE PROCEDURE DeleteMatches
(
IN id INT
)
BEGIN
DELETE FROM matches
WHERE winner_id = id OR loser_id = id;
END //
DELIMITER ;