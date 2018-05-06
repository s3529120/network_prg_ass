import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class server {

	public static void main(String[] args) {
		int serverPort = 19120;
		
		//Generate sockets
		Socket clientSocket = generateSocket(serverPort); 
		
		//Generate player object
		Player player = new Player(clientSocket);
		
		//Start game
		Game game = new Game(player);
		
		//Play game
		game.play();
		
		game.end();

	}
	
	/*Generates a socket object and sets to accept connection
	 * @param portnum port number to accept connection through
	 * @return socket object accepting connection
	 */
	public static Socket generateSocket(int portnum) throws IOException{
		ServerSocket socket = new ServerSocket(portnum); 
		return socket.accept(); 
		
	}
	
	

}
