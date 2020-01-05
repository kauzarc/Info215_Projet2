import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Random;

public class Engine {

	private final LoadSavePNG m_image;
	private final double m_position[][];

	private final Random m_random;

	public Engine() throws IOException {
		m_image = new LoadSavePNG("./", "mms.png");

		final Color colorMap[] = m_image.getImage();
		m_position = new double[colorMap.length][3];

		for (int i = 0; i < colorMap.length; i++) {
			m_position[i][0] = (double) colorMap[i].getRed() / 255.0;
			m_position[i][1] = (double) colorMap[i].getGreen() / 255.0;
			m_position[i][2] = (double) colorMap[i].getBlue() / 255.0;
		}

		m_random = new Random(123456789);
	}

	public void run() throws IOException {
		// test();

		// kScoreImage();

		histo1D(1000);

		// compress(5);
		// compress(10);
		// compress(15);
		// compress(20);
	}

	private static double[][] histogramme(final double xmin, final double xmax, final int NbCases, final double[] ech) {
		final double[][] Histo = new double[2][NbCases];
		final double size = (xmax - xmin) / (double) NbCases;

		for (int i = 0; i < NbCases; i++) {
			Histo[0][i] = xmin + (size * (double) i);
		}

		for (int i = 0; i < ech.length; i++) {
			final int index = (int) Math.floor((ech[i] - xmin) / size);
			if (index >= 0 && index < NbCases) {
				Histo[1][index]++;
			}
		}
		return Histo;
	}

	private void test() throws IOException {
		final Color c = m_image.getPixel(0, 0);
		System.out.println("RGB = " + c.getRed() + " " + c.getGreen() + " " + c.getBlue());

		System.out.println("RGB normalisé= " + m_position[0][0] + " " + m_position[0][1] + " " + m_position[0][2]);

		final Color[] tabColor = m_image.getImage();

		/** inversion des couleurs **/
		for (int i = 0; i < tabColor.length; i++)
			tabColor[i] = new Color(255 - tabColor[i].getRed(), 255 - tabColor[i].getGreen(),
					255 - tabColor[i].getBlue());

		LoadSavePNG.save(tabColor, "./result/", "test.png", m_image.getWidth(), m_image.getHeight());
	}

