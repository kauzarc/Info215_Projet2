import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class SaveFile {
	private final PrintWriter m_writer;

	SaveFile(final String path, final String name) throws FileNotFoundException, UnsupportedEncodingException {
		m_writer = new PrintWriter(path + name, "UTF-8");
	}

	void saveAssignement(final double centres[][], final int assignement[]) {
		for (int k = 0; k < centres.length; k++) {
			String str = "";
			for (int i = 0; i < centres[k].length; i++) {
				str += centres[k][i] + ";";
			}
			str += assignement[k];
			m_writer.println(str);
		}
	}

	void close() {
		m_writer.close();
	}
}
