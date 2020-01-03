
public class MixGauss {

	private static double distance2(final double x[], final double centre[]) {
		double result = 0;
		for (int i = 0; i < x.length; i++) {
			result += Math.pow(x[i] - centre[i], 2);
		}
		result = Math.sqrt(result);
		return result;
	}

	private static double[][] assigner(final double[][] X, final double[][] centres, final double variance[][],
			final double roh[]) {
		final double result[][] = new double[X.length][centres.length];

		for (int d = 0; d < X.length; d++) {

			final double produit[] = new double[centres.length];
			for (int l = 0; l < centres.length; l++) {

				produit[l] = roh[l];
				for (int i = 0; i < X[d].length; i++) {
					double num = Math.pow(X[d][i] - centres[l][i], 2) / (-2. * variance[l][i]);
					num = Math.exp(num);

					double den = 2 * Math.PI * variance[l][i];
					den = Math.sqrt(den);

					produit[l] *= num / den;
				}
			}

			double somme = 0;
			for (int l = 0; l < centres.length; l++) {
				somme += produit[l];
			}

			for (int k = 0; k < centres.length; k++) {
				result[d][k] = produit[k] / somme;
			}
		}

		return result;
	}

	private static double deplct(final double X[][], final double centres[][], final double assignement[][],
			final double variance[][], final double roh[]) {

		final double[][] ancienCentres = new double[centres.length][centres[0].length];
		for (int i = 0; i < centres.length; i++) {
			for (int j = 0; j < centres[i].length; j++) {
				ancienCentres[i][j] = centres[i][j];
			}
		}

		for (int k = 0; k < centres.length; k++) {

			double R = 0.;
			for (int d = 0; d < X.length; d++) {
				R += assignement[d][k];
			}

			for (int i = 0; i < centres[k].length; i++) {

				centres[k][i] = 0.;
				for (int d = 0; d < X.length; d++) {
					centres[k][i] += assignement[d][k] * X[d][i];
				}
				centres[k][i] /= R;

				variance[k][i] = 0.;
				for (int d = 0; d < X.length; d++) {
					variance[k][i] += assignement[d][k] * Math.pow(X[d][i] - centres[k][i], 2);
				}
				variance[k][i] /= R;
			}

			roh[k] = R / X.length;
		}

		double result = 0;
		for (int i = 0; i < centres.length; i++) {
			result += distance2(centres[i], ancienCentres[i]);
		}

		return result;
	}

	public static double[][] epoque(final double X[][], final double centres[][], final int n) {
		double assignement[][] = null;

		double score = Double.MAX_VALUE;

		final double[][] variance = new double[centres.length][centres[0].length];
		for (int i = 0; i < variance.length; i++) {
			for (int j = 0; j < variance[i].length; j++) {
				variance[i][j] = 0.2;
			}
		}

		final double roh[] = new double[centres.length];
		for (int k = 0; k < centres.length; k++) {
			roh[k] = 1. / (double) centres.length;
		}

		for (int i = 0; i < n && score != 0; i++) {
			assignement = assigner(X, centres, variance, roh);
			score = deplct(X, centres, assignement, variance, roh);
		}

		return assignement;
	}
}