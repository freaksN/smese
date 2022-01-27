This project contains the Spring Boot Backend and the ReactJS Frontend of the SMESE - Prototype all bundled together.

To run the project make sure port 8080 is free and no other application is currently using it and that you also have at least Java 1.8 installed and that Java is also added to your environment variables!
To be able to build the prototype make sure you have at least Maven 3.6.3 installed and that Maven is also added to your environment variables!

How to run the SMESE prototype:
The fastest and easiest way to start the prototype is to use the provided JAR file within the directory of the project:

1. Open your console: navigate to this project's directory and run the following command:

java -jar SMESE-prototype-0.0.1-SNAPSHOT.jar

2. After a couple of seconds you will be informed that the application is started and it is running on port 8080.
3. Finally, to interact with the prototype open your browser and go to the following address:  localhost:8080. 

      
If the provided JAR file is corrupted or you would like to build the project yourself and create your own JAR you could either import and build the project within your IDE or build the project using your console and Maven:

To build and run the project without importing it into an IDE:

1. Make sure you have at least Maven 3.6.3 and Java 1.8 installed and both of them are added to your environment variables.
2. Open the pom.xml file within the project directory and uncomment everything from line 155 to 166.
3. Then add within the <executable></executable> property in the pom.xml the path to your javac.exe, which is normally located inside the bin folder of your installed JDK.

In the provided pom.xml there is an example path available for my system. Just adjust it accordingly to yours.
        
4. Now using your console you can navigate to the project's directory and run 

mvn clean install

After a couple of minutes you will have a successfull build and a JAR file named SMESE-prototype-0.0.1-SNAPSHOT.jar will be generated inside the target/ directory.

Afterwards within your console navigate to  the target/ directory and run the following command to start the application on port 8080.

java -jar SMESE-prototype-0.0.1-SNAPSHOT.jar

Finally, to interact with the prototype open your browser and go to the following address:  localhost:8080. 

 To build and run the project using your favorite IDE:

Make sure to import the project as a Maven project and that you are using at least Java 1.8.

Then make sure Maven and the correct version of Java are added to your IDE's settigs. Afterwars just use Maven to do a  clean install. The provided pom.xml contains the Maven build to generate the sources, the dependencies and the JAR file. 
        
Importing a maven project may vary from IDE to IDE. Please follow the instructions for your chosen IDE_

Once you have a successful build. You can start the application within your IDE. 

Finally, to interact with the prototype open your browser and go to the following address:  localhost:8080. 


Hint: The CoreNLP libraries adds the different annotators one by one to the pipeline. As a result it takes a little bit of extra time to initiate the semantic metadata enrichment process only for the **first** uploaded file. However, this is only the case for the **first** uploaded file. For each uploaded file after this the process is not slowed down since all annotators are already added to the pipeline.