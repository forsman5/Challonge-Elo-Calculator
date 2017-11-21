DROP PROCEDURE IF EXISTS InsertPlayer;
DELIMITER //
CREATE PROCEDURE InsertPlayer
(
IN p_id int,
IN p_name varchar(255),
IN p_elo varchar(255)
)
BEGIN
INSERT INTO players (player_id,
					 elo,
					 name)
				    VALUES
					 (p_id,
					  p_elo,
					  p_name);
END //
DELIMITER ;
