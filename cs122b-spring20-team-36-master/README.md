CS 122B Project 5

- # General
    - #### Team#:36
    
    - #### Names:Leon Luo, Matthew Tran
    
    - #### Project 5 Video Demo Link: https://youtu.be/0ijKZsHGr8k

    - #### Instruction of deployment:

	1) clone github repo
	2) cd into repo
	3) create war files with mvn
	4) move war files to Tomcat/Webapps

    - #### Collaborations and Work Distribution:

	Leon Luo:
		Master-Slave Replication
		Scaling w/ Load Balancer
		JMeter
	Matthew Tran:
		JDBC Connection Pooling
		Master-Slave Replication
		General Debugging


- # Connection Pooling
    - #### Include the filename/path of all code/configuration files in GitHub of using JDBC Connection Pooling.

	https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-36/blob/master/cs122b-spring20-project1/WebContent/META-INF/context.xml
    
    - #### Explain how Connection Pooling is utilized in the Fabflix code.
	
	Whenever connecting to Database, we grab a connection from a pool of connections rather than starting and closing one manually by ourselves.
    
    - #### Explain how Connection Pooling works with two backend SQL.
	
	We had a pool of connections for the Master Database when we want to update.
	Otherwise, each instance of Tomcat had a pool of connection to its local MySql database.
    

- # Master/Slave
    - #### Include the filename/path of all code/configuration files in GitHub of routing queries to Master/Slave SQL.

	Servlets that require Writing and connection to Master:
	
	https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-36/tree/master/cs122b-spring20-project1/src/NewMovie.java
	https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-36/tree/master/cs122b-spring20-project1/src/NewStar.java
	https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-36/tree/master/cs122b-spring20-project1/src/PaymentServlet.java

	Config:

	https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-36/blob/master/cs122b-spring20-project1/WebContent/META-INF/context.xml

    - #### How read/write requests were routed to Master/Slave SQL?
		
	We use a different pool of connections to connect to the Master Database when we wanted to update the database.
	Otherwise, read requests would be forwarded to the local database.
    
######## NOTE ##########
We had the login filter disabled for the JMeter Reportings
######## NOTE ##########

- # JMeter TS/TJ Time Logs
    - #### Instructions of how to use the `log_processing.*` script to process the JMeter logs.

	For a single instance, run the command:
		python3 log_processing.py [filenames]
	where filename is the name of the log file.
	*Note have python3 installed

- # JMeter TS/TJ Time Measurement Report

| **Single-instance Version Test Plan**          | **Graph Results Screenshot**                                                                                                         | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis**                                                                                             |
|------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------|----------------------------|-------------------------------------|---------------------------|----------------------------------------------------------------------------------------------------------|
| Case 1: HTTP/1 thread                          | ![](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-36/blob/master/jmeter-pictures/single-http-pooling-1.PNG)            | 155                        | 0.763420                            | 0.880118                  | When browsing, most of time comes from Transmission as indicated by the high Average Query Time          |
| Case 2: HTTP/10 threads                        | ![](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-36/blob/master/jmeter-pictures/single-http-pooling-10.PNG)           | 157                        | 1.038268                            | 1.145941                  | When browsing, most of time comes from Transmission as indicated by the high Average Query Time          |
| Case 3: HTTPS/10 threads                       | ![](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-36/blob/master/jmeter-pictures/single-https-pooling-10.PNG)          | 79                         | 0.970671                            | 1.062026                  | Shorter query time than other cases because we are not being redirected to another page                  |
| Case 4: HTTP/10 threads/No connection pooling  | ![](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-36/blob/master/jmeter-pictures/single-nopooling.PNG)                 | 157                        | 1.081501                            | 1.223627                  | When browsing, most of time comes from Transmission as indicated by the high Average Query Time          |

