DROP PROCEDURE IF EXISTS InsertAlias;
DELIMITER //
CREATE PROCEDURE InsertAlias
(
IN a_name varchar(255),
IN a_alias varchar(255)
)
BEGIN
INSERT INTO aliases (name,
					 alias)
				    VALUES
					 (a_name,
					  a_alias);
END //
DELIMITER ;
