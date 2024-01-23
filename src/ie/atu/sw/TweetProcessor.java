package ie.atu.sw;

// Imports
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors; // For threads
import java.util.concurrent.atomic.AtomicReference; // for doing Atomic Doubles
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.lang.InterruptedException;

/**
 *  The <b>TweetProcessor</b> class processes tweets from a tweet file, 
 *  calculating sentiment scores using lexicon.
 *  Gets result asynchronously using virtual threads across all the tweets.
 * 
 */
public class TweetProcessor {
    private Lexicon lexicon; // Initialise instance of lexicon
    private AtomicReference<Double> SentimentOverall = new AtomicReference<>(0.0); // Set Atomic Number for keeping track of total sentiment

    /**
     * Constructor for TweetProcessor instance with specified lexicon.
     * 
     * @param lexicon - The lexicon to used for sentiment analysis.
     */
    public TweetProcessor(Lexicon lexicon) {
        this.lexicon = lexicon;
    }

    /**
     * retrieves the overall sentiment calculated by the TweetProcessor.
     * 
     * @return - The overall sentiment double.
     */
    public double getSentimentOverall() {
        return SentimentOverall.get();
    }
    
    /**
     * Processes tweets from tweetsfile, asynchronous calculations using 
     * virtual threads and atomic number of sentiment scores, returning overall.
     * 
     * @param tweetsFile - The file path containing tweets for processing.
     * @return - overall Sentiment score of tweets.
     * @throws IOException
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public double processTweets(Path tweetsFile) throws IOException, InterruptedException , ExecutionException {
        // Read in the tweets
        List<String> tweets = Files.readAllLines(tweetsFile);
        // Set up futures ArrayList to store completableFutures of each tweet
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        // Attempt to parse and calculate across pool of virtual threads using executor
        try (var pool = Executors.newVirtualThreadPerTaskExecutor()) {
            // for each singular tweet in tweets file
        	for (String tweet : tweets) {
        		// Create a CompletableFuture to process a tweet asynchronously
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    double sentiment = 0.0; // Initialise starting sentiment for current tweet
                    // Split tweet into words
                    String[] line = tweet.split(" ");
                    // for each word in a line
                    for (String word : line) {                    	
                    	// calculate sentiment score from lexicon and add to current sentiment
                    	sentiment += lexicon.getScore(word.replaceAll("[^a-zA-Z]","").toLowerCase());
                        
                        //Debug
                        //System.out.println("Word: " + word + ", Sentiment: " + sentiment);
                    }
                    // Update the overall sentiment with the last current using atomic
                    SentimentOverall.getAndAccumulate(sentiment, Double::sum);
                    
                    
                    //Debug - Displays process of sentiment calculation
                    //System.out.println("Tweet: " + tweet + ", Sentiment: " + sentiment + ", SentimentOverall: " + SentimentOverall.get());
                    //System.out.println("******************************");
                }, pool);
                
                // Add the completableFuture for current tweet to list of futures
                futures.add(future);    
            }
        }

        // Wait for all the futures to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();

        // Return end result of overall sentiment after calculations
        return getSentimentOverall();

    }
}
