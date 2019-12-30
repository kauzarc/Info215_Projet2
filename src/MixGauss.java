
public class MixGauss {

	private static double[][] assigner(double[][] X, double[][] centres, double variance[][], double roh[]) {
		double result[][] = new double[X.length][centres.length];

		for (int d = 0; d < X.length; d++) {

			double produit[] = new double[centres.length];
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

	private static double deplct(double X[][], double centres[][], double assignement[][]) {
		return 0;
	}

	public static double[][] epoque(double X[][], double centres[][], int n) {
		double assignement[][] = null;

		double score = Double.MAX_VALUE;
		double[][] variance = new double[centres.length][X.length];
		double roh[] = new double[centres.length];

		for (int i = 0; i < n && score != 0; i++) {
			assignement = assigner(X, centres, variance, roh);
			score = deplct(X, centres, assignement);
		}

		return assignement;
	}
}