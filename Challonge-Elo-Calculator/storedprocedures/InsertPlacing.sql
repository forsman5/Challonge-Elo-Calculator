DROP PROCEDURE IF EXISTS InsertPlacing;
DELIMITER //
CREATE PROCEDURE InsertPlacing
(
IN p_id INT,
IN t_id INT,
IN place INT
)
BEGIN
INSERT INTO placings (player_id,
					  tourney_id,
					  placing)
				    VALUES
					  (p_id,
					   t_id,
					   place);
END //
DELIMITER ;
