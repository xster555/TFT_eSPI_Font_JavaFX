package cn.xster.kernel.processing;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.Arrays;

public class PFont{
    protected int glyphCount;
    protected Glyph[] glyphs;
    protected String name;
    protected String psname;
    protected int size;
    protected int density;
    protected boolean smooth;
    protected int ascent;
    protected int descent;
    protected int[] ascii;
    protected boolean lazy;
    protected Font font;
    protected boolean stream;
    protected static String systemFontName;
    protected static Font[] fonts;
    protected BufferedImage lazyImage;
    protected Graphics2D lazyGraphics;
    protected FontMetrics lazyMetrics;
    protected int[] lazySamples;
    private final boolean DEBUG_P4_0278;
    static final char[] EXTRA_CHARS = new char[]{'\u0080', '\u0081', '\u0082', '\u0083', '\u0084', '\u0085', '\u0086', '\u0087', '\u0088', '\u0089', '\u008a', '\u008b', '\u008c', '\u008d', '\u008e', '\u008f', '\u0090', '\u0091', '\u0092', '\u0093', '\u0094', '\u0095', '\u0096', '\u0097', '\u0098', '\u0099', '\u009a', '\u009b', '\u009c', '\u009d', '\u009e', '\u009f', ' ', '¡', '¢', '£', '¤', '¥', '¦', '§', '¨', '©', 'ª', '«', '¬', '\u00ad', '®', '¯', '°', '±', '´', 'µ', '¶', '·', '¸', 'º', '»', '¿', 'À', 'Á', 'Â', 'Ã', 'Ä', 'Å', 'Æ', 'Ç', 'È', 'É', 'Ê', 'Ë', 'Ì', 'Í', 'Î', 'Ï', 'Ñ', 'Ò', 'Ó', 'Ô', 'Õ', 'Ö', '×', 'Ø', 'Ù', 'Ú', 'Û', 'Ü', 'Ý', 'ß', 'à', 'á', 'â', 'ã', 'ä', 'å', 'æ', 'ç', 'è', 'é', 'ê', 'ë', 'ì', 'í', 'î', 'ï', 'ñ', 'ò', 'ó', 'ô', 'õ', 'ö', '÷', 'ø', 'ù', 'ú', 'û', 'ü', 'ý', 'ÿ', 'Ă', 'ă', 'Ą', 'ą', 'Ć', 'ć', 'Č', 'č', 'Ď', 'ď', 'Đ', 'đ', 'Ę', 'ę', 'Ě', 'ě', 'ı', 'Ĺ', 'ĺ', 'Ľ', 'ľ', 'Ł', 'ł', 'Ń', 'ń', 'Ň', 'ň', 'Ő', 'ő', 'Œ', 'œ', 'Ŕ', 'ŕ', 'Ř', 'ř', 'Ś', 'ś', 'Ş', 'ş', 'Š', 'š', 'Ţ', 'ţ', 'Ť', 'ť', 'Ů', 'ů', 'Ű', 'ű', 'Ÿ', 'Ź', 'ź', 'Ż', 'ż', 'Ž', 'ž', 'ƒ', 'ˆ', 'ˇ', '˘', '˙', '˚', '˛', '˜', '˝', 'Ω', 'π', '–', '—', '‘', '’', '‚', '“', '”', '„', '†', '‡', '•', '…', '‰', '‹', '›', '⁄', '€', '™', '∂', '∆', '∏', '∑', '√', '∞', '∫', '≈', '≠', '≤', '≥', '◊', '\uf8ff', 'ﬁ', 'ﬂ'};
    public static char[] CHARSET;

    public PFont(Font font, boolean smooth, char[] charset) {
        this.density = 1;
        this.DEBUG_P4_0278 = false;
        this.font = font;
        this.smooth = smooth;
        this.name = font.getName();
        this.psname = font.getPSName();
        this.size = font.getSize();
        int initialCount = 10;
        this.glyphs = new Glyph[initialCount];
        this.ascii = new int[128];
        Arrays.fill(this.ascii, -1);
        int mbox3 = this.size * 3;
        this.lazyImage = new BufferedImage(mbox3, mbox3, 1);
        this.lazyGraphics = (Graphics2D)this.lazyImage.getGraphics();
        this.lazyGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, smooth ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
        this.lazyGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, smooth ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        this.lazyGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, smooth ? RenderingHints.VALUE_INTERPOLATION_BICUBIC : RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        this.lazyGraphics.setFont(font);
        this.lazyMetrics = this.lazyGraphics.getFontMetrics();
        this.lazySamples = new int[mbox3 * mbox3];
        if (charset == null) {
            this.lazy = true;
        } else {
            char[] sortedCharset = Arrays.copyOf(charset, charset.length);
            Arrays.sort(sortedCharset);
            this.glyphs = new Glyph[sortedCharset.length];
            this.glyphCount = 0;
            char[] var7 = sortedCharset;
            int var8 = sortedCharset.length;

            for(int var9 = 0; var9 < var8; ++var9) {
                char c = var7[var9];
                if (font.canDisplay(c)) {
                    Glyph glyf = new Glyph(c);
                    if (glyf.value < 128) {
                        this.ascii[glyf.value] = this.glyphCount;
                    }

                    glyf.index = this.glyphCount;
                    this.glyphs[this.glyphCount++] = glyf;
                }
            }

            if (this.glyphCount != sortedCharset.length) {
                this.glyphs = (Glyph[])this.subset(this.glyphs, 0, this.glyphCount);
            }
        }

        if (this.ascent == 0) {
            if (font.canDisplay('d')) {
                new Glyph('d');
            } else {
                this.ascent = this.lazyMetrics.getAscent();
            }
        }

        if (this.descent == 0) {
            if (font.canDisplay('p')) {
                new Glyph('p');
            } else {
                this.descent = this.lazyMetrics.getDescent();
            }
        }

    }

