
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class multiserver {
	public final static String COMMUNICATION_LOG_FILE = "comlog.dat";
	public final static String GAME_LOG_FILE = "gamelog.dat";
	public final static int SERVER_PORT = 19120;

	static boolean run=true;

	public static void main(String[] args) {
		long waiting =0, start=0;
		Logger log = new Logger(COMMUNICATION_LOG_FILE,GAME_LOG_FILE);
		List<ThreadPlayer> players;
		ThreadPlayer play;
		Socket soc;
		ServerSocket ss=null;
		ServerTerminator st=null;
	
		//Initialize server socket
		try{
			ss = new ServerSocket(SERVER_PORT);	
		}
		//Exit if cannot initiate server socket
		catch(IOException e){System.exit(1);}
		
		//Initialize players list
		players = new LinkedList<ThreadPlayer>();
		

		//Start message
		System.out.println("Server started");
		
		//Start termination thread
		st = new ServerTerminator();
		
		//Run continuously until terminate
		while(run) {
			
			//Check if should look for more connections or start game
			if((players.size()==2 && waiting>30000)||(players.size()<2)) {
				try {
					//Accept connection from client
					soc = ss.accept();
					
					//Set waiting time
					waiting=System.currentTimeMillis()-start;
					
					//Check if player is first to list
					if(players.size()==0) {
						play = new ThreadPlayer(soc);
						play.setFirst();
						
						//Set waiting start time
						start=System.currentTimeMillis();
					}else {
						play = new ThreadPlayer(soc);
					}
					
					//Add player to list
					players.add(play);
					
					//Inform player of connection
					play.writeLine("Connected waiting for players");
					
				} 
				//Catch connection error
				catch (IOException e) {
					System.err.println("Connection error occurred");
				} 
			}
			//If enough players and.or time elapsed to start game
			else {
				//Start game
				MultiGame game = new MultiGame(players.toArray(new ThreadPlayer[players.size()]),log);
				
				//Remove players from list
				players.clear();
				
				//If players waiting set new first
				if(players.size()!=0) {
					players.get(0);
					start=System.currentTimeMillis();
				}
				//Start game
				game.start();
			}
		
		}
		
		//Close serverport
		try {
			ss.close();
		} catch (IOException e) {
			System.err.println("Serverport closed unexpectedly");
		}
		
		//Close logger
		log.close();
		
		//Termination message
		System.out.println("Server terminated");

	}
	
	//Allow server to close
	public static void closeServer() {
		run = false;
	}
	
	
}
	
	
