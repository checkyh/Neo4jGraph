package ast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * get: 
 * <li>all java files' names and paths</li> 
 * <li>all the source directories's paths</li>
 * <li>all the binary files' paths</li>
 * in a project
 *
 */
public class JavaFiles {
	private List<String> filepaths = new ArrayList<String>();
	private List<String> names = new ArrayList<String>();
	private List<String> sources = new ArrayList<String>();
	private List<String> targets = new ArrayList<String>();

	/**
	 * recursively read all files under path <code>dirPath</code>
	 * 
	 * @param dirPath Path of Directory
	 * @throws IllegalStateException if <code>dirPath</code> is illegal
	 */
	public void readDirectory(String dirPath) {
		
		File dir = new File(dirPath);

		if (!dir.exists()) {
			throw new IllegalStateException("Illegal Directory Path: "
					+ dir.getAbsolutePath());
		}

		for (File subdir : dir.listFiles()) {
			if (subdir.isDirectory()) {
				if (subdir.getName().trim().equals("bin")) {
					targets.add(subdir.getAbsolutePath());
				} else if (subdir.getName().startsWith("src")) {
					sources.add(subdir.getAbsolutePath());
				}
				// recursively read subdirectories except bin/
				if (!subdir.getName().startsWith("bin")) {	
					readDirectory(subdir.getPath());
				}
			} else {
				if (subdir.getName().endsWith(".java")) {
					filepaths.add(subdir.getAbsolutePath());
					names.add(subdir.getName());
				} else if (subdir.getName().endsWith(".jar")) {
					targets.add(subdir.getAbsolutePath());
				}
			}
		}
	}

	public List<String> getFilepaths() {
		return filepaths;
	}

	public List<String> getNames() {
		return names;
	}

	public String[] getSources() {
		return sources.toArray(new String[sources.size()]);
	}

	public String[] getTargets() {
		return targets.toArray(new String[targets.size()]);
	}

	public static void main(String[] args) {
		JavaFiles files = new JavaFiles();
		files.readDirectory("D:\\Java-Projects\\Git\\Neo4jGraph\\");
		System.out.println("filepaths:");
		for (String filepath : files.getFilepaths()) {
			System.out.println(filepath);
		}
		System.out.println("names:");
		for (String name : files.getNames()) {
			System.out.println(name);
		}
		System.out.println("sources:");
		for (String source : files.getSources()) {
			System.out.println(source);
		}
		System.out.println("targets:");
		for (String target : files.getTargets()) {
			System.out.println(target);
		}
	}
}
