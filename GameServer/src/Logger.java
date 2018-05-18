import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
	DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private FileOutputStream cFile;
	private FileOutputStream gFile;
	

	//Constructor sets up files and output streams
	public Logger(String cname, String gname) {
		File comLog = new File(cname);
		File gameLog = new File(gname);
		
		//Attempt to create files
		try {
			comLog.createNewFile();
			gameLog.createNewFile();
		} 
		//Catch if already exists
		catch (IOException e1) {}
		
		//Connect output stream to files
		try {
			cFile = new FileOutputStream(comLog, true);
			gFile = new FileOutputStream(gameLog, true);
		} catch (FileNotFoundException e) {
			System.err.println("Could not find file");
		} 
	
	
	}
	
	/* Generate byte string to write to file
	 * @param message String message to log
	 */
	private byte[] logStr(String message) {
		String write="";
		write+=getDate()+" "+message+"\n";
		
		return write.getBytes();
		
	}
	
	/*Get current datetime for log
	 * @return String current datetime
	 */
	public String getDate() {
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	/* Log communication from single player
	 * @param play Player Single player
	 * @param message String message to log
	 */
	public void logCommunication(Player play, String message) {
		try {
		cFile.write(logStr(play.getAddress()+" "+message));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/* Log game info from single player
	 * @param play Player Single player
	 * @param message String message to log
	 */
	public void logGame(Player play,String message) {
		try {
			gFile.write(logStr(play.getAddress()+" "+message));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/* Log communication from multi player
	 * @param play Player multi player
	 * @param message String message to log
	 */
	public void logCommunication(ThreadPlayer play, String message) {
		try {
			cFile.write(logStr(play.getAddress()+" "+message));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/* Log game info from multi player
	 * @param play Player multi player
	 * @param message String message to log
	 */
	public void logGame(ThreadPlayer play,String message) {
		try {
			gFile.write(logStr(play.getAddress()+" "+message));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/* Log general game information
	 * @param message String message to log
	 */
	public void logGame(String message) {
		try {
			gFile.write(logStr(message));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//Close file
	public void close() {
		try {
			cFile.close();
			gFile.close();
		} catch (IOException e) {
			System.err.println("Logger file closed poorly");
		}
	}
}
