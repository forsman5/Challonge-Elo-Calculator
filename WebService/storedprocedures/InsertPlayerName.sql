DROP PROCEDURE IF EXISTS InsertPlayerName;
DELIMITER //
CREATE PROCEDURE InsertPlayerName
(
IN p_name varchar(255),
IN p_elo INT
)
BEGIN
INSERT INTO players (elo,
					 name)
				    VALUES
					 (p_elo,
					  p_name);
END //
DELIMITER ;
