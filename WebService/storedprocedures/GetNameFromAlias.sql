DROP PROCEDURE IF EXISTS GetNameFromAlias;
DELIMITER //
CREATE PROCEDURE GetNameFromAlias
(
IN in_name VARCHAR(255)
)
BEGIN
SELECT name FROM aliases WHERE alias = in_name;
END //
DELIMITER ;
