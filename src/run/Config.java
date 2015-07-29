package run;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Config {
		
	public static String PROJECT_DIR = null;
	public static String DATABASE_DIR = "./database";
	public static String PACKAGE = "testcase";
	public static String FILENAME = "HelloWorld.java";

	public static void getProjectPath() throws IOException {
		// get project directory
		File file = new File(".");
		PROJECT_DIR = file.getCanonicalPath();
	}
	
	/**
	 * read from config file and set all its fields
	 * @param filename config file name
	 * @throws IOException if config file cannot be opened
	 */
	public static void readConfig(String filename) throws IOException {
		
		// read config info from file
		BufferedReader in = new BufferedReader(new FileReader(filename));
		while (true) {
			String line = in.readLine();
			if (line == null) {
				break;
			}
			String[] fields = line.split("=");
			String name = fields[0].trim();
			String value = fields[1].trim();
			if (name.equals("database.directory")) {
				DATABASE_DIR = value;
			} else if (name.equals("package")) {
				PACKAGE = value;
			} else if (name.equals("filename")) {
				FILENAME = value;
			}
		}
		in.close();
	}

}
