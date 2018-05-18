import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class multiclient {
	//Constant values
	public final static String SERVER_ADDRESS= new String ("m1-c17n1.csit.rmit.edu.au");
	public final static int SERVER_PORT= 19120;

	public static void main(String[] args) {
		//First game run
				playGame();
				
				//Check if user wants to play again
				try {

					BufferedReader in = generateReader(System.in);
					
					String userInput="";
					System.out.println("Would you like to play again:"
							+ " \"p\" or exit \"x\"");
					while((userInput = in.readLine()) != null) 
					{
						//Exit
						if(userInput.compareToIgnoreCase("x")==0) {
							break;
						}
						//Play again
						else if(userInput.compareToIgnoreCase("p")==0) {
							playGame();
						}
						//Invalid entry
						else {
							System.out.println("Please enter to play again:"
									+ " \"p\" or exit \"x\"");
						}
					}
				} catch (IOException e) {
					System.err.println("Failed to start reader");
					System.exit(1);
				}
				
				//Goodbye message
				System.out.println("Thanks for playing\nExiting");
	}
	
	//Play game
	private static void playGame() {
		String userInput="";
		int guessLen,correct,incorrect,turns;
		turns = guessLen = correct = incorrect = 0;
		String[] response;
		
		//Initialize player and connections
		Player me = null;
		BufferedReader stdIn =null;
		try {
			me = new Player(new Socket(SERVER_ADDRESS, SERVER_PORT));
			stdIn = generateReader(System.in);
		} catch (UnknownHostException e1) {
			System.err.println("Failed to initialize, exiting");
			System.exit(1);
		} catch (IOException e1) {
			System.err.println("Failed to initialize, exiting");
			System.exit(1);
		}

		//Starting messages
		try {
			//Connected
			System.out.println(me.readLine());
			
			//Game started
			System.out.println(me.readLine());
			
			//Get player name
			userInput=me.readLine();
			
			//Get player position
			userInput=me.readLine();
			
			//Check for first position
			if(userInput.compareTo("1")==0) {
				requestGuessLength(stdIn,me);
			}
			else{
				System.out.println("Waiting for guess length");
			}
			
			//Get length of guess string
			guessLen=Integer.parseInt(me.readLine());

		}catch(IOException e) {
			System.err.println("IOException");
		}
		
		
		
		
        //Take and process guesses from user
		System.out.println("Please enter your guess (String of numbers "+guessLen+" long)");
		try {
			
			//Read in lines from console
			while ((userInput = stdIn.readLine()) != null) 
			{
				System.out.println("user input was: "+userInput);
				//Check if guess is correct length
				if(userInput.length()!=guessLen) {
					System.out.println("Your guess must be "+guessLen+" long)");	
					continue;
				}
				//Print guess to server
				me.writeLine(userInput);
				
				//Read back correct and incorrect values
				try {
					response=me.readLine().split("-",-1);
					correct = Integer.parseInt(response[0]);
					incorrect = Integer.parseInt(response[1]);
				} catch (NumberFormatException e) {
					System.err.println("Invalid response from server");
				} catch (IOException e) {
					System.err.println("Failed to get message from server");
				}
				
				//Increment turn counter
				turns++;
				
				//Check if guess was correct and inform user
				if(correct!=guessLen) {
					System.out.println("Your guess was "+correct+" correct "+incorrect+" incorrect");
				}else {
					System.out.println("Your guess was  correct!, you win!");
					break;
				}
				
				//Check if max turns exceeded
				if(turns==10) {
					System.out.println("You have reached your maximum turns");
					break;
				}else {
					System.out.println("Please enter your guess (String of numbers "+guessLen+" long)");
				}
			}
			
				//Finals messages
				//Waiting to finish
				System.out.println(me.readLine());
				
				//Winner
				System.out.println(me.readLine());
		} catch (IOException e) {
			System.err.println("Failed to read input");
		}
		

		
		//CLose streams and sockets
		me.closeConnection();
	}
	
	/*Generates a buffered reader object for givven input stream
	 * @param is input stream to generate reader for
	 * @return buffered reader for provided input stream
	 */
	public static BufferedReader generateReader(InputStream is) throws IOException{
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		return in;
		
	}
	
	/*First player only function requests length of guess string from user
	 *  and returns validated answer to server
	 *  @param stdIn BufferedReader connected to console
	 *  @param me Player first player determining length
	 */
	public static void requestGuessLength(BufferedReader stdIn, Player me) {
		String userInput;
		int guessLen;
		
		//Request desired guess string length
		System.out.println("Please enter the length of desired guess string");
		try {
		while ((userInput = stdIn.readLine()) != null) 
		{
			try {
				//Get input from user
				guessLen=Integer.parseInt(userInput);
				}
			
				//Check if number
				catch(NumberFormatException e){
					System.out.println("Invalid input enter a number between 3-8");
					continue;
				}
			
				//Check if in correct range
				if(guessLen<3 || guessLen>8) {
					System.out.println("Invalid input enter a number between 3-8");
					continue;
				}
				
				//Send requested length to server
				me.writeLine(userInput);
				break;
			}
		}catch(IOException e) {
			System.err.println("Failed to read from console");
		}
		
	}

}
