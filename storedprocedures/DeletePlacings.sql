DROP PROCEDURE IF EXISTS DeletePlacings;
DELIMITER //
CREATE PROCEDURE DeletePlacings
(
IN id INT
)
BEGIN
DELETE FROM placings
WHERE player_id = id;
END //
DELIMITER ;