	private void kScoreImage() {
		System.out.println("kScoreImage:");
		final int mink = 2;
		final int maxk = 20;
		final int nit = 10;
		final double score[] = new double[maxk - mink + 1];

		for (int k = mink; k <= maxk; k++) {
			System.out.println("-k=" + k);
			score[k - mink] = Double.MIN_VALUE;
			;

			for (int it = 0; it < nit; it++) {

				final double centre[][] = new double[k][3];
				for (int i = 0; i < k; i++) {
					centre[i][0] = m_random.nextDouble();
					centre[i][1] = m_random.nextDouble();
					centre[i][2] = m_random.nextDouble();
				}

				Kmeans.epoque(m_position, centre, 100);

				final double[][] variance = new double[centre.length][centre[0].length];
				for (int i = 0; i < variance.length; i++) {
					for (int j = 0; j < variance[i].length; j++) {
						variance[i][j] = m_random.nextDouble() / 2.;
					}
				}

				final double roh[] = new double[centre.length];
				for (int i = 0; i < centre.length; i++) {
					roh[i] = 1. / (double) centre.length;
				}

				MixGauss.epoque(m_position, centre, variance, roh, 100);

				final double s = MixGauss.score(m_position, centre, variance, roh);
				System.out.println(s);
				if (s > score[k - mink]) {
					score[k - mink] = s;
				}
			}
		}

		try {
			final SaveFile file = new SaveFile("./result/", "kScoreImage.txt");
			file.saveDouble(score);
			file.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	private void histo1D(final int n) {
		double position[][] = new double[n][1];
		for (int i = 0; 2 * i < n; i++) {
			position[2 * i][0] = m_random.nextGaussian() * 1.5 + 3;
			position[2 * i + 1][0] = m_random.nextGaussian() * 0.2 - 2;
		}

		final double centre[][] = new double[2][1];
		centre[0][0] = -1.;
		centre[1][0] = 1.;
		Kmeans.epoque(position, centre, 100);

		final double[][] variance = new double[centre.length][centre[0].length];
		for (int i = 0; i < variance.length; i++) {
			for (int j = 0; j < variance[i].length; j++) {
				variance[i][j] = m_random.nextDouble() / 2.;
			}
		}

		final double roh[] = new double[centre.length];
		for (int i = 0; i < centre.length; i++) {
			roh[i] = 1. / (double) centre.length;
		}

		MixGauss.epoque(position, centre, variance, roh, 100);

		double tab[] = new double[position.length];
		for (int i = 0; i < position.length; i++) {
			tab[i] = position[i][0];
		}
		double histo[][] = histogramme(-5., 10., 100, tab);
		double histoNorm[][] = new double[histo[0].length][2];
		for (int i = 0; i < histo[0].length; i++) {
			histoNorm[i][0] = histo[0][i];
			histoNorm[i][1] = histo[1][i] / (double) n;
		}

		double gauss[][] = { { centre[0][0], variance[0][0], roh[0] }, { centre[1][0], variance[1][0], roh[1] } };

		try {
			SaveFile f1 = new SaveFile("./result/", "histo1D_histo.txt");
			SaveFile f2 = new SaveFile("./result/", "histo1D_gauss");

			f1.saveMatrix(histoNorm);
			f2.saveMatrix(gauss);

			f1.close();
			f2.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	private void compress(final int k) throws IOException {
		final double centre[][] = new double[k][3];
		for (int i = 0; i < k; i++) {
			centre[i][0] = m_random.nextDouble();
			centre[i][1] = m_random.nextDouble();
			centre[i][2] = m_random.nextDouble();
		}

		Kmeans.epoque(m_position, centre, 100);

		final double[][] variance = new double[centre.length][centre[0].length];
		for (int i = 0; i < variance.length; i++) {
			for (int j = 0; j < variance[i].length; j++) {
				variance[i][j] = m_random.nextDouble() / 2.;
			}
		}

		final double roh[] = new double[centre.length];
		for (int i = 0; i < centre.length; i++) {
			roh[i] = 1. / (double) centre.length;
		}

		final double[][] assignement = MixGauss.epoque(m_position, centre, variance, roh, 100);

		final int index[] = new int[m_position.length];
		for (int i = 0; i < m_position.length; i++) {
			index[i] = indexOfMax(assignement[i]);
		}

		final Color[] out = new Color[m_position.length];

		for (int i = 0; i < assignement.length; i++) {
			out[i] = new Color((int) (centre[index[i]][0] * 255.), (int) (centre[index[i]][1] * 255.),
					(int) (centre[index[i]][2] * 255.));
		}

		LoadSavePNG.save(out, "./result/", "compress_" + k + ".png", m_image.getWidth(), m_image.getHeight());

		final int count[] = new int[centre.length];
		for (int i = 0; i < count.length; i++) {
			count[i] = 0;
		}
		for (int i = 0; i < index.length; i++) {
			count[index[i]]++;
		}

		final SaveFile result = new SaveFile("./result/", "compress_" + k + ".csv");
		result.saveAssignement(centre, count);
		result.close();

		final Color[] colors = new Color[centre.length];
		for (int i = 0; i < centre.length; i++) {
			colors[i] = new Color((int) (centre[i][0] * 255.), (int) (centre[i][1] * 255.),
					(int) (centre[i][2] * 255.));
		}
		LoadSavePNG.save(colors, "./result/", "compress_colors_" + k + ".png", centre.length, 1);
	}

	private int indexOfMax(final double tab[]) {
		int result = 0;
		for (int i = 1; i < tab.length; i++) {
			if (tab[i] > tab[result]) {
				result = i;
			}
		}
		return result;
	}
}
