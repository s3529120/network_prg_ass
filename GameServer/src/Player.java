import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Player {
	private PrintWriter out; 
	private BufferedReader in;
	private Socket socket;
	private String address="";
		
	//Constructor initializes reader and writer
	public Player(Socket clientSocket) {
		//Generate reader and writer
		try {
			socket=clientSocket;
			out = new PrintWriter(clientSocket.getOutputStream(), true); 
			in = generateReader(clientSocket);
			address=socket.getRemoteSocketAddress().toString();
		} catch (IOException e) {
			System.err.println("Failed to generate reader/writer for client socket.");
		}
	}
	
	/*Generates a buffered reader object for givven input stream
	 * @param is input stream to generate reader for
	 * @return buffered reader for provided input stream
	 */
	private static BufferedReader generateReader(Socket soc) throws IOException{
		BufferedReader in = new BufferedReader(new InputStreamReader( soc.getInputStream()));
		return in;
		
	}
	
	//Read line from client
	public String readLine() throws IOException {
		String inputLine; 
		if((inputLine = in.readLine()) != null) 
		{
			return inputLine;
		}else {
			return "";
		}
	}
	
	//Write message to client
	public void writeLine(String message) {
		out.println(message);
	}
	
	//Close connections and objects
	public void closeConnection() {
		//Close reader and writer
		try {
			out.close(); 
			in.close();
			
			//Close socket
			socket.close(); 
		}
		//Check for already closed
		catch (IOException e) {}
	}
	
	//Gets client address
	public String getAddress() {
		return address;
	}
}
