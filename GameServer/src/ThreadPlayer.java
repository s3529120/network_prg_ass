import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ThreadPlayer extends Thread{
	//Timeout duration
	public static final int TIMEOUT = 20000;
	
	private String playerName;
	private MultiGame game;
	private PrintWriter out; 
	private BufferedReader in;
	private Socket socket;
	private boolean first,done,lock;
	private int topScore;
		
	public ThreadPlayer(Socket clientSocket) throws IOException {
		//Generate readers and writer and set variables
		try {
			topScore=0;
			first=done=false;
			socket=clientSocket;
			socket.setSoTimeout(TIMEOUT);
			out = new PrintWriter(clientSocket.getOutputStream(), true); 
			in = generateReader(clientSocket);
		}
		//Throw exception in failure encountered
		catch (IOException e) {
			throw e;
		}
	}

	//Game mutator
	public void setGame(MultiGame game) {
		this.game=game;
	}

	//Wait until signaled by game
	public void customLock(){
		lock=false;
		while(!lock){
			try{
				Thread.sleep(MultiGame.SLEEP_TIME);
			}catch(Exception e){
				lock=true;
			}
		}
	}

	//Unlock thread allowing to proceed
	public void unlock(){
		lock=true;
	}
	
	//Start thread to play game
	public void run() {	
		
		/*Check if first player to see if guess length
		 * should be requested
		 */
		if(first) {
			try {
				//Ask player to set name
				requestPlayerName();
				
				//Get guess string length from player
				getGuessLength();
			}
			//If failed remove player and set default length
			catch (Exception e) {
				closeConnection();
				game.removePlayer(this);
				game.setDefaultLength();
				game.notify();
			}
		}
		//If not first player
		else {
			try {
				//Get player to set name
				requestPlayerName();
			}
			//Remove player if failed
			catch (Exception e) {
				closeConnection();
				game.removePlayer(this);
			}
			
			//Wait for guess length to be set
			customLock();
		}
		
		//Reset value of done
		done=false;
	
		//Start guessing sequence
		try {
			playGame();
		}
		//Remove player if exits or connection lost
		catch (Exception e) {
			closeConnection();
			game.removePlayer(this);
		}
		
		//Set done so server know if finished with this step
		done=true;
		
		//Wait for all players to finish guessing
		customLock();
	}
	
	
	//Guessing section of game
	public void playGame() throws Exception {
		String guess="";
		String correct;
		String[] response;
		boolean maxTurnsReached=false;
		int turns =0;
	
	
		//Accept series of guesses from player 
		while(!maxTurnsReached) {
			
			//Increment turn counter
			turns++;
			
			//Read guess from player
			guess=readLine();	
			
			//Log guess
			game.getLog().logCommunication(this, guess);
			
							
			//Find correct and incorrect positions
			correct=game.processGuess(guess);

			//Extract elements
			response=correct.split("-",-1);

			//Log correctness of guess	
			game.getLog().logGame(this, "Guess made: "+guess +
					response[0]+" correct"+response[1]+" incorrect");
				
			//Update best score by player
			updateScore(Integer.parseInt(response[0]));
							
			//Send response to client
			writeLine(correct);
							
			//Check for correct guess
			if(Integer.parseInt(response[0])==game.getGuessNumber().length()||turns==MultiGame.NUM_GUESSES) {
				break;
			}
		}
		
		//Log player score
		game.getLog().logGame(this, "scored: "+topScore);
		
		//Wait for game to finish message
		writeLine("Waiting for game to finish");
		
	}
	
	
	/* Get desired guess string length from player 1
	 * throw exception on error or exit so game knows to
	 * remove and set default value
	 */
	private void getGuessLength() throws  Exception {
		
		//Synchronize portion on managing game
		synchronized(game){
			int length=0;
			
			//Read desired guess
			length=Integer.parseInt(readLine());
			
			//Log requested guess length
			game.getLog().logCommunication(this, "Resquested length of "+length);
		
			//Set guess length for game to generate string
			game.setGuessLen(length);
			
			//Notify game that it can proceed
			game.notify();
		}
		
	}
	
	/*Ask player to enter their name throws exception 
	 * on error or quit message
	 */
	public void requestPlayerName() throws Exception {
		//Request name
		writeLine("Please enter your name");
		playerName = readLine();
	}

	/*Generates a buffered reader object for givven input stream
	 * @param is input stream to generate reader for
	 * @return buffered reader for provided input stream
	 */
	private static BufferedReader generateReader(Socket soc) throws IOException{
		BufferedReader in = new BufferedReader(new InputStreamReader( soc.getInputStream()));
		return in;
		
	}
	
	/*Read line from player throws exception on exit or error
	 * so that game can remove them
	 * @return String message received
	 */
	public String readLine() throws Exception {
		String inputLine;
		
		try {
			
			//Read line from player
			if((inputLine = in.readLine()) != null) 
			{
				//Check for exit message
				if(inputLine.compareToIgnoreCase("x")==0) {
					throw new QuitException();
				}
				return inputLine;
			}else {
				return "";
			}
		}
		
		//Check for errors or quit instruction
		catch(QuitException e) {
			game.getLog().logCommunication(this, "player quit");
			throw e;
		}catch(SocketTimeoutException e) {
			game.getLog().logCommunication(this, "Connection timed out");
			throw e;
		}catch(IOException e) {
			game.getLog().logCommunication(this, "Read failed");
			throw e;
		}catch(Exception e) {
			game.getLog().logCommunication(this, "Unexpected error occurred");
			throw e;
		}
	}
	
	//Write message to player
	public void writeLine(String message) {
		out.println(message);
	}
	
	//Close objects
	public void closeConnection() {
		//Close readers and writer
		try {
			out.close(); 
			in.close();
			
			//Close socket
			socket.close(); 
		}
		//Catch already closed
		catch (IOException e) {;
		}
	}
	
	//Remote address accessor
	public String getAddress() {
		return socket.getRemoteSocketAddress().toString();
	}
	
	//Update to score if higher
	private void updateScore(int score) {
		if(score>topScore) {
			topScore=score;
		}
	}
	
	//done accessor
	public boolean getDone() {
		return done;
	}

	//topScore accessor
	public int getTopScore() {
		return topScore;
	}

	//first accessor (is first player?)
	public boolean getFirst(){
		return first;
	}
	
	//playerName accessor
	public String getPlayerName() {
		return playerName;
	}
	
	//Set to first player
	public void setFirst() {
		first=true;
	}
}
