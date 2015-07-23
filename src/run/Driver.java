package run;

import neo4j.Neo4j;

public class Driver {

	public static void main(String[] args) {
		
		String filepath = "D:\\Java-Projects\\Git\\Neo4jGraph\\src\\testcase\\";
		String filename = filepath + "SwapArrayElements.java";
		
		StoreWorker worker = new StoreWorker(filename);
		
		String dirPath = "M:\\Neo4j-Database\\neo4jgraph";
		Neo4j neo4j = new Neo4j(dirPath);
		neo4j.clear();
		neo4j.run(worker);
		neo4j.shutdown();
	}

}
