package ast;

import neo4j.Neo4j;

public class Driver {

	public static void main(String[] args) {
		
		String filepath = "D:\\Java-Projects\\Neo4jGraph\\src\\testcase\\";
		String filename = filepath + "HelloWorld.java";
		
		StoreWorker worker = new StoreWorker(filename);
		
		System.out.println("start");
		
		String dirPath = "M:\\Neo4j-Database\\neo4jgraph";
		Neo4j neo4j = new Neo4j(dirPath);
		System.out.println("ready");
		neo4j.clear();
		neo4j.run(worker);
		neo4j.shutdown();
		
		System.out.println("done");
	}

}
