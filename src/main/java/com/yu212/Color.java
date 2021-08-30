package com.yu212;

public class Color {
    int rgb;

    public Color(int rgb) {
        this.rgb = rgb;
    }

    public int getRed() {
        return (rgb >> 16) & 0xFF;
    }

    public int getGreen() {
        return (rgb >> 8) & 0xFF;
    }

    public int getBlue() {
        return rgb & 0xFF;
    }

    public double difference(Color other) {
        double diff = 0;
        diff += (getRed() - other.getRed()) / 255.0 * (getRed() - other.getRed()) / 255.0;
        diff += (getGreen() - other.getGreen()) / 255.0 * (getGreen() - other.getGreen()) / 255.0;
        diff += (getBlue() - other.getBlue()) / 255.0 * (getBlue() - other.getBlue()) / 255.0;
        return diff;
    }
}
