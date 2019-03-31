package test;

import com.github.jaiimageio.impl.plugins.tiff.TIFFImageReader;
import com.github.jaiimageio.impl.plugins.tiff.TIFFImageReaderSpi;
import com.github.jaiimageio.impl.plugins.tiff.TIFFImageWriter;
import com.github.jaiimageio.impl.plugins.tiff.TIFFImageWriterSpi;

import javax.imageio.IIOImage;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class Change_Test {
    /**
     * 合并多个Tiff文件成一个多页Tiff文件
     *
     * @param fTiff
     *            目标tiff文件
     * @param tifDir
     *            源tiff文件路径,tiff文件必须以0001.tif,0002.tif...置于此路径下
     * @return true表示成功,false表示失败
     */
    static public boolean mergeTiff(File fTiff, File tifDir) {

        boolean bres = true;
        //tiff格式的图片读取器;
        TIFFImageReader tiffImageReader = new TIFFImageReader(new TIFFImageReaderSpi());
        FileImageInputStream fis = null;
        //map用于保存数字格式的文件名为key,以保证合并后的顺序正确
        Map<String, BufferedImage> biMap = new HashMap<String, BufferedImage>();
        File[] fs = tifDir.listFiles();
        for(File f: fs) {
            String fileName = f.getName();

            if(!fileName.endsWith(".tif")) {
                continue;
            }


            String key = fileName.replace(".tif","");
            if(isNum(key)){
                try {
                    fis = new FileImageInputStream(f);
                    tiffImageReader.setInput(fis);

                    biMap.put(key,tiffImageReader.read(0));
                }catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    if(tiffImageReader != null) {
                        tiffImageReader.dispose();
                    }
                    if (fis != null) {
                        try {
                            fis.flush();
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        }
        //tiff格式图片输出流
        TIFFImageWriter tiffImageWriter = new TIFFImageWriter(new TIFFImageWriterSpi());

        try {
            //先指定一个文件用于存储输出的数据
            tiffImageWriter.setOutput(new FileImageOutputStream(fTiff));
            //指定第一个tif文件写到指定的文件中
            BufferedImage bufferedImage_0 = biMap.get("0");
            //IIOImage类是用于存储    图片/缩略图/元数据信息    的引用类
            IIOImage iioImage_0 = new IIOImage(bufferedImage_0, null, null);
            //write方法,将给定的IIOImage对象写到文件系统中;
            tiffImageWriter.write(null,iioImage_0,null);
            for (int i = 1; i < biMap.size(); i++) {
                //判断该输出流是否可以插入新图片到文件系统中的
                if(tiffImageWriter.canInsertImage(i)){
                    //根据顺序获取缓冲中的图片;
                    BufferedImage bufferedImage = biMap.get(String.valueOf(i));
                    IIOImage iioImage = new IIOImage(bufferedImage, null, null);
                    //将文件插入到输出的多图片文件中的指定的下标处
                    tiffImageWriter.writeInsert(i, iioImage,null);
                }
            }
            bres = true;
        } catch (IOException e) {
            e.printStackTrace();
            bres = false;
        }finally {
            return bres;
        }

    }


    /**
     * 从一个分页的tiff文件中拆分各页,并从0开始命名每一页
     * @param fTiff 源tiff文件
     * @param decDir
     *            tiff目标路径,目标文件将会以0001.tif,0002.tif...置于此路径下
     * @return true表示成功,false表示失败
     */
    public static boolean makeSingleTif(File fTiff, File decDir) {
        boolean bres = true;
        FileImageInputStream fis = null;
        try {
            //java1.8的ImageIO不支持原文中以"TIFF"名字获取ImageReader,具体原因是
            //ImageReaderSpi中有一个注册器,会先在内存中注册各个名称的读取器,而这个注册器恰好没有TIFF格式的
            //所以需要jai-imageio-1.1.jar提供TIFFImageReader;
            TIFFImageReaderSpi tiffImageReaderSpi = new TIFFImageReaderSpi();
            TIFFImageReader tiffImageReader = new TIFFImageReader(tiffImageReaderSpi);

            fis = new FileImageInputStream(fTiff);
            tiffImageReader.setInput(fis);

            int numPages = tiffImageReader.getNumImages(true);
            for(int i=0; i<numPages; i++) {

                BufferedImage bi = tiffImageReader.read(i);

                File tif = new File(decDir.getPath() + File.separator
                        + String.format(""+i) + ".tif");
//此处我注销了原文中的写Tiff文件的方法其原因是如果采用此方法会导致个别图片拆不出来,所以改用ImageWriter,个人推测是因为原文采用的方式涉及到图片的每个像素
//                bres = createTiff(tif,new RenderedImage[]{bi},dpiData,false);
                //TIFFImageWriter与reader是同样的原理;
                FileImageOutputStream fios = new FileImageOutputStream(tif);
                TIFFImageWriterSpi tiffImageWriterSpi = new TIFFImageWriterSpi();
                TIFFImageWriter tiffImageWriter = new TIFFImageWriter(tiffImageWriterSpi);
                tiffImageWriter.setOutput(fios);

                tiffImageWriter.write(bi);
            }

        } catch (Exception e) {
            e.printStackTrace();
            bres = false;

        }finally {

            if (fis != null) {
                try {
                    fis.flush();
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }


        return bres;
    }


    public static void main(String[] args) {
        boolean num = isNum("123");
        System.out.println(num);
        File fTiff = new File("C:\\Users\\York\\Desktop\\many.tif");
        File tifDir = new File("C:\\Users\\York\\Desktop\\TIFFfile");
        File decDir = new File("C:\\Users\\York\\Desktop\\singlefile");
        System.out.println(mergeTiff(fTiff, tifDir));
        System.out.println(makeSingleTif(fTiff, decDir));
    }

    //如果能转成bigdecimal则表示是数字
    private static boolean isNum(String name){
        try{
            if(!name.contains(".")){
                BigDecimal bigDecimal = new BigDecimal(name);
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            return false;
        }
    }

}
