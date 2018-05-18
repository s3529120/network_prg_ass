import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class MultiGame extends Thread{
	//Constants
	public final static int NUM_GUESSES = 10;
	public final static int SLEEP_TIME = 250;
	public final static int DEFAULT_LENGTH = 6;

	//Class variables
	private String guessNumber;
	private ThreadPlayer[] players;
	private Logger log;
	private String winner;
	private int winningScore,guessLen;
	private boolean done;
	
	/* Constructor takes in vital information and informs
	 * players whether or not they are the first player
	 */
	public MultiGame(ThreadPlayer[] players,Logger log) {
		done=false;
		this.players= players;
		this.log = log;
		
		
		//Iterate through players
		for(int i=0;i<players.length;i++) {
			//Set game object to this
			players[i].setGame(this);
			
			//Send game start message
			players[i].writeLine("Game starting!");
			
			/*Send 1 to first player 0 to rest, lets player
			 * know if they need to set guess length
			 */
			if(players[i].getFirst()==true){
				players[i].writeLine("1");
			}else{
				players[i].writeLine("0");

			}
		}
	}
	
	public void run() {
		//Log game start
		log.logGame("Multiplayer game started");
		
		
		//Start player threads
		for(ThreadPlayer play : players) {
			play.start();
		}
		
		//Get player names
		//Iterate through waiting until names are selected
		while(!done) {
			//Wait
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e) {
			}
			
			//CHeck if players names set
			for(int i=0;i<players.length;i++) {
				if(i!=0){
					done=done&&players[i].getDone();
				}else{
					done=players[i].getDone();
				}
			}
		}

		//Let threads know they can proceed with game
		unlockThreads(false);
		
		//Waiting for guess length to be selected by player 1
		synchronized(this) {
			try {
				wait();
				
				//Generate guess number string
				setGuessNumber();
				
				//Inform players of length
				informGuessLength();
				
				//Allow thread to proceed
				unlockThreads(true);
			} catch (InterruptedException e) {
			}
		}
		
		//Waiting for all players to be done guessing
			while(!done) {
				try {
					//Wait
					Thread.sleep(SLEEP_TIME);
				} catch (InterruptedException e) {
				}
				
				//CHeck if players done guessing
				for(int i=0;i<players.length;i++) {
					if(i!=0){
						done=done&&players[i].getDone();
					}else{
						done=players[i].getDone();
					}
				}
			}
			
			//Calculates top player
			for(ThreadPlayer play : players) {
				if(play.getTopScore()>winningScore) {
					winner=play.getPlayerName();
					winningScore=play.getTopScore();
				}
			}
			
			//Lets players know winner was found
			informWinner();

			//Allow threads to proceed
			unlockThreads(false);
			
			//Log winner
			log.logGame(winner+" won the game with a score of: "+winningScore);
		
		end();
	}

	//Informs players of games winner
	public void informWinner(){
		for(ThreadPlayer play : players){
			if(winner.compareToIgnoreCase(play.getName())==0){
				play.writeLine("You are the winner with a score of "+winningScore);
			}else{
				play.writeLine(winner+" the winner with a score of "+winningScore);
			}
		}
	}
		
	
	/*Determine number of correct guess and incorrect places
	 * @param guess String Guess by player
	 * @return String indicating correct and incorrect
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
		//Return string format is (correct-incorrect)
		return correct+"-"+incorrect;
	}
	
	//Close player connections
	public void end() {
		for(ThreadPlayer play : players) {
			play.closeConnection();
		}
	}
	
	/*Generate guess string from length
	 */
	public void setGuessNumber() {
		List<Integer> array = new LinkedList<>();
		for (int i = 0; i <= 9; i++) {
		    array.add(i);
		}
		
		/* Randomize order of array then get first values
		 * until guess string equals desired length
		 */
		String guessNumber="";
		for(int i=0;i<guessLen;i++) {
			Collections.shuffle(array);
			guessNumber+=((LinkedList<Integer>) array).pollFirst();
		}
		
		//Set guess number
		this.guessNumber=guessNumber;
	}

	//Inform players for guess length
	public void informGuessLength(){
		//Inform each client of string length
		for(ThreadPlayer p : players){
			p.writeLine(Integer.toString(guessNumber.length()));
		}	

	}

	/*Unlock each thread allowing them to proceed
	 * @param skipFirst boolean whether to skip unlocking first player
	 */
	public void unlockThreads(boolean skipFirst){
		for(ThreadPlayer play : players){
			if(!skipFirst || !play.getFirst()){
				play.unlock();
			}
		}
	}
	
	/*Removed given player from game
	 * @param p ThreadPlayer player to remove
	 */
	public void removePlayer(ThreadPlayer p) {
		ThreadPlayer[] newPlayers = new ThreadPlayer[players.length-1];
		int j=0;
		
		//Log removal
		log.logGame(p, "was removed from game");
		
		//Copy no removed players to new array
		for(int i=0;i<players.length;i++) {
			if(!players[i].equals(p)) {
				newPlayers[j]=players[i];
				j++;
			}
		}
		
		//Replace old array with new
		players=newPlayers;
	}
	
	//Set length of guess string to default
	public void setDefaultLength() {
		guessLen=DEFAULT_LENGTH;
	}
	
	//guessLen mutator
	public void setGuessLen(int len) {
		guessLen=len;
	}
	
	//Guess number accessor
	public String getGuessNumber() {
		return guessNumber;
	}
	
	//Logger accessor
	public Logger getLog() {
		return log;
	}
	
	//Winner accessor
	public String getWinner() {
		return winner;
	}
	
	//Winning score accessor
	public int getWinningScore() {
		return winningScore;
	}

	//Done value accessor
	public boolean getDone() {
		return done;
	}
	
	
	
}
