MySql README
This file should aid in installing and setting up MySql.

First, roughly follow this guide:
https://www.elated.com/articles/mysql-for-absolute-beginners/
for installation.

Once installed, run this command:
TODO

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