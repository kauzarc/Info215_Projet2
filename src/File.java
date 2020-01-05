import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class File {
	BufferedReader m_fileReader;

	public File(String path, String name) throws FileNotFoundException {
		m_fileReader = new BufferedReader(new FileReader(path + name));
	}

	public String[] read() throws IOException {
		ArrayList<String> result = new ArrayList<>();

		String st;
		while ((st = m_fileReader.readLine()) != null) {
			result.add(st);
		}

		return result.toArray(new String[result.size()]);
	}
}
