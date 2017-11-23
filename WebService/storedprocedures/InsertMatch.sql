DROP PROCEDURE IF EXISTS InsertMatch;
DELIMITER //
CREATE PROCEDURE InsertMatch
(
IN m_id INT,
IN w_id INT,
IN l_id INT,
IN w_score INT,
IN l_score INT,
IN t_id INT
)
BEGIN
INSERT INTO matches (match_id,
					 winner_id,
					 loser_id,
					 winner_score,
					 loser_score,
					 tourney_id)
				    VALUES
					(m_id,
					 w_id,
					 l_id,
					 w_score,
					 l_score,
					 t_id);
END //
DELIMITER ;
