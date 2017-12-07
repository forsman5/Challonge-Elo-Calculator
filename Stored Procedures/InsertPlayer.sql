DROP PROCEDURE IF EXISTS InsertPlayer;
DELIMITER //
CREATE PROCEDURE InsertPlayer
(
IN p_id INT,
IN p_name varchar(255),
IN p_elo INT
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
