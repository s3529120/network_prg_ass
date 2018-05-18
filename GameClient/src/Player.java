import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class Player {
	private PrintWriter out; 
	private BufferedReader in;
	private Socket socket;
		
	/*Player constructor
	 * @param clientSocket SOcket connection to server
	 */
	public Player(Socket clientSocket) {
		//Set values and generate reader and writer
		try {
			socket=clientSocket;
			out = new PrintWriter(clientSocket.getOutputStream(), true); 
			in = multiclient.generateReader(clientSocket.getInputStream());
		} catch (IOException e) {
			System.err.println("Failed to generate reader/writer for client socket.");
		}
	}
	
	/* Read line from server
	 * @return String message from server
	 */
	public String readLine() throws IOException {
		String inputLine; 
		
		//Check if value returned
		if((inputLine = in.readLine()) != null) 
		{
			return inputLine;
		}else {
			return "";
		}
	}
	
	/* Write line to server
	 * @param message String to send to server
	 */
	public void writeLine(String message) {
		//Send message to server
		out.println(message);
	}
	
	public void closeConnection() {
		//Close readers and writer
		try {
			out.close(); 
			in.close();
			
			//Close socket
			socket.close(); 
		}
		
		//Catch exceptions from already closed objects
		catch (IOException e) {}
	}
}
