SPORTS TOURNAMENT MANAGEMENT SYSTEM - MYSQL VERSION

REQUIREMENTS
- Java JDK 17 or later
- MySQL Server 8.0 or later
- MySQL Connector/J (mysql-connector-j-<version>.jar)
- Python 3

STEP 1 - DATABASE SETUP
1. Open MySQL Workbench.
2. Connect to your local MySQL server.
3. Open Database/sports.sql in a query tab.
4. Click the lightning icon (Execute).
5. The script creates the sports_management database, six tables and sample records.

STEP 2 - SET MYSQL PASSWORD
1. Open Java/DBConnection.java.
2. Replace root with your MySQL username if different.
3. Replace your_mysql_password with your MySQL password.
4. Open Python/analysis.py and enter the same MySQL username/password in DB_CONFIG.

STEP 3 - ADD MYSQL JDBC DRIVER
1. Download MySQL Connector/J from the official MySQL website.
2. Place the downloaded mysql-connector-j-<version>.jar inside the Java folder.
3. Use the exact JAR filename in the commands below.

STEP 4 - RUN JAVA PROJECT (Windows VS Code terminal)
Open terminal inside the Java folder and run:

javac -cp ".;mysql-connector-j-<version>.jar" Main.java DBConnection.java
java -cp ".;mysql-connector-j-<version>.jar" Main

Replace <version> with the version in your downloaded JAR filename.
On macOS/Linux, use : instead of ; in the classpath.

STEP 5 - RUN PYTHON ANALYSIS
Open terminal inside the Python folder and run:

pip install pandas matplotlib mysql-connector-python
python analysis.py

The charts will be saved in the visualizations folder.

FEATURES
- Team add, view, update and delete
- Player add, view, update and delete
- Create and view tournaments
- Register teams for tournaments
- Schedule matches for registered teams
- Record match scores and determine winners
- View match results
- Tournament standings (win = 3 points, draw = 1 point, loss = 0 points)
- Python performance analysis and five charts

IMPORTANT
- Use MySQL Server 8.0 or later for CHECK constraint enforcement.
- Use unique IDs when adding teams, players, tournaments and matches.
- Register both teams in a tournament before scheduling their match.
- Only completed matches are included in standings and charts.
- If you already created tables using the earlier PostgreSQL script, use a fresh MySQL database; PostgreSQL and MySQL databases are separate.
