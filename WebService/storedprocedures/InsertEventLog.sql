DROP PROCEDURE IF EXISTS InsertEventLog;
DELIMITER //
CREATE PROCEDURE InsertEventLog
(
IN date1 DATE,
IN meth VARCHAR(255),
IN mess VARCHAR(255)
)
BEGIN
INSERT INTO event_log (date_occured, method, message)
			VALUES (date1, meth, mess);
END //
DELIMITER ;