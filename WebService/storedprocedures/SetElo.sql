DROP PROCEDURE IF EXISTS SetElo;
DELIMITER //
CREATE PROCEDURE SetElo
(
IN in_id INT,
IN in_elo INT
)
BEGIN
UPDATE players SET elo = in_elo WHERE player_id = in_id;
END //
DELIMITER ;