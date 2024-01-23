package ie.atu.sw;

// Imports
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.nio.file.Paths;
import java.io.IOException;
import java.io.FileWriter;


/**
 * The <b>Runner</b> class produces a menu for our Sentiment Analysis program, 
 * allowing the user to perform a variety of operations based on Scanner input.
 * Operations include displaying a menu and getting menu choice, file path 
 * specification for lexicons and tweets and executing of Sentiment analysis. 
 * 
 * @author Shane Walsh
 * @version 1.0, java SE 21
 * @since 1.8
 */

public class Runner {
	
	/**
	 * Main method to run Sentiment Analysis program, presenting a menu for user input.
	 * Handles menu choices for lexicon and tweet file paths, and executing sentiment calculation.
	 * uses showMenu(), getMenuChoice(), setLexicon(), setTweets(), progress(), executeSentiment()
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		
		// File Paths for Lexicon and tweets
		String lexiconFilePath = null;
		String tweetsFilePath = null;
		double outputSentiment = 0.0;
		
		// Menu set up
		Scanner sc = new Scanner(System.in);
		
		// Added this at the top just to tell the user their active directory
		System.out.println("\nWorking Directory = " + System.getProperty("user.dir"));
		
		// Placed program title here, outside showMenu method to avoid overcrowding console
		System.out.println("************************************************************");
		System.out.println("*     ATU - Dept. of Computer Science & Applied Physics    *");
		System.out.println("*                                                          *");
		System.out.println("*             Virtual Threaded Sentiment Analyser          *");
		System.out.println("*                                                          *");
		System.out.println("************************************************************");

		// Menu
		while (true) {
			showMenu();
			int choice = getMenuChoice(sc);
			
			switch(choice) {
				case 1: 
					// Specify Lexicon
					lexiconFilePath = setLexicon(sc);
					break;
					
				case 2: 
					// Specify Tweets file
					tweetsFilePath = setTweets(sc);
					break;
				
				case 3:
					// Execute, Analyse, Report				
					// Imitate progress of sentiment calculation
					progress();
					outputSentiment = executeSentiment(lexiconFilePath, tweetsFilePath);
					break;
					
				case 4: 
					//Allow to output result to file
					progress();
					outputFile(outputSentiment, lexiconFilePath, tweetsFilePath);
					break;
					
				case -1:
					// Exit Program
					System.out.println("******************************");
					System.out.println("Exiting...");
					System.exit(0);
				default:
					System.out.println("******************************");
					System.out.println("Invalid Option. Please enter a number from 1-3");
			}
		}
	}
	
	/**
	 * Execute Sentiment Analysis on the chosen tweet file using the chosen lexicon.
	 * Instantiates Lexicon object and TweetProcessor object. Calculates sentiment, displays result.
	 * 
	 * @param lexPath - The file path for the chosen lexicon
	 * @param tweetsPath - The file path for the tweets
	 */
	public static double executeSentiment(String lexPath, String tweetsPath) {
		try {
			// Create lexicon obj
			Lexicon lexicon = new Lexicon();
			lexicon.loadLex(lexPath);

			// create tweet processor obj
			TweetProcessor tweetProcessor = new TweetProcessor(lexicon);
			
			// Grab sentiment overall from tweetProcessor and print
			double overallSentiment = tweetProcessor.processTweets(Paths.get(tweetsPath));
			
			//Report whether sentiment is positive, negative or neutral
			reportSentiment(overallSentiment, lexPath, tweetsPath);
			
			return overallSentiment;
			
		} catch (IOException | InterruptedException | ExecutionException e) {
			System.out.println("Error: " + e.getMessage());
		}
		return 0;
	}
	
	/**
	 * Reports sentiment rating (positive, neutral, negative) 
	 * based on the overall sentiment score returned from tweetProcessor.
	 * 
	 * @param overallSentiment - The overall sentiment score
	 * @param lexPath - The lexicon filepath used
	 * @param tweetsPath - The tweets filepath used
	 */
	public static void reportSentiment(double overallSentiment, String lexPath, String TweetsPath) {
		System.out.println("****************************** \nReport: ");
		System.out.println("\nOverall Sentiment: " + overallSentiment);
		
		if(overallSentiment > 0.0) {
			System.out.println("******************************");
			System.out.println("\nOverall Sentiment is POSITIVE!! "
					+ "＼（＾○＾）人（＾○＾）／");
		}
		else if(overallSentiment < 0.0) {
			System.out.println("******************************");
			System.out.println("\nOverall Sentiment is NEGATIVE!! "
					+ "(ᴗ_ᴗ)");
		}
		else {
			System.out.println("******************************");
			System.out.println("\nOverall Sentiment is NEUTRAL!! "
					+ "(⚈_⚈)");
		}
		
		// Tells user Lexicon and Tweets File Used
		System.out.println("Lexicon Path used: " + lexPath + "\nTweets File Path Used: " + TweetsPath);
		System.out.println("******************************");
	}
	
