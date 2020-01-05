import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.File;
import java.io.IOException;

/**
 * petite class pour abstraire la lecture et la sauvegarde des images
 */
public class LoadSavePNG {

	private final String m_path;
	static private BufferedImage m_bi;
	private final int m_width;
	private final int m_height;

	public LoadSavePNG(final String path, final String imageName) throws IOException {
		m_path = path + imageName;
		m_bi = ImageIO.read(new File(m_path));
		m_width = m_bi.getWidth();
		m_height = m_bi.getHeight();
	}

	public Color getPixel(final int x, final int y) {
		return new Color(m_bi.getRGB(x, y));
	}

	public Color[] getImage() {
		final int[] im_pixels = m_bi.getRGB(0, 0, m_width, m_height, null, 0, m_width);

		final Color[] result = new Color[im_pixels.length];
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

	public static void save(final Color image[], final String path, final String name, final int width,
			final int height) throws IOException {
		final BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++)
				result.setRGB(j, i, image[i * width + j].getRGB());
		}

		System.out.println("saving: " + path + name);
		ImageIO.write(result, "PNG", new File(path + name));
	}
}
