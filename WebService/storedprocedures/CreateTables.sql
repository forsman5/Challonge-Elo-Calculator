DROP PROCEDURE IF EXISTS CreateTables;
DELIMITER //
CREATE PROCEDURE CreateTables()
BEGIN
DROP TABLE IF EXISTS aliases;
CREATE TABLE aliases
(
  alias_id        int unsigned NOT NULL auto_increment,
  name      	 varchar(255) NOT NULL UNIQUE,
  alias			  varchar(255) NOT NULL,
  
  PRIMARY KEY     (alias_id)
);

DROP TABLE IF EXISTS tournaments;
CREATE TABLE tournaments
(
  tourney_id              int unsigned NOT NULL auto_increment,
  date_started       date NOT NULL,
  name			  varchar(255) NOT NULL,
  link			  varchar(255),
  
  PRIMARY KEY     (tourney_id)
);

DROP TABLE IF EXISTS players;
CREATE TABLE players
(
  player_id int unsigned NOT NULL auto_increment,
  elo       int unsigned NOT NULL,
  name		varchar(255) NOT NULL UNIQUE,
  
  PRIMARY KEY     (player_id)
);

DROP TABLE IF EXISTS matches;
CREATE TABLE matches
(
  match_id  int unsigned NOT NULL auto_increment,
  winner_id       int unsigned NOT NULL,
  loser_id		int unsigned NOT NULL,
  winner_score int unsigned NOT NULL,
  loser_score int unsigned NOT NULL,
  tourney_id int unsigned NOT NULL,
  
  PRIMARY KEY     (match_id)
);
END //
DELIMITER ;
