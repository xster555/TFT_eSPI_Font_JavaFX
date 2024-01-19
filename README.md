# TFT_eSPI_Font
TFT_eSPI使用的.vlw和.h格式字体生成
## 用法
### 1. 创建对象
```java
FontCreator fontCreator = new FontCreator();
```
### 2.调用方法
可以创建.vlw文件和.h文件
#### 2.1 创建.vlw文件
```java
fontCreator.getVlwFont(fontNumber, fontSize, specificChar, unicodeBlocks, outFileName)
```
参数：

fontNumber（int 必填） - 字体编号（使用系统字体，可以通过对象的 getFontList()获取所有字体）

fontSize（int 必填） - 字体大小（单位：像素）

specificChar（String 可选） - 需要添加到字体库的字符

unicodeBlocks（int[] 可选） - 需要添加的Unicode字符范围，如需要添加字符0-9（0x0030 - 0x0039），和字符A-Z（0x0041 - 0x005A），该数组应为 ***{0x0030, 0x0039, 0x0041, 0x005A}***

outFileName （String 可选）- 输出文件的文件名，默认 **字体+字号.vlw** , 如 **黑体10.vlw**

#### 2.2 创建.h文件
```java
fontCreator.getHFont(fontNumber, fontSize, specificChar, unicodeBlocks, outFileName)
```

文件中默认变量名为my_font，生成后可自行修改


参数：

fontNumber（int 必填） - 字体编号（使用系统字体，可以通过对象的 getFontList()获取所有字体）

fontSize（int 必填） - 字体大小（单位：像素）

specificChar（String 可选） - 需要添加到字体库的字符

unicodeBlocks（int[] 可选） - 需要添加的Unicode字符范围，如需要添加字符0-9（0x0030 - 0x0039），和字符A-Z（0x0041 - 0x005A），该数组应为 ***{0x0030, 0x0039, 0x0041, 0x005A}***

outFileName （String 可选）- 输出文件的文件名，默认 **字体+字号.h** , 如 **黑体10.h**
