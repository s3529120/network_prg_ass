import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class client {
	//Constant values
	public final static String SERVER_ADDRESS= new String ("m1-c17n1.csit.rmit.edu.au");
	public final static int SERVER_PORT= 19120;

	public static void main(String[] args) {
		String userInput;
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
		
		//Request desired guess string length
		System.out.println("Please enter the length of desired guess string");
		try {
		while ((userInput = stdIn.readLine()) != null) 
		{
			try {
				guessLen=Integer.parseInt(userInput);
				}
			
				//Check if number
				catch(NumberFormatException e){
					System.out.println("Invalid input enter a number between 3-8");
					continue;
				}
			
				//Chexk if in correct range
				if(guessLen<3 || guessLen>8) {
					System.out.println("Invalid input enter a number between 3-8");
					continue;
				}
				
				//Send requested length to server
				me.writeLine(userInput);
			}
		}catch(IOException e) {
			System.err.println("Failed to read from console");
		}
		
		
		
        //Take and process guesses from user
		System.out.println("Please enter your guess (String of numbers "+guessLen+" long)");
		try {
			
			//Read in lines from console
			while ((userInput = stdIn.readLine()) != null) 
			{
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
				
				//CHeck if guess was correct and inform user
				if(correct!=guessLen) {
					System.out.println("Your guess was "+correct+"/"+guessLen+" correct");
				}else {
					System.out.println("Your guess was  correct!, you win!");
					break;
				}
				
				//Check if max turns exceeded
				if(turns==10) {
					System.out.println("You failed to guess in time :(");
					break;
				}else {
					System.out.println("Please enter your guess (String of numbers "+guessLen+" long)");
				}
			}
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

}
