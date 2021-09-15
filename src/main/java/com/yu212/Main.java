package com.yu212;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException {
        String id = "stone";
        int kernelSize = 3;
        Image generated = new TextureSynthesis(loadImage(id), 80, 80, kernelSize).synthesize();
        Image computed = new GraphCut(generated, 8).compute();
        Image[][] splitted = computed.split(4, 4);
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                splitted[i][j].save(String.format("%s/%d", id, i * 4 + j));
            }
        }
    }

    public static Image loadImage(String id) throws IOException {
        Path path = Path.of("src/main/resources/block").resolve(id + ".png");
        return new Image(ImageIO.read(path.toFile()));
    }
}
