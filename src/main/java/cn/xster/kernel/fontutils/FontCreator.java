package cn.xster.kernel.fontutils;

import cn.xster.kernel.processing.PFont;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class FontCreator {

    public int pixelDensity = 1;
    private String[] fontList;

    public FontCreator(){
        fontList = PFont.list();
    }

    public String[] getFontList(){ return fontList; }

    public void getHFont(int fontNumber, int fontSize, int[] unicodeBlocks){
        getHFont(fontNumber, fontSize, "", unicodeBlocks, "");
    }

    public void getHFont(int fontNumber, int fontSize, int[] unicodeBlocks, String outFileName){
        getHFont(fontNumber, fontSize, "", unicodeBlocks, outFileName);
    }
    public void getHFont(int fontNumber, int fontSize, String specificChar){
        int[] unicodeBlocks ={};
        getHFont(fontNumber, fontSize, specificChar, unicodeBlocks, "");
    }

    public void getHFont(int fontNumber, int fontSize, String specificChar, String outFileName){
        int[] unicodeBlocks ={};
        getHFont(fontNumber, fontSize, specificChar, unicodeBlocks, outFileName);
    }

    public void getHFont(int fontNumber, int fontSize, String specificChar, int[] unicodeBlocks){
        getHFont(fontNumber, fontSize, specificChar, unicodeBlocks, "");
    }
    public void getHFont(int fontNumber, int fontSize, String specificChar, int[] unicodeBlocks, String outFileName){
        // 获取字体名
        String[] fontList = PFont.list();
        String fontName = fontList[fontNumber];

        // 设置保存的文件名
        if (outFileName.isEmpty()){
            outFileName = fontName + Integer.toString(fontSize) + ".h";
        }
        if (!outFileName.endsWith(".h")){
            outFileName = outFileName + ".h";
        }

        PFont font;
        font = handleData(fontName, fontSize, specificChar, unicodeBlocks);

        System.out.println("Created h font");

        // creating file
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outFileName));
            if (font != null) {
                font.save(output);
                StringBuilder content = new StringBuilder("#include <pgmspace.h>\n" +
                    "const uint8_t my_font[] PROGMEM = {\n");
                int count = 1;
                for(Byte b : output.toByteArray()){
                    String temp = Integer.toHexString(Byte.toUnsignedInt(b));
                    if (temp.length() == 1) temp = "0" + temp;
                    content.append("0x").append(temp.toUpperCase()).append(", ");

                    if(count % 10 == 0) content.append("\n");

                    count++;
                }
                content.deleteCharAt(content.length() - 1);
                content.deleteCharAt(content.length() - 1);
                content.append("};");
                bufferedWriter.write(content.toString());
                output.close();
                bufferedWriter.close();
                System.out.println("OK!");
            }
        }
        catch(IOException e) {
            System.out.println("Doh! Failed to create the file");
        }
    }

    public void getVlwFont(int fontNumber, int fontSize, int[] unicodeBlocks){
        getVlwFont(fontNumber, fontSize, "", unicodeBlocks, "");
    }

    public void getVlwFont(int fontNumber, int fontSize, int[] unicodeBlocks, String outFileName){
        getVlwFont(fontNumber, fontSize, "", unicodeBlocks, outFileName);
    }

    public void getVlwFont(int fontNumber, int fontSize, String specificChar){
        int[] unicodeBlocks = {};
        getVlwFont(fontNumber, fontSize, specificChar, unicodeBlocks, "");
    }
    public void getVlwFont(int fontNumber, int fontSize, String specificChar, String outFileName){
        int[] unicodeBlocks = {};
        getVlwFont(fontNumber, fontSize, specificChar, unicodeBlocks, outFileName);
    }

    public void getVlwFont(int fontNumber, int fontSize, String specificChar, int[] unicodeBlocks){
        getVlwFont(fontNumber, fontSize, specificChar, unicodeBlocks, "");
    }

    public void getVlwFont(int fontNumber, int fontSize, String specificChar, int[] unicodeBlocks, String outFileName){
        // 获取字体名
        String[] fontList = PFont.list();
        String fontName = fontList[fontNumber];

        // 设置保存的文件名
        if (outFileName.isEmpty()){
            outFileName = fontName + Integer.toString(fontSize) + ".vlw";
        }
        if (!outFileName.endsWith(".vlw")){
            outFileName = outFileName + ".vlw";
        }

        PFont font;
        font = handleData(fontName, fontSize, specificChar, unicodeBlocks);

        System.out.println("Created vlw font");

        // creating file
        try {
            OutputStream output = this.createOutput(outFileName);
            if (output != null && font != null) {
                font.save(output);
                output.close();
                System.out.println("OK!");
            }
        }
        catch(IOException e) {
            System.out.println("Doh! Failed to create the file");
        }
    }

    private PFont handleData(String fontName, int fontSize, String specificChar, int[] unicodeBlocks){
        // 单个字符去重并获得unicode值
        String scrStr = removeDuplicates(specificChar);
        List<Integer> specificUnicodes = toUnicode(scrStr);

        // 数据处理
        int firstUnicode = 0;
        int lastUnicode  = 0;
        char[]   charset;
        int  index = 0, count = 0;

        // 处理字符块
        int blockCount = unicodeBlocks.length;
        for (int i = 0; i < blockCount; i+=2) {
            firstUnicode = unicodeBlocks[i];
            lastUnicode  = unicodeBlocks[i+1];
            if (lastUnicode < firstUnicode) {
                System.err.println("ERROR: Bad Unicode range secified, last < first!");
                continue;
            }
            // calculate the number of characters
            count += (lastUnicode - firstUnicode + 1);
        }
        // 添加单个字符
        count += specificUnicodes.size();
        // 没有要转换的字符
        if (count == 0) {
            System.err.println("ERROR: No Unicode range or specific codes have been defined!");
            return null;
        }

        // 将字符块范围内的字符添加到列表
        charset = new char[count];
        for (int i = 0; i < blockCount; i+=2) {
            firstUnicode = unicodeBlocks[i];
            lastUnicode  =  unicodeBlocks[i+1];
            // loading the range specified
            for (int code = firstUnicode; code <= lastUnicode; code++) {
                charset[index] = Character.toChars(code)[0];
                index++;
            }
        }

        // loading the specific point codes
        for (int i = 0; i < specificUnicodes.size(); i++) {
            charset[index] = Character.toChars(specificUnicodes.get(i))[0];
            index++;
        }

        // Make font smooth (anti-aliased)
        boolean smooth = true;

        return this.createFont(fontName, fontSize, smooth, charset);
    }

    private List<Integer> toUnicode(String s)
    {
        List<Integer> result = new ArrayList<>();
        for(int i=0;i<s.length();i++)
        {
            result.add((int) s.charAt(i));
        }
        return result;
    }

    private String removeDuplicates(String str) {
        Set<Character> set = new HashSet<>();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (!set.contains(c)) {
                set.add(c);
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private PFont createFont(String name, float size, boolean smooth, char[] charset) {
        String lowerName = name.toLowerCase();

        try {
            InputStream stream = null;
            Font baseFont;
            if (!lowerName.endsWith(".otf") && !lowerName.endsWith(".ttf")) {
                baseFont = PFont.findFont(name);
            } else {
                stream = this.createInput(name);
                if (stream == null) {
                    System.err.println("The font \"" + name + "\" is missing or inaccessible, make sure the URL is valid or that the file has been added to your sketch and is readable.");
                    return null;
                }

                baseFont = Font.createFont(0, this.createInput(name));
            }

            return this.createFont(baseFont, size, smooth, charset, stream != null);
        } catch (Exception var8) {
            System.err.println("Problem with createFont(\"" + name + "\")");
            var8.printStackTrace();
            return null;
        }
    }

    private PFont createFont(Font baseFont, float size, boolean smooth, char[] charset, boolean stream) {
        return new PFont(baseFont.deriveFont(size * (float)this.pixelDensity), smooth, charset, stream, this.pixelDensity);
    }


    private InputStream createInput(String filename) {
        InputStream input = this.createInputRaw(filename);
        if (input != null) {
            String lower = filename.toLowerCase();
            if (!lower.endsWith(".gz") && !lower.endsWith(".svgz")) {
                return new BufferedInputStream(input);
            }

            try {
                return new BufferedInputStream(new GZIPInputStream(input));
            } catch (IOException var5) {
                var5.printStackTrace();
            }
        }

        return null;
    }

    private InputStream createInputRaw(String filename) {
        if (filename == null) {
            return null;
        }else if (filename.length() == 0) {
            return null;
        } else {
            String filenameShort;

            String filePath;
            try {
                File file = new File(filename);

                if (file.isDirectory()) {
                    return null;
                } else {
                    if (file.exists()) {
                        try {
                            filePath = file.getCanonicalPath();
                            String filenameActual = (new File(filePath)).getName();
                            filenameShort = (new File(filename)).getName();
                            if (!filenameActual.equals(filenameShort)) {
                                throw new RuntimeException("This file is named " + filenameActual + " not " + filename + ". Rename the file or change your code.");
                            }
                        } catch (IOException var7) {
                        }
                    }

                    return new FileInputStream(file);
                }
            } catch (SecurityException | IOException var16) {
                return null;
            }
        }
    }

    private OutputStream createOutput(String filename) {
        try {
            File file = new File(filename);
            OutputStream output = new FileOutputStream(file);
            return file.getName().toLowerCase().endsWith(".gz") ? new BufferedOutputStream(new GZIPOutputStream(output)) : new BufferedOutputStream(output);
        } catch (IOException var2) {
            var2.printStackTrace();
            return null;
        }
    }
}
