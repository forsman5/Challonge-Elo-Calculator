DROP PROCEDURE IF EXISTS GetPlayerID;
DELIMITER //
CREATE PROCEDURE GetPlayerID
(
IN in_name VARCHAR(255)
)
BEGIN
SELECT player_id FROM players WHERE name = in_name;
END //
DELIMITER ;
