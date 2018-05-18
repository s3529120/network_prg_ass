import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class Game {
	//Number of allowed guesses
	private final static int NUM_GUESSES = 10;

	private String guessNumber;
	private Player play;
	private Logger log;
	
	/* Constructor holds player and log also
	 * requests and generates guess string length
	 */
	public Game(Player player,Logger log) {
		int length=0;
		List<Integer> array = new LinkedList<>();
		play= player;
		this.log = log;
		
		//CHeck if player missing
		if(play==null){
			System.err.println("failed");
		}

		//Initialize list of numbers to make up guess string
		for (int i = 0; i <= 9; i++) {
		    array.add(i);
		}
		
		try {
			//Recieve requested length
			length=Integer.parseInt(play.readLine());
			log.logCommunication(play, "Resquested length of "+length);
		} 
		//Check for format exception
		catch (NumberFormatException e) {
			System.err.println("Number format exception encountered.");
		}
		//Check for bad connection
		catch (IOException e) {
			System.err.println("IO exception encountered." );
		}
		
		//Compose guess number
		guessNumber="";
		for(int i=0;i<length;i++) {
			Collections.shuffle(array);
			guessNumber+=((LinkedList<Integer>) array).pollFirst();
		}
		
	}
	
	//Play game
	public void play() {
		String guess="";
		String correct;
		String[] response;
		
		//Log game start
		log.logGame(play, "Single player game started");
		
		//Accept series of guesses from player 
		for(int i=0;i<NUM_GUESSES;i++) {
			try {
				//Get guess
				guess=play.readLine();
				
				//Log guess
				log.logCommunication(play, guess);
			}
			//Check for connection issues
			catch (IOException e) {

				System.err.println("IO exception encountered." );
			}
			
			//Find correct and incorrect positions
			correct=processGuess(guess);

			//Extract elements
			response=correct.split("-",-1);
			
			//Log correctness of guess
			log.logGame(play, "Guess made: "+guess +
					response[0]+" correct, "+response[1]+" incorrect");
			
			//Send response to client
			play.writeLine(correct);
			
			//Check for correct guess
			if(Integer.parseInt(response[0])==guessNumber.length()) {
				//Log that game was won
				log.logGame(play, "won the game");
				
				//Terminate game
				return;
			}
		}
		
		//Log no winner found
		log.logGame(play, "No winner for game");
	}
	
	/*Determine number of correct guess and incorrect places
	 * @param guess String guess by client
	 */
	private String processGuess(String guess) {
		int correct, incorrect;
		incorrect = correct=0;
		
		//Iterate through guess number positions
		for(int i=0;i<guessNumber.length();i++) {
			
			//Increment correct if found
			if(guessNumber.charAt(i)==guess.charAt(i)) {
				correct++;
			}
			//Iterate through each guess position for incorrects
			for(int j=0;j<guessNumber.length();j++) {
				//If same position not relevant to incorrects
				if(i==j) {
					continue;
				}
				//Check for incorrects
				if(guessNumber.charAt(j)==guess.charAt(i)) {
					incorrect++;
				}
			}
		}
		//Return string
		return correct+"-"+incorrect;
	}
	
	//Close player connection
	public void end() {
		play.closeConnection();
	}
	
	
}
