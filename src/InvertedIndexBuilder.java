import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;

public class InvertedIndexBuilder {
	
	private final InvertedIndex index;
	
	public InvertedIndexBuilder(InvertedIndex index) {
		this.index = index;
	}
	

	// TODO Remove InvertedIndex from parameters.
	public void buildIndex(Path directory) {
		HashSet<String> fileNames = new HashSet<String>();
		fileNames = DirectoryTraverser.traverse(directory);
		for (String file: fileNames) {
	        buildIndexHelper(file, index);
		}
	}
	
	public static void buildIndexHelper(String file, InvertedIndex index) {
		try (BufferedReader reader = Files.newBufferedReader(Paths.get(file),
                Charset.forName("UTF-8"));) {
        	int counter = 1;
            String line = null;

            while ((line = reader.readLine()) != null) {
            	if (!line.isEmpty()) {
	            	String[] split = line.split("\\s+");
	            	for (String s: split) {
	            		String cleaned = cleanWord(s);
	            		if (!cleaned.isEmpty()) {
		            		index.add(cleaned, file, counter);
		            		counter++;
	            		}
	            	}
	            }
            }
        } catch (IOException e){
        	e.printStackTrace();
        }
	}
	
	
	public static String cleanWord(String word) {
		return word.toLowerCase().replaceAll("\\p{Punct}+", "") .trim();
	}
}