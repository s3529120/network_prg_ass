
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class server {
	public final static String COMMUNICATION_LOG_FILE = "comlog.dat";
	public final static String GAME_LOG_FILE = "gamelog.dat";

	public static void main(String[] args) {
		int serverPort = 19120;
		Logger log = new Logger(COMMUNICATION_LOG_FILE,GAME_LOG_FILE);
		
		//Declare socket
		Socket clientSocket=null;
		
		//Start message
		System.out.println("Starting server");

		//Generate socket
		try {
			clientSocket = generateSocket(serverPort);
		} catch (IOException e) {
			System.err.println("IO exception encountered." );
		} 
		
		//Player connected message
		System.out.println("Player connected");

		//Generate player object
		Player player = new Player(clientSocket);
		
		//Start game
		Game game = new Game(player,log);
	
		//Play game
		game.play();
		
		//Game finished message
		System.out.println("Game finished");

		//Close objects and streams
		game.end();
		log.close();

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
