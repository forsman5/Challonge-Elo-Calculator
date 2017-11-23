DROP PROCEDURE IF EXISTS GetElo;
DELIMITER //
CREATE PROCEDURE GetElo
(
IN in_id INT,
IN in_elo INT
)
BEGIN
UPDATE players SET elo = in_elo WHERE player_id = in_id;
END //
DELIMITER ;