import java.awt.Color;
import java.io.IOException;

public class Engine {

	LoadSavePNG m_image;

	public Engine() throws IOException {
		m_image = new LoadSavePNG("./", "mms.png");
	}

	public void run() throws IOException {
		test();
	}

	private void test() throws IOException {
		Color c = m_image.getPixel(0, 0);
		System.out.println("RGB = " + c.getRed() + " " + c.getGreen() + " " + c.getBlue());

		double[] pix = new double[3];
		pix[0] = (double) c.getRed() / 255.0;
		pix[1] = (double) c.getGreen() / 255.0;
		pix[2] = (double) c.getBlue() / 255.0;
		System.out.println("RGB normalisé= " + pix[0] + " " + pix[1] + " " + pix[2]);

		Color[] tabColor = m_image.getImage();

		/** inversion des couleurs **/
		for (int i = 0; i < tabColor.length; i++)
			tabColor[i] = new Color(255 - tabColor[i].getRed(), 255 - tabColor[i].getGreen(),
					255 - tabColor[i].getBlue());

		LoadSavePNG.save(tabColor, "./", "test.png", m_image.getWidth(), m_image.getHeight());
	}
}
