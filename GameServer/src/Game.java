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
		boolean win=false;
		String guess="";
		int correct;
		for(int i=0;i<NUM_GUESSES;i++) {
			try {
				guess=play.readLine();
			} catch (IOException e) {

				System.err.println("IO exception encountered." );
			}
			
			correct=processGuess(guess);
			
			play.writeLine(String.valueOf(correct));
			if(correct==guessNumber.length()) {
				win=true;
				break;
			}
		}
	}
	
	private int processGuess(String guess) {
		int correct=0;
		for(int i=0;i<guessNumber.length();i++) {
			if(guessNumber.charAt(i)==guess.charAt(i)) {
				correct++;
			}
		}
		return correct;
	}
	
	public void end() {
		play.closeConnection();
	}
	
	
}
