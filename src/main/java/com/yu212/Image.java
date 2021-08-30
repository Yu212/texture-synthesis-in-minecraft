package com.yu212;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

public class Image {
    public final int width;
    public final int height;
    private final Color[][] colors;

    public Image(BufferedImage image) {
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.colors = new Color[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                colors[i][j] = new Color(image.getRGB(j, i));
            }
        }
    }

    public Image(Color[][] colors) {
        this.width = colors[0].length;
        this.height = colors.length;
        this.colors = colors;
    }

    public Color getColor(int x, int y) {
        return colors[y][x];
    }

    public Color[][] getColors() {
        return colors;
    }

    public void save(String name) throws IOException {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                image.setRGB(j, i, colors[i][j].rgb);
            }
        }
        ImageIO.write(image, "png", Path.of("src/main/resources").resolve(name + ".png").toFile());
    }

    public void saveForTwitter(String name) throws IOException {
        BufferedImage convertedImage = convertForTwitter();
        ImageIO.write(convertedImage, "png", Path.of("src/main/resources").resolve(name + ".png").toFile());
    }

    private BufferedImage convertForTwitter() {
        BufferedImage image = new BufferedImage(3201, 3200, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                for (int k = 0; k < 2500; k++) {
                    image.setRGB(j * 50 + k / 50, i * 50 + k % 50, colors[i][j].rgb);
                }
            }
        }
        return image;
    }
}
