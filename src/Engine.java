import java.awt.Color;
import java.io.IOException;
import java.util.Random;

public class Engine {

	LoadSavePNG m_image;
	double m_position[][];

	Random m_random;

	public Engine() throws IOException {
		m_image = new LoadSavePNG("./", "mms.png");

		Color colorMap[] = m_image.getImage();
		m_position = new double[colorMap.length][3];

		for (int i = 0; i < colorMap.length; i++) {
			m_position[i][0] = (double) colorMap[i].getRed() / 255.0;
			m_position[i][1] = (double) colorMap[i].getGreen() / 255.0;
			m_position[i][2] = (double) colorMap[i].getBlue() / 255.0;
		}

		m_random = new Random(123456789);
	}

	public void run() throws IOException {
		test();
		compress();
	}

	private void test() throws IOException {
		Color c = m_image.getPixel(0, 0);
		System.out.println("RGB = " + c.getRed() + " " + c.getGreen() + " " + c.getBlue());

		System.out.println("RGB normalisé= " + m_position[0][0] + " " + m_position[0][1] + " " + m_position[0][2]);

		Color[] tabColor = m_image.getImage();

		/** inversion des couleurs **/
		for (int i = 0; i < tabColor.length; i++)
			tabColor[i] = new Color(255 - tabColor[i].getRed(), 255 - tabColor[i].getGreen(),
					255 - tabColor[i].getBlue());

		LoadSavePNG.save(tabColor, "./result/", "test.png", m_image.getWidth(), m_image.getHeight());
	}

	private void compress() throws IOException {
		final int k = 15;
		double centre[][] = new double[k][3];
		for (int i = 0; i < k; i++) {
			centre[i][0] = m_random.nextDouble();
			centre[i][1] = m_random.nextDouble();
			centre[i][2] = m_random.nextDouble();
		}

		double[][] assignement = MixGauss.epoque(m_position, centre, 100);

		int index[] = new int[m_position.length];
		for (int i = 0; i < m_position.length; i++) {
			index[i] = indexOfMax(assignement[i]);
		}

		Color[] out = new Color[m_position.length];

		for (int i = 0; i < assignement.length; i++) {
			// System.out.println("" + centre[index[i]][0] + " " + centre[index[i]][1] + " " + centre[index[i]][2]);
			out[i] = new Color((int) (centre[index[i]][0] * 255.), (int) (centre[index[i]][1] * 255.),
					(int) (centre[index[i]][2] * 255.));
		}

		String name = "compress_" + k + ".png";
		LoadSavePNG.save(out, "./result/", name, m_image.getWidth(), m_image.getHeight());
	}

	private int indexOfMax(double tab[]) {
		int result = 0;
		for (int i = 1; i < tab.length; i++) {
			if (tab[i] > tab[result]) {
				result = i;
			}
		}
		return result;
	}
}
