DROP PROCEDURE IF EXISTS DeletePlayer;
DELIMITER //
CREATE PROCEDURE DeletePlayer
(
IN id INT
)
BEGIN
DELETE FROM players
WHERE player_id = id;
END //
DELIMITER ;