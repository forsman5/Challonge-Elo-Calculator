DROP PROCEDURE IF EXISTS UpdateAliasReferences;
DELIMITER //
CREATE PROCEDURE UpdateAliasReferences
(
IN nameOld VARCHAR(255),
IN nameNew VARCHAR(255)
)
BEGIN
UPDATE aliases SET name = nameNew WHERE name = nameOld;
END //
DELIMITER ;