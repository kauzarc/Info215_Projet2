import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.File;
import java.io.IOException;

public class LoadSavePNG {

	private String m_path;
	static private BufferedImage m_bi;
	private int m_width;
	private int m_height;

	public LoadSavePNG(String path, String imageName) throws IOException {
		m_path = path + imageName;
		m_bi = ImageIO.read(new File(m_path));
		m_width = m_bi.getWidth();
		m_height = m_bi.getHeight();
	}

	public Color getPixel(int x, int y) {
		return new Color(m_bi.getRGB(x, y));
	}

	public Color[] getImage() {
		int[] im_pixels = m_bi.getRGB(0, 0, m_width, m_height, null, 0, m_width);

		Color[] result = new Color[im_pixels.length];
		for (int i = 0; i < im_pixels.length; i++)
			result[i] = new Color(im_pixels[i]);

		return result;
	}

	public int getWidth() {
		return m_width;
	}

	public int getHeight() {
		return m_height;
	}

	public static void save(Color image[], String path, String name, int width, int height) throws IOException {
		BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++)
				result.setRGB(j, i, image[i * width + j].getRGB());
		}

		System.out.println("saving: " + path + name);
		ImageIO.write(result, "PNG", new File(path + name));
	}
}
