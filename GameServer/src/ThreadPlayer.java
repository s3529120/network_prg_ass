import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ThreadPlayer extends Thread{
		private MultiGame game;
		private PrintWriter out; 
		private BufferedReader in;
		private Socket socket;
		private boolean first,done;
		private int topScore;
		
	public ThreadPlayer(Socket clientSocket) {
		//Generate readers and writer
		try {
			topScore=0;
			first=done=false;
			socket=clientSocket;
			out = new PrintWriter(clientSocket.getOutputStream(), true); 
			in = generateReader(clientSocket);
		} catch (IOException e) {
			System.err.println("Failed to generate reader/writer for client socket.");
		}
	}

	public ThreadPlayer(Socket clientSocket,boolean fir) {
		this(clientSocket);
		first=true;
	}
	
	public void setGame(MultiGame game) {
		this.game=game;
	}
	
	public void run() {
		
		if(!first) {
			synchronized(game) {
				writeLine("Waiting for player one to select length");
				try {
					wait();
				} catch (InterruptedException e) {
					
				}
				writeLine(Integer.toString(game.getGuessNumber().length()));
			}
		}else {
			synchronized(game) {
				getGuessLength();
				notify();
			}
		}
		
		synchronized(game) {
			playGame();
			done=true;
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
				
		try {
			awaitResults();
		} catch (InterruptedException e) {
		}
	}
	
	public void playGame() {
		String guess="";
		String correct;
		String[] response;
		
		//Accept series of guesses from player 
		for(int i=0;i<MultiGame.NUM_GUESSES;i++) {
			try {
				guess=readLine();
								
				game.getLog().logCommunication(this, guess);
			} catch (IOException e) {

				System.err.println("IO exception encountered." );
			}
							
			//Find correct and incorrect positions
			correct=game.processGuess(guess);

			//Extract elements
			response=correct.split("-",-1);
							
			game.getLog().logGame(this, "Guess made: "+guess +
					response[0]+" correct"+response[1]+" incorrect");
							
			updateScore(Integer.parseInt(response[0]));
							
			//Send response to client
			writeLine(correct);
							
			//Check for correct guess
			if(Integer.parseInt(response[0])==game.getGuessNumber().length()) {
				break;
			}
		}
		game.getLog().logGame(this, "scored: "+topScore);
		
		closeConnection();
	}
	
	
	private void getGuessLength() {
		List<Integer> array = new LinkedList<>();
		int length=0;
		for (int i = 1; i <= 6; i++) {
		    array.add(i);
		}
		
		try {
			length=Integer.parseInt(readLine());
			game.getLog().logCommunication(this, "Resquested length of "+length);
		} catch (NumberFormatException e) {
			System.err.println("Number format exception encountered.");
		} catch (IOException e) {
			System.err.println("IO exception encountered." );
		}
		
		String guessNumber="";
		for(int i=0;i<length;i++) {
			Collections.shuffle(array);
			guessNumber+=((LinkedList<Integer>) array).pollFirst();
		}
		game.setGuessNumber(guessNumber);
		
	}

	/*Generates a buffered reader object for givven input stream
	 * @param is input stream to generate reader for
	 * @return buffered reader for provided input stream
	 */
	private static BufferedReader generateReader(Socket soc) throws IOException{
		BufferedReader in = new BufferedReader(new InputStreamReader( soc.getInputStream()));
		return in;
		
	}
	
	public String readLine() throws IOException {
		String inputLine; 
		if((inputLine = in.readLine()) != null) 
		{
			return inputLine;
		}else {
			return "";
		}
	}
	
	public void writeLine(String message) {
		//Echo message to client
		out.println(message);
	}
	
	public void closeConnection() {
		//Close readers and writer
		try {
			out.close(); 
			in.close();
			
				
			//Close socket
			socket.close(); 
		}catch (IOException e) {;
		}
	}
	
	public String getAddress() {
		return socket.getRemoteSocketAddress().toString();
	}
	
	private void updateScore(int score) {
		if(score>topScore) {
			topScore=score;
		}
	}
	
	private void awaitResults() throws InterruptedException{
		synchronized(game) {
			writeLine("Waiting for game to finish");
			wait();
		}
		writeLine(game.getWinner()+"is the winner with a score of: "+game.getWinningScore());
	}

	public boolean getDone() {
		return done;
	}

	public int getTopScore() {
		return topScore;
	}
}
