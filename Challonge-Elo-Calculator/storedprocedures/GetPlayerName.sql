DROP PROCEDURE IF EXISTS GetPlayerName;
DELIMITER //
CREATE PROCEDURE GetPlayerName
(
IN in_id INT
)
BEGIN
SELECT name FROM players WHERE player_id = in_id;
END //
DELIMITER ;
