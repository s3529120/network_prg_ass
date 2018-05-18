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
		
		for(int i=0;i<players.length;i++) {
			players[i].setGame(this);
			players[i].writeLine("Game starting!");
			if(players[i].getFirst()==true){
				players[i].writeLine("1");
			}else{
				players[i].writeLine("0");

			}
		}
		
	}
	
	public void run() {
		log.logGame("Multiplayer game started");
		
		
		//Start player threads
		for(ThreadPlayer play : players) {
			System.out.println(play.getFirst());
			play.start();
		}
		
		//Waiting for guess length to be selected
		synchronized(this) {
			try {
				wait();
				unlockThreads(true);
			} catch (InterruptedException e) {
			}
		}
		
		//Waiting for all players to be done guessing
			while(!done) {
				System.out.println("server waiting");
				try {
					Thread.sleep(SLEEP_TIME);
				} catch (InterruptedException e) {
				}
				for(int i=0;i<players.length;i++) {
					System.out.println("donecheck");
					if(i!=0){
						done=done&&players[i].getDone();
					}else{
						done=players[i].getDone();
					}
				}
			}
			for(ThreadPlayer play : players) {
				if(play.getTopScore()>winningScore) {
					winner=play.getAddress();
					winningScore=play.getTopScore();
				}
			}
			informWinner();

			unlockThreads(false);
			log.logGame(winner+" won the game with a score of: "+winningScore);
		
		end();
	}

	public void informWinner(){
		for(ThreadPlayer play : players){
			if(winner == play.getAddress()){
				play.writeLine("You are the winner with a score of "+winningScore);
			}else{
				play.writeLine(winner+" the winner with a score of "+winningScore);

			}
		}
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

	public void informGuessLength(){
		//Inform client of string length
		for(ThreadPlayer p : players){
		
			p.writeLine(Integer.toString(guessNumber.length()));
		
		}	

	}

	public void unlockThreads(boolean skipFirst){
		for(ThreadPlayer play : players){
			if(!skipFirst || !play.getFirst()){
				play.unlock();
			}
		}
			
	}
	
	
	
}
