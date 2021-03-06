# Challonge Elo Calculator
This is the source code for a web app which has a few major components.
- A front end, written in Django. Detailled below.
- A back end, written in Java. Detailled below.
- A database which provided information for the front end and is populated by the backend.

## Overview
- The backend webservice runs once a day (configurable).
- When it runs, it processes data and places it into the backend MySQL database.
  - For more information, see the backend section below.
- Then, the frontend accesses this data and displays it to the user on a webpage.

## Navigation
- The top level folder with the name matching that of the repo (Challonge-Elo-Calculator) is the folder of the eclipse project folder.
  - This contains all files for "the backend".
  - If a java API is built for the front end, it will also be placed here, but in a different package.
  - Each package should be compiled as a jar file.
	- The "backend" jar file will be what is run once a day to fill in the database with new info.
- The top level folder named ChallongeEloSite contains all code for the front end. This is a Django "project".
  - Inside this folder, another folder named ChallongeEloSite contains all the code for the "site", managed by Django.
  - The other folder inside this folder, named display, is the "app" folder for Django.
  - Django takes the most of the actions given by Display and performs them within the "site".
  - Most of the interesting code is contained in the app folder ("Display").
  - More information available here: https://docs.djangoproject.com/en/2.0/intro/tutorial01/ -- Search the page for "app".
-The top level folder named Stored Procedures contains all stored procedures created on the database.
  -RefreshSprocs.sh will run all these sproc creation files, ensuring the database is up to date.
  

## The Frontend
- Written on Python's Django.
- Displays every player entry in the database, along with their elo.
- Also displays every opponent they've ever played, and the count of matches between the two of them.

## The Backend
Currently, the majority of the code is written in the backend, which is a service which will run, say, every day, and
populate the database. In order to accomplish this, the backend:
- Checks all given "alias" records, which is a note of all the records under one player to be united under those of another.
  - For example: records for Joe should be combined with records of Joe F
- Saves all these aliases, and all further calculations take this into consideration
  - all data is saved under the primary name and the primary name alone.
	- Supports backwards compatibility. Adding an alias and re running the calculation after records have been created
	  under both accounts results in the all the records for the alias part of the name:alias to be united under those
	  of the "name" player.
	  - However, this simply averages their elo scores (weighted by number of matches played), instead of recalculating
- Polls Challonge's API for all tournaments played under each api key given.
  - Multiple subdomains are supported
- For all returned tournaments in each account (given by an api key)
  - Gets all the players, and creates records for each.
	- Bad players are filtered out. These can be set by the end user.
	  - For example, any players named "Spacer" are filtered out currently.
	- Gets all the matches, and performs calculations for each match.
	  - This is done to keep an updated elo score for each player as time moves on.
	  - Filters bad matches and performs no calculations for those.
	    - For example, disqualifications.
	- All tournaments are processed in the order they occurred, in order to keep elo changes consistent.
	
- Email alerts are sent to a systems administrator whenever something fails.
- A daily email report is sent to the end user in order to inform them of actions taken on the day (tournaments processed, etc).
- All events are logged, with times started and time elapased.
- Many other relevant settings are available for an end user to select, without having any access to the source code.
- New teams are entered with an elo taken as the average of both individuals, if they exist as players.
  - This is done to avoid creating teams of very skilled individuals who enter one tournament with 1000 elo, and unfairly
    causing established teams with 1200 elo to take larger elo hits than deserved against these teams.

## Planned
- Once the front end is complete, there should be a way to log into an administrator account.
  - This account will be able to call API methods indirectly, to do things like reset the database, and more.
  - For this reason, some of the methods in the backend jar file will need to be accesible to the API.