
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class multiserver {
	public final static String COMMUNICATION_LOG_FILE = "comlog.dat";
	public final static String GAME_LOG_FILE = "gamelog.dat";

	public static void main(String[] args) {
		int serverPort = 19120;
		Logger log = new Logger(COMMUNICATION_LOG_FILE,GAME_LOG_FILE);
		List<ThreadPlayer> players;
		ThreadPlayer play;
		
		//Generate sockets
		Socket clientSocket=null;
		
		players = new LinkedList<ThreadPlayer>();
		
		while(true) {
			if(players.size()<2) {
				try {
					if(players.size()==0) {
						play = new ThreadPlayer(generateSocket(serverPort),true);
					}else {
						play = new ThreadPlayer(generateSocket(serverPort));
					}
					players.add(play);
					play.writeLine("Connected waiting for players");
					
				} catch (IOException e) {
					System.err.println("IO exception encountered." );
				} 
			}else {
				//Start game
				MultiGame game = new MultiGame((ThreadPlayer[])players.toArray(),log);
				players.clear();
				//Play game
				game.start();
			}
		
		}
		//log.close();

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
	
	