	/**
	 * Outputs sentiment results to file using fileWriter
	 * 
	 * 
	 * @param overallSentiment - The overall sentiment score
	 * @param lexPath - The lexicon filepath used
	 * @param tweetsPath - The tweets filepath used
	 */
	
	public static void outputFile(double overallSentiment, String lexPath, String TweetsPath) {
		// Output the sentiment report to a txt, true parameter in FileWriter means it'll append instead when one exists already
		String outputFilePath = "SentimentResults.txt";
		try(FileWriter writer = new FileWriter(outputFilePath, true)) {
			writer.write("\nReport: " + overallSentiment);
			if(overallSentiment > 0.0) {
				writer.write(" = POSITIVE");
			}
			else if(overallSentiment < 0.0) {
				writer.write(" = NEGATIVE");
			}
			else {
				writer.write(" = NEUTRAL");
			}		
			
			writer.write("\nLexicon Path: " + lexPath + "\nTweets File Path: " + TweetsPath);
			writer.write("\n******************************");
			
			System.out.println("\n\nSentiment report outputted to file: " + outputFilePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Failed to write report to file");
			e.printStackTrace();
		}
	}
	
	/**
	 * Displays menu options for the Sentiment Analysis program.
	 */
	public static void showMenu() {
		System.out.println(ConsoleColour.WHITE);
		
		System.out.println("(1) Specify/Change Lexicon");
		System.out.println("(2) Specify/Change Tweets File");
		System.out.println("(3) Execute, Analyse and Report");
		System.out.println("(4) Output results");
		System.out.println("(-1) Quit");
		
		//Output a menu of options and solicit text from the user
		System.out.print(ConsoleColour.BLACK_BOLD_BRIGHT);
		System.out.print("Select Option [1-4]: ");
		System.out.println();
	}
	
	/**
	 * Gets user choice for menu operation
	 * 
	 * @param sc - Scanner object for user input
	 * @return - The user's menu option as String
	 */
	public static int getMenuChoice(Scanner sc) {
		int choice;
		while (true) {
			try {
				choice = Integer.parseInt(sc.nextLine());
				if (choice >= 1 && choice <= 4) {
					break;
				}
				else if (choice == -1) {
					System.out.println("Exiting...");
					System.exit(0);
				}
				else {
					System.out.println("******************************");
					System.out.println("Invalid option. Please enter a number from 1-4");
					System.out.println("Select Option [1-4]: ");
				}
			} catch (NumberFormatException e) {
				System.out.println("******************************");
				System.out.println("Invalid option. Please enter a number from 1-4");
				System.out.println("Select Option [1-4]: ");
			}
		}
		return choice;
	}
	
	/**
	 * Sets file path for lexicon based on user input, avoids hardcoding.
	 * 
	 * @param sc - Scanner object for user input
	 * @return - The file path for chosen lexicon
	 */
	public static String setLexicon(Scanner sc) {
		System.out.println("******************************");
		System.out.println("Enter lexicon file path: ");
		String filePath = sc.nextLine();
		System.out.println("\nLexicon in use pulled from: " + filePath);
		return filePath;
	}
	
	/**
	 * Sets the file path for the tweets file based on user input, avoids hardcoding.
	 * 
	 * @param sc - Scanner object for user input
	 * @return - The file path for chosen tweets file
	 */
	public static String setTweets(Scanner sc) {
		System.out.println("******************************");
		System.out.println("Enter tweets file path: ");
		String filePath = sc.nextLine();
		System.out.println("\nTweets File in use pulled from: " + filePath);
		return filePath;
	}
	
	/**
	 * Simulates progress of sentiment calculation by displaying a progress meter.
	 */
	public static void progress() {
		System.out.print(ConsoleColour.CYAN);	//Change the colour of the console text
		int size = 100;							//The size of the meter. 100 equates to 100%
		for (int i =0 ; i < size ; i++) {		//The loop equates to a sequence of processing steps
			printProgress(i + 1, size); 		//After each (some) steps, update the progress meter
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}					//Slows things down so the animation is visible 
		}	
	}
	
	/**
	 * prints progress meter
	 * 
	 * @param index
	 * @param total
	 */
	public static void printProgress(int index, int total) {
		if (index > total) return;	//Out of range
        int size = 50; 				//Must be less than console width
	    char done = '█';			//Change to whatever you like.
	    char todo = '░';			//Change to whatever you like.
	    
	    //Compute basic metrics for the meter
        int complete = (100 * index) / total;
        int completeLen = size * complete / 100;
        
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < size; i++) {
        	sb.append((i < completeLen) ? done : todo);
        }
        
        System.out.print("\r" + sb + "] " + complete + "%");
        
        //Once the meter reaches its max, move to a new line.
        if (done == total) System.out.println("\n");
    }
}
