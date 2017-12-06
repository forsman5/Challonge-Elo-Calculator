MySql README
This file should aid in installing and setting up MySql.

First, roughly follow this guide:
https://www.elated.com/articles/mysql-for-absolute-beginners/
for installation.

Once installed, run this command:
TODO

For connecting using mySQL shell, use this command initially: \connect root@localhost

Upon restarting the computer, the sql server must be restarted (if not selected to be done automatically).
To do so:

"
From what I've gathered this means the mysql service (mysqld) isn't running.

First make sure it is installed as a windows service : 
Open the command line as an administrator.
Run
"C:\Program Files\MySQL\MySQL Server 5.7\bin\mysqld" --install

Next, start it up :
services.msc > MySQL > start "

When the services page opens, start MySQL. (Not the router).

For installing all the stored procedures (must be done before executing any java):
Ensure you have git, or some other tool that allows you to run .sh files installed.
Be sure that your mysql\bin folder is part of your $PATH variable:

Go to Control Panel -> System -> Advanced
Click Environment Variables
Under System Variables find PATH and click on it.
In the Edit windows, find the end of the string of paths (each path needs to be separated by a semi-colon ";" so you may need to add that to the end) add the path to your mysql\bin folder to the end paths.
Go back to Desktop
Close any command prompts you may have open Got to do this so the new $PATH variable will load.
Re-open command prompt. Try running mysql -uroot and if the path was set correctly, mysql will now run.

Open the command prompt in this location. 
Either open by right clicking, or open anywhere and issue the command
cd path\to\sproc\folder

Then,
for Windows: run sh RefreshSprocs.sh
for Linux: ./RefreshSprocs.sh