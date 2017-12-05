DROP PROCEDURE IF EXISTS GetEmptyPlayers;
DELIMITER //
CREATE PROCEDURE GetEmptyPlayers()
BEGIN
SELECT *
FROM players p
LEFT JOIN matches m ON (m.winner_id = p.player_id OR m.loser_id = p.player_id)
WHERE (m.winner_id IS NULL and m.loser_id IS NULL);
END //
DELIMITER ;