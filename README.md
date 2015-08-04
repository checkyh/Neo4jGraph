# Neo4jGraph

Neo4jGraph stores Java AST into Neo4j database.

# Setup

### 1. install Neo4j

Download the latest version of Neo4j database from http://neo4j.com/download/other-releases/. 
The project is developed with Neo4j 2.2.3. 

### 2. install ASTView plug-in for eclipse (optional)

You can find ASTView plug-in in Eclipse Marketplace.

### 3. set classpath

Select _Project->Properties->Java Build Path_ to set your classpath.

Add _.gitignore_ under your project directory to avoid pushing _.classpath_ to repo:

	.gitignore
	.classpath
	
	*.class
	*.jar
	*.ini
	
	/bin/
	/database/

# Run the code

### Configuration file

Before running the code, you need to set options in configuration file `./config.ini`. Here's the demo of file content:

	database.directory=D:\Neo4j-Database\Neo4jGraph
	project.directory=D:\Java-Projects\example
	
Users should ensure that `database.directory` and `project.directory` options are correctly set in configuration file, or the behavior of this program is not defined.

### View the result

After the program finished, open the Neo4j server and land on http://localhost:7474/ to view the result. Maybe you need to learn something about **Cypher** to continue.

### Notes

+ You must not run the code with the Neo4j server running, or you will get a Java Runtime Exception.