    public Object subset(Object list, int start, int count) {
        Class<?> type = list.getClass().getComponentType();
        Object outgoing = Array.newInstance(type, count);
        System.arraycopy(list, start, outgoing, 0, count);
        return outgoing;
    }

    public PFont(Font font, boolean smooth, char[] charset, boolean stream, int density) {
        this(font, smooth, charset);
        this.stream = stream;
        this.density = density;
    }

    public void save(OutputStream output) throws IOException {
        DataOutputStream os = new DataOutputStream(output);
        os.writeInt(this.glyphCount);
        if (this.name == null || this.psname == null) {
            this.name = "";
            this.psname = "";
        }

        os.writeInt(11);
        os.writeInt(this.size);
        os.writeInt(0);
        os.writeInt(this.ascent);
        os.writeInt(this.descent);

        int i;
        for(i = 0; i < this.glyphCount; ++i) {
            this.glyphs[i].writeHeader(os);
        }

        for(i = 0; i < this.glyphCount; ++i) {
            this.glyphs[i].writeBitmap(os);
        }

        os.writeUTF(this.name);
        os.writeUTF(this.psname);
        os.writeBoolean(this.smooth);
        os.flush();
    }

    protected void addGlyph(char c) {
        Glyph glyph = new Glyph(c);
        if (this.glyphCount == this.glyphs.length) {
            this.glyphs = (Glyph[])this.expand(this.glyphs);
        }

        if (this.glyphCount == 0) {
            glyph.index = 0;
            this.glyphs[this.glyphCount] = glyph;
            if (glyph.value < 128) {
                this.ascii[glyph.value] = 0;
            }
        } else if (this.glyphs[this.glyphCount - 1].value < glyph.value) {
            this.glyphs[this.glyphCount] = glyph;
            if (glyph.value < 128) {
                this.ascii[glyph.value] = this.glyphCount;
            }
        } else {
            for(int i = 0; i < this.glyphCount; ++i) {
                if (this.glyphs[i].value > c) {
                    for(int j = this.glyphCount; j > i; --j) {
                        this.glyphs[j] = this.glyphs[j - 1];
                        if (this.glyphs[j].value < 128) {
                            this.ascii[this.glyphs[j].value] = j;
                        }
                    }

                    glyph.index = i;
                    this.glyphs[i] = glyph;
                    if (c < 128) {
                        this.ascii[c] = i;
                    }
                    break;
                }
            }
        }

        ++this.glyphCount;
    }

    public Object expand(Object array) {
        int len = Array.getLength(array);
        return expand(array, len > 0 ? len << 1 : 1);
    }

    public Object expand(Object list, int newSize) {
        Class<?> type = list.getClass().getComponentType();
        Object temp = Array.newInstance(type, newSize);
        System.arraycopy(list, 0, temp, 0, Math.min(Array.getLength(list), newSize));
        return temp;
    }

    protected int index(char c) {
        if (this.lazy) {
            int index = this.indexActual(c);
            if (index != -1) {
                return index;
            } else if (this.font != null && this.font.canDisplay(c)) {
                this.addGlyph(c);
                return this.indexActual(c);
            } else {
                return -1;
            }
        } else {
            return this.indexActual(c);
        }
    }

    protected int indexActual(char c) {
        if (this.glyphCount == 0) {
            return -1;
        } else {
            return c < 128 ? this.ascii[c] : this.indexHunt(c, 0, this.glyphCount - 1);
        }
    }

    protected int indexHunt(int c, int start, int stop) {
        int pivot = (start + stop) / 2;
        if (c == this.glyphs[pivot].value) {
            return pivot;
        } else if (start >= stop) {
            return -1;
        } else {
            return c < this.glyphs[pivot].value ? this.indexHunt(c, start, pivot - 1) : this.indexHunt(c, pivot + 1, stop);
        }
    }

