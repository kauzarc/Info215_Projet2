public class Kmeans {

	private static double distance2(double x[], double centre[]) {
		double result = 0;
		for (int i = 0; i < x.length; i++) {
			result += Math.pow(x[i] - centre[i], 2);
		}
		result = Math.sqrt(result);
		return result;
	}

	private static int[] assigner(double[][] X, double[][] centres) {
		int result[] = new int[X.length];

		for (int i = 0; i < X.length; i++) {

			double min = Double.MAX_VALUE;
			int minId = -1;

			for (int j = 0; j < centres.length; j++) {
				double buff = distance2(X[i], centres[j]);
				if (buff < min) {
					min = buff;
					minId = j;
				}
			}
			result[i] = minId;
		}

		return result;
	}

	private static void somme(double a[], double b[]) {
		for (int i = 0; i < a.length; i++) {
			a[i] += b[i];
		}
	}

	private static double deplct(double X[][], double centre[][], int assignement[]) {
		double[][] ancienCentre = new double[centre.length][centre[0].length];
		for (int i = 0; i < centre.length; i++) {
			for (int j = 0; j < centre[i].length; j++) {
				ancienCentre[i][j] = centre[i][j];
			}
		}

		int count[] = new int[centre.length];
		for (int i = 0; i < count.length; i++) {
			count[i] = 0;
		}

		for (int i = 0; i < assignement.length; i++) {
			count[assignement[i]]++;
		}

		for (int i = 0; i < centre.length; i++) {
			if (count[i] != 0) {
				for (int j = 0; j < centre[i].length; j++) {
					centre[i][j] = 0;
				}
			}
		}

		for (int i = 0; i < X.length; i++) {
			somme(centre[assignement[i]], X[i]);
		}

		for (int i = 0; i < centre.length; i++) {
			if (count[i] != 0) {
				for (int j = 0; j < centre[i].length; j++) {
					centre[i][j] /= count[i];
				}
			}
		}

		double result = 0;
		for (int i = 0; i < centre.length; i++) {
			result += distance2(centre[i], ancienCentre[i]);
		}

		return result;
	}

	public static int[] epoque(double X[][], double centre[][], int n) {
		int assignement[] = null;
		double score = Double.MAX_VALUE;

		for (int i = 0; i < n && score != 0; i++) {
			assignement = assigner(X, centre);
			score = deplct(X, centre, assignement);
		}

		return assignement;
	}
}
