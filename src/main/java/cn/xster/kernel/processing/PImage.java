package cn.xster.kernel.processing;

import java.util.Arrays;

public class PImage{
    public int format;
    public int[] pixels;
    public int pixelDensity = 1;
    public int pixelWidth;
    public int pixelHeight;
    public int width;
    public int height;

    public PImage(int width, int height, int format) {
        this.init(width, height, format, 1);
    }

    public void init(int width, int height, int format, int factor) {
        this.width = width;
        this.height = height;
        this.format = format;
        this.pixelDensity = factor;
        this.pixelWidth = width * this.pixelDensity;
        this.pixelHeight = height * this.pixelDensity;
        this.pixels = new int[this.pixelWidth * this.pixelHeight];
        if (format == 1) {
            Arrays.fill(this.pixels, -16777216);
        }
    }
}
