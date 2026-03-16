package spiel1;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class LevelReader {
	private File f;
	private String filePath;
	private BufferedReader in;
	private int zeilenAnzahl;
	
	
	private int swordLevel;
	private int spearLevel;
	
	private int money;
	
	
	
	public LevelReader() {
		f = null;
		filePath = "res/Level/";
		in = null;
	}
	
	
	public int getSwordLevel() {
		return swordLevel;
	}
	
	public int getSpearLevel() {
		return spearLevel;
	}
	
	public int getMoney() {
		return this.money;
	}

	    //Spielfeld kennt levelreader und nutzt updateLevel um level in schwert und so zu schreiben
	    
	
	
	// Lesen aus Leveldatei
	public String[] readLevel(int n) {		
		String[] datei = new String[zeilenAnzahl];
		
		try {
			f= new File(filePath + "level1.txt");
			in = new BufferedReader(new FileReader(f));
			//String zeile = null;
			
			for (int line_index=0; line_index < zeilenAnzahl;line_index++) {
				datei[line_index] = in.readLine();
				if(datei[line_index].equals("Schwert")) swordLevel = Integer.parseInt(datei[line_index+1]);
				if(datei[line_index].equals("money")) swordLevel = Integer.parseInt(datei[line_index+1]);
				if(datei[line_index].equals("Lance")) swordLevel = Integer.parseInt(datei[line_index+1]);
//				if(zeile != null) {
//					level.setLine(zeile, line_index);
//				}
			}}
			catch(IOException e) {
				e.printStackTrace();
				
				
				
				
			}
		finally {
			if(null!=in) {
				try {
					in.close();	
					}
				catch(IOException e) {
					e.printStackTrace();
				}
			}
			
		}
		return datei;
		
//		public void writeUsingBufferedWriter() {
//
//	        // try-with-resources
//	        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
//	            writer.write(content);
//	        } catch (IOException e) {
//	            e.printStackTrace();
//	            System.err.println("error with the BufferedWriter");
//	        }
//	    }
//		
		
	}
}