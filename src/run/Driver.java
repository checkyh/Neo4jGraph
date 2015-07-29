package run;

import java.io.IOException;

import neo4j.Neo4j;

public class Driver {

	public static void main(String[] args) {
		
		try {
			Config.getProjectPath();
			Config.readConfig("./config.ini");
		} catch (IOException e) {
			System.out.println("Fatal Error: fail to get global configuration.");
			return;
		}
		
		StoreWorker worker = new StoreWorker();
		
		Neo4j neo4j = Neo4j.open(Neo4j.WRITE);
		neo4j.run(worker);
		neo4j.shutdown();
	}

}
