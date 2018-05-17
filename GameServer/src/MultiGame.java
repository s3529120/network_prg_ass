import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class MultiGame extends Thread{

	public final static int NUM_GUESSES = 10;
	public final static int SLEEP_TIME = 250;

	private String guessNumber;
	private ThreadPlayer[] players;
	private Logger log;
	private String winner;
	private int winningScore;
	private boolean done;
	
	public MultiGame(ThreadPlayer[] players,Logger log) {
		done=false;
		this.players= players;
		this.log = log;
		
	}
	
	public void run() {
		log.logGame("Multiplayer game started");
		
		//Waiting for guess length to be selected
		synchronized(this) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		
		//Waiting for all players to be done guessing
		synchronized(this) {
			while(!done) {
				try {
					Thread.sleep(SLEEP_TIME);
				} catch (InterruptedException e) {
				}
				for(ThreadPlayer play : players) {
					done=done&&play.getDone();
				}
			}
		}
		
		synchronized(this) {
			for(ThreadPlayer play : players) {
				if(play.getTopScore()>winningScore) {
					winner=play.getAddress();
					winningScore=play.getTopScore();
				}
			}
			notify();
			log.logGame(winner+" won the game with a score of: "+winningScore);
		}
		end();
	}
		
	
	/*Determine number of correct guess and incorrect places
	 * 
	 */
	public String processGuess(String guess) {
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
		for(ThreadPlayer play : players) {
			play.closeConnection();
		}
	}
	
	public void setGuessNumber(String num) {
		this.guessNumber=num;
	}
	public String getGuessNumber() {
		return guessNumber;
	}
	
	public Logger getLog() {
		return log;
	}
	public String getWinner() {
		return winner;
	}
	
	public int getWinningScore() {
		return winningScore;
	}

	public boolean getDone() {
		return done;
	}
	
	
	
}
