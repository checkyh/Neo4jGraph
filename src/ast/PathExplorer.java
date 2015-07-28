package ast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * get: 
 * <li>all java files' filenames and paths</li> 
 * <li>all the source directories's paths</li>
 * <li>all the binary files' paths</li>
 * in a project
 *
 */
public class PathExplorer {
	
	private List<String> filePaths = new ArrayList<String>();
	private List<String> fileNames = new ArrayList<String>();
	private List<String> srcs = new ArrayList<String>();
	private List<String> bins = new ArrayList<String>();
	
	public PathExplorer(String dirPath) {
		readDirectory(dirPath);
	}

	/**
	 * recursively read all files under path <code>dirPath</code>
	 * 
	 * @param dirPath Path of Directory
	 * @throws IllegalStateException if <code>dirPath</code> is illegal
	 */
	private void readDirectory(String dirPath) {
		
		File dir = new File(dirPath);

		if (!dir.exists()) {
			throw new IllegalStateException("Illegal Directory Path: "
					+ dir.getAbsolutePath());
		}

		for (File subdir : dir.listFiles()) {
			if (subdir.isDirectory()) {
				if (subdir.getName().trim().equals("bin")) {
					bins.add(subdir.getAbsolutePath());
				} else if (subdir.getName().startsWith("src")) {
					srcs.add(subdir.getAbsolutePath());
				}
				// recursively read subdirectories except bin/
				if (!subdir.getName().startsWith("bin")) {	
					readDirectory(subdir.getPath());
				}
			} else {
				if (subdir.getName().endsWith(".java")) {
					filePaths.add(subdir.getAbsolutePath());
					fileNames.add(subdir.getName());
				} else if (subdir.getName().endsWith(".jar")) {
					bins.add(subdir.getAbsolutePath());
				}
			}
		}
	}

	public List<String> getFilepaths() {
		return filePaths;
	}

	public List<String> getFileNames() {
		return fileNames;
	}

	public String[] getSourcePaths() {
		return srcs.toArray(new String[srcs.size()]);
	}

	public String[] getClassPaths() {
		return bins.toArray(new String[bins.size()]);
	}

	public static void main(String[] args) {
		String dirPath = "D:\\Java-Projects\\Git\\Neo4jGraph\\";
		PathExplorer files = new PathExplorer(dirPath);
		System.out.println("filePaths:");
		for (String filepath : files.getFilepaths()) {
			System.out.println(filepath);
		}
		System.out.println("fileNames:");
		for (String name : files.getFileNames()) {
			System.out.println(name);
		}
		System.out.println("srcs:");
		for (String src : files.getSourcePaths()) {
			System.out.println(src);
		}
		System.out.println("bins:");
		for (String bin : files.getClassPaths()) {
			System.out.println(bin);
		}
	}
}
