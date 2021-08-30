package com.yu212;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
    public static void main(String[] args) throws IOException {
        String id = "stone";
        int kernelSize = 5;
        Image generated = new Main(loadImage(id), 64, 64, kernelSize).synthesize();
        generated.save(id + "_" + kernelSize);
    }

    public static Image loadImage(String id) throws IOException {
        Path path = Path.of("src/main/resources/block").resolve(id + ".png");
        return new Image(ImageIO.read(path.toFile()));
    }

    private static final ThreadLocalRandom rand = ThreadLocalRandom.current();
    private int remaining;
    private final int kernelSize;
    private final double errorThreshold;
    private final Color[][] sample;
    private final Color[][] window;
    private final boolean[][] mask;
    private final int windowWidth;
    private final int windowHeight;
    private final int sampleWidth;
    private final int sampleHeight;

    public Main(Image input, int windowWidth, int windowHeight, int kernelSize) {
        this.sample = input.getColors();
        this.sampleWidth = input.width;
        this.sampleHeight = input.height;
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        this.kernelSize = kernelSize;
        this.errorThreshold = 0.1;
        this.window = new Color[windowHeight][windowWidth];
        this.mask = new boolean[windowHeight][windowWidth];
        this.remaining = windowWidth * windowHeight - 9;
        int sw = input.width;
        int sh = input.height;
        int seedX = rand.nextInt(sw - 2);
        int seedY = rand.nextInt(sh - 2);
        for (Point d : Point.iterate(3, 3)) {
            Point point = d.add(windowWidth / 2 - 1, windowHeight / 2 - 1);
            window[point.y][point.x] = sample[seedY + d.y][seedX + d.x];
            mask[point.y][point.x] = true;
        }
    }

    public Image synthesize() {
        while (remaining > 0) {
            for (Point c : getNeighboringPixelIndices()) {
                double[][] ssd = normalizedSSD(c);
                List<Point> indices = getCandidateIndices(ssd);
                Point selected = indices.get(rand.nextInt(indices.size()));
                selected = selected.add(kernelSize / 2, kernelSize / 2);
                window[c.y][c.x] = sample[selected.y][selected.x];
                mask[c.y][c.x] = true;
                remaining--;
            }
        }
        return new Image(window);
    }

    private List<Point> getCandidateIndices(double[][] ssd) {
        int ssdWidth = sampleWidth - kernelSize + 1;
        int ssdHeight = sampleHeight - kernelSize + 1;
        double minSSD = ssd[0][0];
        for (Point point : Point.iterate(ssdWidth, ssdHeight)) {
            minSSD = Math.min(minSSD, ssd[point.y][point.x]);
        }
        double minThreshold = minSSD * (1 + errorThreshold);
        List<Point> indices = new ArrayList<>();
        for (Point point : Point.iterate(ssdWidth, ssdHeight)) {
            if (ssd[point.y][point.x] <= minThreshold) {
                indices.add(point);
            }
        }
        return indices;
    }

    private double[][] normalizedSSD(Point c) {
        int pad = kernelSize / 2;
        int ssdWidth = sampleWidth - kernelSize + 1;
        int ssdHeight = sampleHeight - kernelSize + 1;
        double[][] ssd = new double[ssdHeight][ssdWidth];
        double[][] allMask = gaussianMask(kernelSize, kernelSize, kernelSize / 6.4);
        double totalWeight = 0;
        for (Point b : Point.iterate(kernelSize, kernelSize)) {
            Point d = b.add(c).add(-pad, -pad);
            if (!d.isInner(windowWidth, windowHeight)) {
                allMask[b.y][b.x] = 0;
                continue;
            }
            if (!mask[d.y][d.x]) {
                allMask[b.y][b.x] = 0;
            }
            totalWeight += allMask[b.y][b.x];
        }
        for (Point a : Point.iterate(ssdWidth, ssdHeight)) {
            for (Point b : Point.iterate(kernelSize, kernelSize)) {
                Point d = b.add(c).add(-pad, -pad);
                if (!d.isInner(windowWidth, windowHeight)) {
                    continue;
                }
                if (window[d.y][d.x] == null) {
                    continue;
                }
                double diff = window[d.y][d.x].difference(sample[a.y + b.y][a.x + b.x]);
                ssd[a.y][a.x] += diff * allMask[b.y][b.x] / totalWeight;
            }
        }
        return ssd;
    }

    private double[][] gaussianMask(int width, int height, double sigma) {
        double s = 2 * sigma * sigma;
        double sum = 0;
        double[][] gaussiMask = new double[height][width];
        for (Point point : Point.iterate(width, height)) {
            int x = point.x - width / 2;
            int y = point.y - height / 2;
            double r = x * x + y * y;
            gaussiMask[point.y][point.x] = Math.exp(-r / s) / (Math.PI * s);
            sum += gaussiMask[point.y][point.x];
        }
        for (Point point : Point.iterate(width, height)) {
            gaussiMask[point.y][point.x] /= sum;
        }
        return gaussiMask;
    }

    private List<Point> getNeighboringPixelIndices() {
        Set<Point> neighbors = new HashSet<>();
        Map<Point, Integer> neighborCount = new HashMap<>();
        for (Point point : Point.iterate(windowWidth, windowHeight)) {
            if (!mask[point.y][point.x]) {
                continue;
            }
            for (Point neighbor : point.neighborPoints(8)) {
                if (!neighbor.isInner(windowWidth, windowHeight)) {
                    continue;
                }
                if (!mask[neighbor.y][neighbor.x]) {
                    neighborCount.merge(neighbor, 1, Integer::sum);
                    neighbors.add(neighbor);
                }
            }
        }
        List<Point> list = new ArrayList<>(neighbors);
        Collections.shuffle(list);
        list.sort(Comparator.<Point>comparingInt(neighborCount::get).reversed());
        return list;
    }
}
