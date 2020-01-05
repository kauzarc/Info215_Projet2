import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Random;

public class Engine {

	/*
	 * mms.png
	 */
	private final LoadSavePNG m_image;
	private final double m_positionIm[][];

	/*
	 * gmm_data.d
	 */
	private final File m_file;
	private final double m_positionFile[][];

	private final Random m_random;

	/**
	 * Constructeur charge mms.png et gmm_data.d
	 * 
	 * @throws IOException
	 */
	public Engine() throws IOException {
		System.out.println("Initialisation");
		m_image = new LoadSavePNG("./", "mms.png");
		m_file = new File("./", "gmm_data.d");

		final Color colorMap[] = m_image.getImage();
		m_positionIm = new double[colorMap.length][3];

		for (int i = 0; i < colorMap.length; i++) {
			m_positionIm[i][0] = (double) colorMap[i].getRed() / 255.0;
			m_positionIm[i][1] = (double) colorMap[i].getGreen() / 255.0;
			m_positionIm[i][2] = (double) colorMap[i].getBlue() / 255.0;
		}

		final String file[] = m_file.read();
		m_positionFile = new double[file.length][2];

		for (int i = 0; i < file.length; i++) {
			final String vStr[] = file[i].split(" ");
			m_positionFile[i][0] = Double.valueOf(vStr[0]);
			m_positionFile[i][1] = Double.valueOf(vStr[1]);
		}

		m_random = new Random(123456789);
	}

	/**
	 * Execute les fonctions pour repondre au questions
	 * 
	 * @throws IOException
	 */
	public void run() throws IOException {
		test();

		segmentation();

		kScoreImage();

		kScoreFile();

		histo1D(1000);

		compress(5);
		compress(10);
		compress(15);
		compress(20);
	}

	/**
	 * Cree un histogramme a patrir d'un tableau de valeur
	 * 
	 * @param xmin
	 * @param xmax
	 * @param NbCases
	 * @param ech     le tableau de double
	 * @return un tableau 2D contenant les x associé a un nombre d'element
	 */
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

	/**
	 * Fonction test donne dans les instructions du projet
	 * 
	 * @throws IOException
	 */
	private void test() throws IOException {
		System.out.println("test:");
		final Color c = m_image.getPixel(0, 0);
		System.out.println("RGB = " + c.getRed() + " " + c.getGreen() + " " + c.getBlue());

		System.out
				.println("RGB normalisé= " + m_positionIm[0][0] + " " + m_positionIm[0][1] + " " + m_positionIm[0][2]);

		final Color[] tabColor = m_image.getImage();

		/** inversion des couleurs **/
		for (int i = 0; i < tabColor.length; i++)
			tabColor[i] = new Color(255 - tabColor[i].getRed(), 255 - tabColor[i].getGreen(),
					255 - tabColor[i].getBlue());

		LoadSavePNG.save(tabColor, "./result/", "test.png", m_image.getWidth(), m_image.getHeight());
	}

