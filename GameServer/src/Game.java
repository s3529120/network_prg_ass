import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class Game {

	private final static int NUM_GUESSES = 10;

	String guessNumber;
	Player play;
	
	public Game(Player player) {
		int length=0;
		List<Integer> array = new LinkedList<>();
		play= player;
		
		for (int i = 1; i <= 6; i++) {
		    array.add(i);
		}
		
		try {
			length=Integer.parseInt(play.readLine());
		} catch (NumberFormatException e) {
			System.err.println("Number format exception encountered.");
		} catch (IOException e) {
			System.err.println("IO exception encountered." );
		}
		
		guessNumber="";
		for(int i=0;i<length;i++) {
			Collections.shuffle(array);
			guessNumber+=((LinkedList<Integer>) array).pollFirst();
		}
		
	}
	
	public void play() {
		String guess="";
		String correct;
		String[] response;
		
		//Accept series of guesses from player 
		for(int i=0;i<NUM_GUESSES;i++) {
			try {
				guess=play.readLine();
			} catch (IOException e) {

				System.err.println("IO exception encountered." );
			}
			
			//Find correct and incorrect positions
			correct=processGuess(guess);

			//Extract elements
			response=correct.split("-",-1);
			
			//Send response to client
			play.writeLine(correct);
			
			//Check for correct guess
			if(Integer.parseInt(response[0])==guessNumber.length()) {
				break;
			}
		}
	}
	
	/*Determine number of correct guess and incorrect places
	 * 
	 */
	private String processGuess(String guess) {
		int correct, incorrect;
		incorrect = correct=0;
		
		//Iterate through guess number positions
		for(int i=0;i<guessNumber.length();i++) {
			if(guessNumber.charAt(i)==guess.charAt(i)) {
				correct++;
			}
			//Iterate through each guess position for incorrects
			for(int j=0;j<guessNumber.length();j++) {
				//If same position not relevant to incorrects
				if(i==j) {
					continue;
				}
				//CHeck for incorrects
				if(guessNumber.charAt(j)==guess.charAt(i)) {
					incorrect++;
				}
			}
		}
		//Return string
		return correct+"-"+incorrect;
	}
	
	public void end() {
		play.closeConnection();
	}
	
	
}
