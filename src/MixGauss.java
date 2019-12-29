
public class MixGauss {

	private static int[] assigner(double[][] X, double[][] centres) {
		return null;
	}

	private static double deplct(double X[][], double centre[][], int assignement[]) {
		return 0;
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