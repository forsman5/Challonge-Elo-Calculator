---END USERS---
Do not redistribute.
To use, simply run the run.bat file.
Output will be found in the data folder, with Records.html. Click through there to view results.
The files currently in \data\tournaments will all be parsed. Change those files to suit your needs.
To change the directory, edit the doc\config.cfg file, to, for example, include only results from the \data\tournaments\2015 folder.

--IMPORTANT--
In all data files, users names may not contain the characters '-'.

Developed by Joe Forsman.
jrforsman@gmail.com
@PowellSmash

Goals

-Change tourney files to use .tm file ending, not .txt.
-Integrating the old SmashProcess.java files to continue beta testing.
-Finding a way to host the files and adjusting the paths accordingly.

Changelog

Version 0.6, 1/11
-Functioning Google Drive addition means the depreciation of the redistributable format.
-Added full Google Drive webhosting application.
	-This includes changing it so index.html (the renamed topfile) is now output with the records.
		-This means that the PLAYER_PATH variable is now gone, and the code paths are simplified.

Version 0.5, 1/10
-Added a way to mark tournaments as defunct through their location in memory. A defunct tournament's points 
	will not be counted towards power ranking totals.
-Added a link to the main page at the top of all player pages.
-Added a link to the player the record is showing against.

Version 0.4, 1/5
-Added a "Last Updated" field to the PR.
-Added links to tournaments.

Version 0.3, 1/5
-Added configuration file to change directories.
-Updated tournament files to contain new information which will be relevant in future releases.
	-Includes Singles, Doubles, S4, and Crews placement and point gain.
-Squashed a future potential bug which could arise if there were two sets with the same score 
	and same participants in the same tourney.
-Added extremely basic comment functionality in tournament files -- be careful!
	One of the only places comments ("#") work is shown as an example in data\tournaments\2015\SMYG1.txt
-Added PR and tournament lists.	

Version 0.2, 1/3
-Few bugs squashed.
-Made much more adaptable, proper for redistributable release.
-Updated html output slightly.
-Few optimization changes.

Version 0.1, 1/2
-Data files compiled. Currently composed of all PowellSmash tournies, from Season Opener to 1/4/2015.
-First working completion of CompileStats.java. Hardcoded and ugly.