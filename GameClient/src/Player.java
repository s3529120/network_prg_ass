import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Player {
		PrintWriter out; 
		BufferedReader in;
		Socket socket;
	public Player(Socket clientSocket) {
		//Generate readers and writer
		try {
			socket=clientSocket;
			out = new PrintWriter(clientSocket.getOutputStream(), true); 
			in = client.generateReader(clientSocket.getInputStream());
		} catch (IOException e) {
			System.err.println("Failed to generate reader/writer for client socket.");
		}
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
}
