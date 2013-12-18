import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Filehandler {
	// READ IS WHAT THE BOXLAB SERVER HAS DOWNLOADED
	private final static String DIRLOCATIONREAD = "..//..//boxlab-server//data//in";
	private final static String DIRLOCATIONWRITE = "..//..//boxlab-server//data//out";
	private final static String FILETOPI = ".json.download";
	private final static String FILEFROMPI = ".json.send";
	private final static String SEPARETOR = "ñ";

	public Filehandler() {
		File dirRead = new File(DIRLOCATIONREAD);
		if (!dirRead.exists()) {
			dirRead.mkdir();
		}

		File dirWrite = new File(DIRLOCATIONWRITE);
		if (!dirWrite.exists()) {
			dirWrite.mkdir();
		}
	}

	public void writeToFile(List<String> msg) {
		String fileName =  DIRLOCATIONWRITE + "//" +new SimpleDateFormat("yyyy-MM-dd hh-mm-ss").format(new Date())+FILETOPI;
		
		File fileToWrite = new File(fileName);

		try {
			fileToWrite.createNewFile();
			FileWriter fstream = new FileWriter(fileToWrite);
			BufferedWriter writer = new BufferedWriter(fstream);
			for (String string : msg) {
				writer.write(string);
				writer.newLine();
			}
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public List<String> readFromFiles() {
		List<String> inFromFiles = new ArrayList<>();
		File directory = new File(DIRLOCATIONREAD);
		File[] acceptedFiles = directory.listFiles(new FilenameFilter() {
			public boolean accept(File directory, String fileName) {
				return fileName.endsWith(FILEFROMPI);
			}
		});
		for (File file : acceptedFiles) {
			BufferedReader br = null;
			try {
				String sCurrentLine;
				br = new BufferedReader(new FileReader(file));
				while ((sCurrentLine = br.readLine()) != null) {
					inFromFiles.add(sCurrentLine+SEPARETOR);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					if (br != null)
						br.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
				//deleteReadedFiles();
			}

		}
		return inFromFiles;
	}

	public void deleteReadedFiles() {
		File directory = new File(DIRLOCATIONREAD);
		File[] acceptedFiles = directory.listFiles(new FilenameFilter() {
			public boolean accept(File directory, String fileName) {
				return fileName.endsWith(FILEFROMPI);
			}
		});
		for (File file : acceptedFiles) {
			file.delete();
		}

	}
}
