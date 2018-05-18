import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

//Server terminator exits server if x is read in
public class ServerTerminator extends Thread{
	//Read in until x is entered then terminate server
	public void run() {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		
		//Read until x is entered
		String input;
		try {
			while((input=in.readLine())!=null){
				if(input.compareToIgnoreCase("x")==0) {
					terminateServer();
				}
			}
		}
		//Terminate on IO error
		catch (IOException e) {
			terminateServer();
		}
	}
	
	//Call server to terminate
	public void terminateServer() {
		multiserver.closeServer();
	}
	
	
}
