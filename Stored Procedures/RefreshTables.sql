DROP PROCEDURE IF EXISTS RefreshTables;
DELIMITER //
CREATE PROCEDURE RefreshTables()
BEGIN
DROP TABLE IF EXISTS event_log;
CREATE TABLE event_log
(
  event_id int unsigned NOT NULL auto_increment,
  date_occurred DATETIME NOT NULL,
  method VARCHAR(255) NOT NULL,
  param VARCHAR(255),
  returned VARCHAR(255),
  time_elapsed INT NOT NULL,
  message VARCHAR(2047),

  PRIMARY KEY (event_id)
);

DELETE FROM players;
DELETE FROM tournaments;
DELETE FROM matches;
DELETE FROM aliases;
DELETE FROM placings;

ALTER TABLE players AUTO_INCREMENT = 1;
ALTER TABLE tournaments AUTO_INCREMENT = 1;
ALTER TABLE matches AUTO_INCREMENT = 1;
ALTER TABLE aliases AUTO_INCREMENT = 1;
ALTER TABLE placings AUTO_INCREMENT = 1;

END //
DELIMITER ;