    public float width(char c) {
        if (c == ' ') {
            return this.width('i');
        } else {
            int cc = this.index(c);
            return cc == -1 ? 0.0F : (float)this.glyphs[cc].setWidth / (float)this.size;
        }
    }

    public static String[] list() {
        loadFonts();
        String[] list = new String[fonts.length];

        for(int i = 0; i < list.length; ++i) {
            list[i] = fonts[i].getName();
        }

        return list;
    }

    public static void loadFonts() {
        if (fonts == null) {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            fonts = ge.getAllFonts();
//            if (PApplet.platform == 2) {
//                fontDifferent = new HashMap();
//                Font[] var1 = fonts;
//                int var2 = var1.length;
//
//                for(int var3 = 0; var3 < var2; ++var3) {
//                    Font font = var1[var3];
//                    fontDifferent.put(font.getName(), font);
//                }
//            }
        }

    }

    public static Font findFont(String name) {
        Font font;
//        if (PApplet.platform == 2) {
//            loadFonts();
//            font = (Font)fontDifferent.get(name);
//            if (font != null) {
//                return font;
//            }
//        }

        font = new Font(name, 0, 1);
        if (systemFontName == null) {
            systemFontName = (new Font("", 0, 1)).getFontName();
        }

        if (!name.equals(systemFontName) && font.getFontName().equals(systemFontName)) {
//            PGraphics.showWarning("\"" + name + "\" is not available, so another font will be used. Use PFont.list() to show available fonts.");
        }

        return font;
    }

    static {
        CHARSET = new char[94 + EXTRA_CHARS.length];
        int index = 0;

        for(int i = 33; i <= 126; ++i) {
            CHARSET[index++] = (char)i;
        }

        char[] var5 = EXTRA_CHARS;
        int var2 = var5.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            char extraChar = var5[var3];
            CHARSET[index++] = extraChar;
        }

    }

    public class Glyph {
        public PImage image;
        public int value;
        public int height;
        public int width;
        public int index;
        public int setWidth;
        public int topExtent;
        public int leftExtent;

        protected void writeHeader(DataOutputStream os) throws IOException {
            os.writeInt(this.value);
            os.writeInt(this.height);
            os.writeInt(this.width);
            os.writeInt(this.setWidth);
            os.writeInt(this.topExtent);
            os.writeInt(this.leftExtent);
            os.writeInt(0);
        }

        protected void writeBitmap(DataOutputStream os) throws IOException {
            int[] pixels = this.image.pixels;

            for(int y = 0; y < this.height; ++y) {
                for(int x = 0; x < this.width; ++x) {
                    os.write(pixels[y * this.width + x] & 255);
                }
            }
        }

        protected Glyph(char c) {
            int mbox3 = PFont.this.size * 3;
            PFont.this.lazyGraphics.setColor(Color.white);
            PFont.this.lazyGraphics.fillRect(0, 0, mbox3, mbox3);
            PFont.this.lazyGraphics.setColor(Color.black);
            PFont.this.lazyGraphics.drawString(String.valueOf(c), PFont.this.size, PFont.this.size * 2);
            WritableRaster raster = PFont.this.lazyImage.getRaster();
            raster.getDataElements(0, 0, mbox3, mbox3, PFont.this.lazySamples);
            int minX = 1000;
            int maxX = 0;
            int minY = 1000;
            int maxY = 0;
            boolean pixelFound = false;

            int x;
            int xx;
            for(int y = 0; y < mbox3; ++y) {
                for(x = 0; x < mbox3; ++x) {
                    xx = PFont.this.lazySamples[y * mbox3 + x] & 255;
                    if (xx != 255) {
                        if (x < minX) {
                            minX = x;
                        }

                        if (y < minY) {
                            minY = y;
                        }

                        if (x > maxX) {
                            maxX = x;
                        }

                        if (y > maxY) {
                            maxY = y;
                        }

                        pixelFound = true;
                    }
                }
            }

            if (!pixelFound) {
                minY = 0;
                minX = 0;
                maxY = 0;
                maxX = 0;
            }

            this.value = c;
            this.height = maxY - minY + 1;
            this.width = maxX - minX + 1;
            this.setWidth = PFont.this.lazyMetrics.charWidth(c);
            this.topExtent = PFont.this.size * 2 - minY;
            this.leftExtent = minX - PFont.this.size;
            this.image = new PImage(this.width, this.height, 4);
            int[] pixels = this.image.pixels;

            for(x = minY; x <= maxY; ++x) {
                for(xx = minX; xx <= maxX; ++xx) {
                    int val = 255 - (PFont.this.lazySamples[x * mbox3 + xx] & 255);
                    int pindex = (x - minY) * this.width + (xx - minX);
                    pixels[pindex] = val;
                }
            }

            if (this.value == 100 && PFont.this.ascent == 0) {
                PFont.this.ascent = this.topExtent;
            }

            if (this.value == 112 && PFont.this.descent == 0) {
                PFont.this.descent = -this.topExtent + this.height;
            }

        }
    }
}
