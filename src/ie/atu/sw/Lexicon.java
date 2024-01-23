package ie.atu.sw;

// Imports
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * The <b>Lexicon</b> class is used for creating a map containing the associated words of the lexicon and their sentiment score.
 * Provides methods for loading a lexicon from a file, retrieving sentiment score of a specific word and store in skip list map.
 */
public class Lexicon {
	
	/**
	 * Map of lexicon words and their sentiment scores

	 *
	 */
    Map<String, Double> wordScores = new ConcurrentSkipListMap<String, Double>();

    /**
     * Loads the lexicon from file, filling the map with word and it's score pair.
     * 
     * @param lexiconFile - File path of lexicon.
     * @throws IOException
     */
    void loadLex(String lexiconFile) throws IOException{
        // Load the lexicon using bufferedReader
        BufferedReader br = new BufferedReader(new FileReader(lexiconFile));
        String line;
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(","); // Split based on comma
            wordScores.put(parts[0], Double.parseDouble(parts[1])); // fill map with it's key value pairs
            //Debug
            //System.out.println("Word: " + parts[0] + "\nDouble: " + parts[1]);
        }
        br.close(); // Close bufferedReader
    }
    
    /**
     * Retrieves sentiment score for specific words from lexicon.
     * 
     * @param word - word associated with sentiment score.
     * @return - Sentiment score of specific word.
     */

    double getScore(String word) {
        // Return the score of word if present in map
        if (wordScores.containsKey(word)) {
            return wordScores.get(word);
        }
        return 0;
    }

}
