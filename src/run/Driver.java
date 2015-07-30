package run;

import java.io.IOException;

import neo4j.Neo4j;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Driver {
	
	private static Logger logger = Logger.getLogger(Driver.class);

	public static void main(String[] args) {
		
		PropertyConfigurator.configure("log4j.properties");
		
		try {
			Option.readOption("./config.ini");
		} catch (IOException e) {
			logger.error("fail to get global configuration.");
			return;
		}
		
		StoreWorker worker = new StoreWorker();
		
		Neo4j neo4j = Neo4j.open(Option.DATABASE_DIR, Neo4j.WRITE);
		neo4j.run(worker);
		neo4j.shutdown();
		
		logger.info("Done.");
	}

}
