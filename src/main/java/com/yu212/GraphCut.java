package com.yu212;

import java.util.Arrays;

public class GraphCut {
    private final int inputWidth;
    private final int inputHeight;
    private final Color[][] input;
    private final int overlap;

    public GraphCut(Image input, int overlap) {
        this.input = input.getColors();
        this.inputWidth = input.width;
        this.inputHeight = input.height;
        this.overlap = overlap;
    }

    public Image compute() {
        Color[][] temp = computeHorizontal(input, inputWidth, inputHeight);
        Color[][] computed = computeVertical(temp, inputWidth - overlap * 2, inputHeight);
        return new Image(computed);
    }

    public Color[][] computeHorizontal(Color[][] image, int width, int height) {
        int outWidth = width - overlap * 2;
        double[][] dp = new double[height + 1][overlap * 2];
        int[][] prev = new int[height + 1][overlap * 2];
        for (int i = 0; i < height; i++) {
            Arrays.fill(dp[i + 1], Double.MAX_VALUE);
        }
        int minGoal = -1;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < overlap * 2; j++) {
                double prevMin = dp[i][j];
                int prevOffset = 0;
                if (j > 0 && prevMin > dp[i][j - 1]) {
                    prevOffset = -1;
                    prevMin = dp[i][j - 1];
                }
                if (j + 1 < overlap * 2 && prevMin > dp[i][j + 1]) {
                    prevOffset = 1;
                    prevMin = dp[i][j + 1];
                }
                Color color1 = image[i][outWidth + j];
                Color color2 = image[i][j];
                double diff = color1.difference(color2);
                if (dp[i + 1][j] > prevMin + diff) {
                    dp[i + 1][j] = prevMin + diff;
                    prev[i + 1][j] = prevOffset;
                    if (i + 1 == height && (minGoal == -1 || dp[height][minGoal] > prevMin + diff)) {
                        minGoal = j;
                    }
                }
            }
        }
        int[] sep = new int[height];
        for (int i = height - 1; i >= 0; i--) {
            sep[i] = minGoal;
            minGoal += prev[i + 1][minGoal];
        }
        Color[][] output = new Color[height][outWidth];
        for (int i = 0; i < height; i++) {
            System.arraycopy(image[i], overlap, output[i], 0, outWidth);
            if (sep[i] < overlap) {
                System.arraycopy(image[i], sep[i], output[i], outWidth - overlap + sep[i], overlap - sep[i]);
            } else {
                System.arraycopy(image[i], outWidth + overlap, output[i], 0, sep[i] - overlap);
            }
        }
        return output;
    }

    public Color[][] computeVertical(Color[][] image, int width, int height) {
        int outHeight = height - overlap * 2;
        double[][] dp = new double[width + 1][overlap * 2];
        int[][] prev = new int[width + 1][overlap * 2];
        for (int i = 0; i < width; i++) {
            Arrays.fill(dp[i + 1], Double.MAX_VALUE);
        }
        int minGoal = -1;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < overlap * 2; j++) {
                double prevMin = dp[i][j];
                int prevOffset = 0;
                if (j > 0 && prevMin > dp[i][j - 1]) {
                    prevOffset = -1;
                    prevMin = dp[i][j - 1];
                }
                if (j + 1 < overlap * 2 && prevMin > dp[i][j + 1]) {
                    prevOffset = 1;
                    prevMin = dp[i][j + 1];
                }
                Color color1 = image[outHeight + j][i];
                Color color2 = image[j][i];
                double diff = color1.difference(color2);
                if (dp[i + 1][j] > prevMin + diff) {
                    dp[i + 1][j] = prevMin + diff;
                    prev[i + 1][j] = prevOffset;
                    if (i + 1 == width && (minGoal == -1 || dp[width][minGoal] > prevMin + diff)) {
                        minGoal = j;
                    }
                }
            }
        }
        int[] sep = new int[width];
        for (int i = width - 1; i >= 0; i--) {
            sep[i] = minGoal;
            minGoal += prev[i + 1][minGoal];
        }
        Color[][] output = new Color[outHeight][width];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < outHeight; j++) {
                output[j][i] = input[j + overlap][i];
            }
            for (int j = overlap; j < sep[i]; j++) {
                output[j - overlap][i] = input[outHeight + j][i];
            }
            for (int j = sep[i]; j < overlap; j++) {
                output[outHeight - overlap + j][i] = input[j][i];
            }
        }
        return output;
    }
}
