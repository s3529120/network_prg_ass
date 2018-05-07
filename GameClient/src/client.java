import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class client {
	public final static String SERVER_ADDRESS= new String ("m1-c17n1.csit.rmit.edu.au");
	public final static int SERVER_PORT= 19120;

	public static void main(String[] args) {
		String userInput;
		int guessLen,correct,turns=0;
		
		//Setup message echo socket writer
		Player me;
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
		
		System.out.println("Please enter the length of desired guess string");
		while ((userInput = stdIn.readLine()) != null) 
		{
			try {
				guessLen=Integer.parseInt(userInput);
			}catch(NumberFormatException e){
				System.out.println("Invalid input enter a number between 3-8");
				continue;
			}
			
			if(guessLen<3 || guessLen>8) {
				System.out.println("Invalid input enter a number between 3-8");
				continue;
			}
			me.writeLine(userInput);
		}
		
		
		
        //Take in user messages and recieve echos until 'x' is written and echoed
		System.out.println("Please enter your guess (String of numbers "+guessLen+" long)");
		while ((userInput = stdIn.readLine()) != null) 
		{
			if(userInput.length()!=guessLen) {
				System.out.println("Your guess must be "+guessLen+" long)");	
				continue;
			}
			//Print guess to server
			me.writeLine(userInput);
			
			//Read response message and echo unless 'x' then quit
			correct = Integer.parseInt(me.readLine());
			turns++;
			if(correct!=guessLen) {
				System.out.println("Your guess was "+correct+"/"+guessLen+" correct");
			}else {
				System.out.println("Your guess was  correct!, you win!");
				break;
			}
			if(turns==10) {
				System.out.println("You failed to guess in time :(");
				break;
			}else {
				System.out.println("Please enter your guess (String of numbers "+guessLen+" long)");
			}
		}
		

		
		//CLose streams and sockets
		me.closeConnection();
		stdIn.close();
	
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
