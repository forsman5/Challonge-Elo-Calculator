DROP PROCEDURE IF EXISTS GetNameFromAlias;
DELIMITER //
CREATE PROCEDURE GetNameFromAlias
(
IN in_name VARCHAR(255),
OUT latest DATE
)
BEGIN
SELECT name FROM aliases WHERE alias = in_name LIMIT 1;
END //
DELIMITER ;
