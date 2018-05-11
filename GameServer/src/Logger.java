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
	
	public Logger(String cname, String gname) {
		File comLog = new File(cname);
		File gameLog = new File(gname);
		try {
			comLog.createNewFile();
			gameLog.createNewFile();
		} catch (IOException e1) {
		try {
			cFile = new FileOutputStream(comLog, true);
			gFile = new FileOutputStream(gameLog, true);
		} catch (FileNotFoundException e) {
		} 
	}
	
	}
	
	private byte[] logStr(Player play, String message) {
		String write="";
		write+=getDate()+" "+play.getAddress()+" "+message+"\n";
		
		return write.getBytes();
		
	}
	
	public String getDate() {
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	public void logCommunication(Player play, String message) {
		try {
			cFile.write(logStr(play, message));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void logGame(Player play,String message) {
		try {
			gFile.write(logStr(play, message));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
		try {
			cFile.close();close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
