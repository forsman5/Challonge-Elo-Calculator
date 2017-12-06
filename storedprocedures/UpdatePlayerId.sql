DROP PROCEDURE IF EXISTS UpdatePlayerId;
DELIMITER //
CREATE PROCEDURE UpdatePlayerId
(
IN idOld INT,
IN idNew INT
)
BEGIN
UPDATE matches SET winner_id = idNew WHERE winner_id = idOld;
UPDATE matches SET loser_id = idNew WHERE loser_id = idOld;
UPDATE placings SET player_id = idNew WHERE player_id = idOld;
END //
DELIMITER ;