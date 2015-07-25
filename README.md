# Neo4jGraph

Neo4jGraph stores Java AST into Neo4j database.

# Setup

### 1. install Neo4j

Download the latest version of Neo4j database from http://neo4j.com/download/other-releases/. 
The project is developed with Neo4j 2.2.3. 

### 2. get dependent .jar files

#### List of eclipse jar files 

(All can be found in **/eclipse/plugin/** directory):

+ org.eclipse.core.contenttype_3.4.100.v20110423-0524.jar
+ org.eclipse.core.jobs_3.5.101.v20120113-1953.jar
+ org.eclipse.core.resources_3.7.101.v20120125-1505.jar
+ org.eclipse.core.runtime_3.7.0.v20110110.jar
+ org.eclipse.equinox.common_3.6.0.v20110523.jar
+ org.eclipse.equinox.preferences_3.4.2.v20120111-2020.jar
+ org.eclipse.jdt.core_3.7.3.v20120119-1537.jar
+ org.eclipse.osgi_3.7.2.v20120110-1415.jar

#### List of neo4j jar files

Download the .zip file from http://neo4j.com/download/other-releases/ and you can
find all the .jar files.

### 3. install ASTView plugin for eclipse (optional)

You can find ASTView plugin in Eclipse Marketplace.

# Run the code

All parameters are hard-written in run/Driver.java, you need to modify the code to change them.


