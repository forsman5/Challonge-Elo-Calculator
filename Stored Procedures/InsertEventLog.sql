DROP PROCEDURE IF EXISTS InsertEventLog;
DELIMITER //
CREATE PROCEDURE InsertEventLog
(
IN date1 DATETIME,
IN meth VARCHAR(255),
IN paramVal VARCHAR(255),
IN outVal VARCHAR(255),
IN timeElap INT,
IN mess VARCHAR(255)
)
BEGIN
INSERT INTO event_log (date_occurred, method, param, returned, time_elapsed, message)
			VALUES (date1, meth, paramVal, outVal, timeElap, mess);
END //
DELIMITER ;