import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Player {
		private PrintWriter out; 
		private BufferedReader in;
		private Socket socket;
	public Player(Socket clientSocket) {
		//Generate readers and writer
		try {
			socket=clientSocket;
			out = new PrintWriter(clientSocket.getOutputStream(), true); 
			in = generateReader(clientSocket);
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
}