| **Scaled Version Test Plan**                   | **Graph Results Screenshot**                                                                                                         | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis**                                                                                             |
|------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------|----------------------------|-------------------------------------|---------------------------|----------------------------------------------------------------------------------------------------------|
| Case 1: HTTP/1 thread                          | ![](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-36/blob/master/jmeter-pictures/scaled-pooling-1.PNG)                 | 169                        | 0.801490                            | 0.941777                  | When browsing, most of time comes from Transmission as indicated by the high Average Query Time          |
| Case 2: HTTP/10 threads                        | ![](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-36/blob/master/jmeter-pictures/scaled-pooling-10.PNG)                | 172                        | 0.750256                            | 0.882395                  | When browsing, most of time comes from Transmission as indicated by the high Average Query Time          |
| Case 3: HTTP/10 threads/No connection pooling  | ![](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-36/blob/master/jmeter-pictures/scaled-nopooling.PNG)                 | 170                        | 0.784518                            | 0.914131                  | When browsing, most of time comes from Transmission as indicated by the high Average Query Time          |


============================================================================================================================================
============================================================================================================================================


CS122B Project 4

Demo: https://www.youtube.com/watch?v=VZ19awYE99s <br/>
<pre>
Deployment Instructions:<br/>
	built a WAR file with Maven and had Tomcat deploy the war file<br/>
	built an APK of our app and dragged it onto an emulator<br/>

Contribution:<br/>
	Matthew Tran: Autocomplete<br/>
	Leon Luo: Autocomplete and Android app<br/>
		I take full responsibility for completing the Android<br/>
		app on my own. My partner was having trouble with SDK<br/>
		installation and getting an emulator to work. We spent<br/> 
		a majority of our time on the Android app not coding<br/>
		but trying to figure out the issue, which ended<br/>
		nowhere. I thought it'd be better if one of us started<br/>
		working on the project rather than spend hours solving<br/>
		an issue that we had zero knowledge about.<br/> 
</pre>
==========================================================
CS122B Project 3

Link to Demo: https://www.youtube.com/watch?v=8dSgjhnb4lU

To Deploy the XML Parser you will need to run the following commands in the directory of the pom.xml:

You will also need to move the appropriate XML files in this directory
mvn clean package
mvn exec:java -Dexec.mainClass="MovieStarSAXParser"

Prepared Statements MoviesServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-36/blob/master/cs122b-spring20-project1/src/MoviesServlet.java
ConfirmationServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-36/blob/master/cs122b-spring20-project1/src/ConfirmationServlet.java
EmployeeLoginServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-36/blob/master/cs122b-spring20-project1/src/EmployeeLoginServlet.java
LoginServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-36/blob/master/cs122b-spring20-project1/src/LoginServlet.java
NewMovie https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-36/blob/master/cs122b-spring20-project1/src/NewMovie.java
NewStar https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-36/blob/master/cs122b-spring20-project1/src/NewStar.java
PaymentServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-36/blob/master/cs122b-spring20-project1/src/PaymentServlet.java
ShoppingCart https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-36/blob/master/cs122b-spring20-project1/src/ShoppingCart.java
SingleMovie https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-36/blob/master/cs122b-spring20-project1/src/SingleMovie.java
SingleStarServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-36/blob/master/cs122b-spring20-project1/src/SingleStarServlet.java

2 Parsing Times Optimization Strategies that we used are:

Load database into memory so that we do not have create a query everytime to get data.

We compiled all of the new data and stored it in a file where we would later load it in to mySQL using the command: LOAD DATA

Data Report: https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-36/blob/master/XMLParser/report.txt

Contributions:

Leon Luo: Recaptcha Prepared Statements XML Parser HTTPS

Matthew Tran: HTTPS Encryption Dashboard + Stored Procedure

Both: Debugging both Front and Backend

=======================================

CS122b Project 2

Link to Demo: https://www.youtube.com/watch?v=_GaT7xCsDsk

We created an additional table to give each movie a price. Using this SQL statement:

CREATE TABLE prices as SELECT id as movieId, 10 as price FROM movies;

We also had to add an additional column named "quantity" in sales to account for when users buy multiple movies using this SQL statement.

ALTER TABLE sales ADD COLUMN quantity int;

Our substring matching design matched any string of the following format: %[string]%. We allow the search word to be a substring of any word in the database query.

Contributions:

Leon Luo: Java servlets and javascripts

Matthew Tran: HTML and javascripts

========================================

CS122b Project 1

Link to Demo: https://youtu.be/XLFvEarwN38

We deploy our application with Tomcat by first creating a .war file using Maven. Once we built our .war file, we loaded it using Tomcat, the same way the Instructor taught us.

Contributions:

Leon Luo: Servlets Files

Matthew Tran: HTML and JS files

Both: Debugging

=====================================================