	/**
	 * algorithme de segmentation
	 */
	private void segmentation() {
		System.out.println("Segmentation:");
		final double centre[][] = new double[7][3];
		// jaune
		centre[0][0] = 0.89;
		centre[0][1] = 0.87;
		centre[0][2] = 0.09;

		// bleu
		centre[1][0] = 0.06;
		centre[1][1] = 0.41;
		centre[1][2] = 0.70;

		// orange
		centre[2][0] = 0.92;
		centre[2][1] = 0.36;
		centre[2][2] = 0.07;

		// vert
		centre[3][0] = 0.25;
		centre[3][1] = 0.81;
		centre[3][2] = 0.19;

		// rouge
		centre[4][0] = 0.62;
		centre[4][1] = 0.15;
		centre[4][2] = 0.15;

		// marron noir
		centre[5][0] = 0.20;
		centre[5][1] = 0.16;
		centre[5][2] = 0.11;

		// marron clair
		centre[6][0] = 0.90;
		centre[6][1] = 0.83;
		centre[6][2] = 0.64;

		// Kmeans.epoque(m_positionIm, centre, 100);

		// initialisation des variances
		final double[][] variance = new double[centre.length][centre[0].length];
		for (int i = 0; i < variance.length; i++) {
			for (int j = 0; j < variance[i].length; j++) {
				variance[i][j] = m_random.nextDouble() / 2.;
			}
		}

		// initialisation de roh
		final double roh[] = new double[centre.length];
		for (int i = 0; i < centre.length; i++) {
			roh[i] = 1. / (double) centre.length;
		}

		// 100 epoque max
		final double[][] assignement = MixGauss.epoque(m_positionIm, centre, variance, roh, 100);

		// creation d'un tableau contennant le centre auquels est associée chaque pixel
		final int index[] = new int[m_positionIm.length];
		for (int i = 0; i < m_positionIm.length; i++) {
			index[i] = indexOfMax(assignement[i]);
		}

		// nouvelle image ou chaque pixel est remplace par la couleur du centre
		final Color[] out = new Color[m_positionIm.length];
		for (int i = 0; i < assignement.length; i++) {
			out[i] = new Color((int) (centre[index[i]][0] * 255.), (int) (centre[index[i]][1] * 255.),
					(int) (centre[index[i]][2] * 255.));
		}

		// sauvegarde de l'image
		try {
			LoadSavePNG.save(out, "./result/", "segmentation.png", m_image.getWidth(), m_image.getHeight());
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * cree un fichier contenant le score apres convergence pour differente valeurs
	 * de k pour les pixel de l'image
	 */
	private void kScoreImage() {
		System.out.println("kScoreImage:");
		final int mink = 2;
		final int maxk = 10;
		// nombre d'essai a chaque centre
		final int nit = 10;
		// pour stocker k associer au score
		final double score[][] = new double[maxk - mink + 1][2];

		for (int k = mink; k <= maxk; k++) {
			System.out.println("-k=" + k);
			// initialise le score pour un nombre de scores donne
			score[k - mink][0] = k;
			score[k - mink][1] = -Double.MAX_VALUE;

			// nit condition initial
			for (int it = 0; it < nit; it++) {

				// initialise les centres
				final double centre[][] = new double[k][3];
				for (int i = 0; i < k; i++) {
					centre[i][0] = m_random.nextDouble();
					centre[i][1] = m_random.nextDouble();
					centre[i][2] = m_random.nextDouble();
				}
				Kmeans.epoque(m_positionIm, centre, 100);

				// initialise les variances
				final double[][] variance = new double[centre.length][centre[0].length];
				for (int i = 0; i < variance.length; i++) {
					for (int j = 0; j < variance[i].length; j++) {
						variance[i][j] = m_random.nextDouble() / 2.;
					}
				}

				// initialise les valeurs de roh
				final double roh[] = new double[centre.length];
				for (int i = 0; i < centre.length; i++) {
					roh[i] = 1. / (double) centre.length;
				}

				// 100 epoque maximum
				MixGauss.epoque(m_positionIm, centre, variance, roh, 100);

				// garde le meilleur score pour chaque valeur de k
				final double s = MixGauss.score(m_positionIm, centre, variance, roh);
				if (s > score[k - mink][1]) {
					score[k - mink][1] = s;
				}
			}
		}

		// sauvegarde le resultat
		try {
			final SaveFile file = new SaveFile("./result/", "kScoreImage.txt");
			file.saveMatrix(score);
			file.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * cree un fichier contenant le score apres convergence pour differente valeurs
	 * de k pour les points de gmm_data.d ->voir les commentaires de "kScoreImage()"
	 */
	private void kScoreFile() {
		System.out.println("kScoreFile:");
		final int mink = 2;
		final int maxk = 10;
		final int nit = 10;
		final double score[][] = new double[maxk - mink + 1][2];

		for (int k = mink; k <= maxk; k++) {
			System.out.println("-k=" + k);
			score[k - mink][0] = k;
			score[k - mink][1] = -Double.MAX_VALUE;

			for (int it = 0; it < nit; it++) {

				final double centre[][] = new double[k][2];
				for (int i = 0; i < k; i++) {
					centre[i][0] = m_random.nextDouble();
					centre[i][1] = m_random.nextDouble();
				}

				Kmeans.epoque(m_positionFile, centre, 100);

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

				MixGauss.epoque(m_positionFile, centre, variance, roh, 100);

				final double s = MixGauss.score(m_positionFile, centre, variance, roh);
				if (s > score[k - mink][1]) {
					score[k - mink][1] = s;
				}
			}
		}

		try {
			final SaveFile file = new SaveFile("./result/", "kScoreFile.txt");
			file.saveMatrix(score);
			file.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Test l'algorithme avec n points repartis selon une mixture de loi normale en
	 * essayant de trouver les valeurs des gaussienne utiliser, les resultats sont
	 * inscrit dans des fichiers
	 * 
	 * @param n
	 */
	private void histo1D(final int n) {
		System.out.println("histo1D:");
		// initialise des points selon des lois normales
		final double position[][] = new double[n][1];
		for (int i = 0; 2 * i < n; i++) {
			position[2 * i][0] = m_random.nextGaussian() * 1.5 + 3;
			position[2 * i + 1][0] = m_random.nextGaussian() * 0.2 - 2;
		}

		// initialisation des centres
		final double centre[][] = new double[2][1];
		centre[0][0] = -1.;
		centre[1][0] = 1.;
		Kmeans.epoque(position, centre, 100);

		// initialise la variance
		final double[][] variance = new double[centre.length][centre[0].length];
		for (int i = 0; i < variance.length; i++) {
			for (int j = 0; j < variance[i].length; j++) {
				variance[i][j] = m_random.nextDouble() / 2.;
			}
		}

		// initialise roh
		final double roh[] = new double[centre.length];
		for (int i = 0; i < centre.length; i++) {
			roh[i] = 1. / (double) centre.length;
		}

		// 100 epoque maximum
		MixGauss.epoque(position, centre, variance, roh, 100);

		// creation de l'histogramme
		final double tab[] = new double[position.length];
		for (int i = 0; i < position.length; i++) {
			tab[i] = position[i][0];
		}
		final double histo[][] = histogramme(-5., 10., 100, tab);

		// organisation et normalisation des donnee de l'histogramme
		final double histoNorm[][] = new double[histo[0].length][2];
		for (int i = 0; i < histo[0].length; i++) {
			histoNorm[i][0] = histo[0][i];
			histoNorm[i][1] = histo[1][i] / (double) n;
		}

		// preparation a la sauvegarde des imformation des gaussienes
		final double gauss[][] = { { centre[0][0], variance[0][0], roh[0] }, { centre[1][0], variance[1][0], roh[1] } };

		// sauvegarde
		try {
			final SaveFile f1 = new SaveFile("./result/", "histo1D_histo.txt");
			final SaveFile f2 = new SaveFile("./result/", "histo1D_gauss");

			f1.saveMatrix(histoNorm);
			f2.saveMatrix(gauss);

			f1.close();
			f2.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Implementation de l'algorithme de compression du projet
	 * 
	 * @param k nombre de centres utilisé
	 * @throws IOException
	 */
	private void compress(final int k) throws IOException {
		System.out.println("compress:");
		// initialisation des centres
		final double centre[][] = new double[k][3];
		for (int i = 0; i < k; i++) {
			centre[i][0] = m_random.nextDouble();
			centre[i][1] = m_random.nextDouble();
			centre[i][2] = m_random.nextDouble();
		}
		Kmeans.epoque(m_positionIm, centre, 100);

		// initialisation des variances
		final double[][] variance = new double[centre.length][centre[0].length];
		for (int i = 0; i < variance.length; i++) {
			for (int j = 0; j < variance[i].length; j++) {
				variance[i][j] = m_random.nextDouble() / 2.;
			}
		}

		// initialisation de roh
		final double roh[] = new double[centre.length];
		for (int i = 0; i < centre.length; i++) {
			roh[i] = 1. / (double) centre.length;
		}

		// 100 epoque max
		final double[][] assignement = MixGauss.epoque(m_positionIm, centre, variance, roh, 100);

		// creation d'un tableau contennant le centre auquels est associée chaque pixel
		final int index[] = new int[m_positionIm.length];
		for (int i = 0; i < m_positionIm.length; i++) {
			index[i] = indexOfMax(assignement[i]);
		}

		// nouvelle image ou chaque pixel est remplace par la couleur du centre
		final Color[] out = new Color[m_positionIm.length];
		for (int i = 0; i < assignement.length; i++) {
			out[i] = new Color((int) (centre[index[i]][0] * 255.), (int) (centre[index[i]][1] * 255.),
					(int) (centre[index[i]][2] * 255.));
		}

		// sauvegarde de l'image
		LoadSavePNG.save(out, "./result/", "compress_" + k + ".png", m_image.getWidth(), m_image.getHeight());

		// compte le nombre dse point assigner a chaque centre
		final int count[] = new int[centre.length];
		for (int i = 0; i < count.length; i++) {
			count[i] = 0;
		}
		for (int i = 0; i < index.length; i++) {
			count[index[i]]++;
		}

		// sauvegarde les valeurs des centres et le nombre de point assigner
		final SaveFile result = new SaveFile("./result/", "compress_" + k + ".csv");
		result.saveAssignement(centre, count);
		result.close();

		// sauvegarde une image avec un pixel par centres
		final Color[] colors = new Color[centre.length];
		for (int i = 0; i < centre.length; i++) {
			colors[i] = new Color((int) (centre[i][0] * 255.), (int) (centre[i][1] * 255.),
					(int) (centre[i][2] * 255.));
		}
		LoadSavePNG.save(colors, "./result/", "compress_colors_" + k + ".png", centre.length, 1);
	}

	/**
	 * @param tab un tableau de valeur
	 * @return l'index de la plus grande valeur
	 */
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
