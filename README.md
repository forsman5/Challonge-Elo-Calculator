# PowellSmashRankings
This is the source code for a web app which has a few major components.
-A front end, which is still to be completed.
	-Most likely will contain an API and javascript to call and read from said API.
-A back end, written in Java. Detailled below.
-A database which provided information for the front end and is populated by the backend.

Currently, the majority of the code is written in the backend, which is a service which will run, say, every day, and
populate the database. In order to accomplish this, the backend:
-Checks all given "alias" records, which is a note of all the records under one player to be united under those of another.
	-For example: records for Joe should be combined with records of Joe F
-Saves all these aliases, and all further calculations take this into consideration
	-all data is saved under the primary name and the primary name alone.
	-Supports backwards compatibility. Adding an alias and re running the calculation after records have been created
	 under both accounts results in the all the records for the alias part of the name:alias to be united under those
	 of the "name" player.
		-However, this simply averages their elo scores (weighted by number of matches played), instead of recalculating
-Polls Challonge's API for all tournaments played under each api key given.
	-Multiple subdomains are supported
-For all returned tournaments in each account (given by an api key)
	-Gets all the players, and creates records for each.
		-Bad players are filtered out. These can be set by the end user.
			-For example, any players named "Spacer" are filtered out currently.
	-Gets all the matches, and performs calculations for each match.
		-This is done to keep an updated elo score for each player as time moves on.
		-Filters bad matches and performs no calculations for those.
			-For example, disqualifications.
	
	
-Email alerts are sent to a systems administrator whenever something fails.
-All events are logged, with times started and time elapased.
-Many other relevant settings are available for an end user to select, without having any access to the source code.

Planned:
-Daily reports sent to an end user and a systems administrator