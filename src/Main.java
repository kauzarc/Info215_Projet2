import java.awt.Color;
import java.io.IOException;

public class Main {

	public static void main(String[] args) throws IOException {
		String path = "./";
		String imageName = "mms.png";

		LoadSavePNG image = new LoadSavePNG(path, imageName);

		Color c = image.getPixel(0, 0);
		System.out.println("RGB = " + c.getRed() + " " + c.getGreen() + " " + c.getBlue());

		double[] pix = new double[3];
		pix[0] = (double) c.getRed() / 255.0;
		pix[1] = (double) c.getGreen() / 255.0;
		pix[2] = (double) c.getBlue() / 255.0;
		System.out.println("RGB normalisé= " + pix[0] + " " + pix[1] + " " + pix[2]);

		Color[] tabColor = image.getImage();

		/** inversion des couleurs **/
		for (int i = 0; i < tabColor.length; i++)
			tabColor[i] = new Color(255 - tabColor[i].getRed(), 255 - tabColor[i].getGreen(),
					255 - tabColor[i].getBlue());

		LoadSavePNG.save(tabColor, path, "test.png", image.getWidth(), image.getHeight());
	}
}
