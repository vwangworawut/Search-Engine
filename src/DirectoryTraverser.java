import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;


public class DirectoryTraverser {

	/**
	 * 
	 * @param path 
	 * 			location of file
	 * @param fileNames 
	 * 			set of file names
	 */
	private static void traverse(Path path, String suffix, HashSet<String> fileNames) {
		try (DirectoryStream<Path> listing = Files.newDirectoryStream(path)) {
			for (Path file : listing) {
				if (Files.isDirectory(file)) {
					traverse(file, suffix, fileNames);
				}
				else {
					if (file.toString().toLowerCase().endsWith(suffix)) {
						fileNames.add(file.toString());
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Safely starts the recursive traversal with the proper padding. Users
	 * of this class can access this method, so some validation is required.
	 *
	 * @param directory to traverse
	 * @throws IOException
	 */
	public static HashSet<String> traverse(Path directory) {
		HashSet<String> fileNames = new HashSet<>();
		if (Files.isDirectory(directory)) {
			traverse(directory, ".txt", fileNames);
		}
		return fileNames;
	}
}
