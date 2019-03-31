/*
package test;

import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.codec.TIFFEncodeParam;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class changeTest {

    public static void main(String[] args) throws Exception {
        File file = new File("temp/a.jpg");
        List<String> list = new ArrayList<String>();
        list.add(file.getAbsolutePath());
        list.add(file.getAbsolutePath());
        list.add(file.getAbsolutePath());
        imgToTif(list, "temp2", "a.tif");
    }

    public static void imgToTif(List<String> filesPath, String toPath, String distFileName) {
        if (filesPath != null && filesPath.size() > 0) {
            File[] files = new File[filesPath.size()];
            for (int i = 0; i < filesPath.size(); i++) {
                files[i] = new File(filesPath.get(i));
            }
            if (files.length > 0) {
                try {
                    PlanarImageArrayList<PlanarImage> pages = new ArrayList<PlanarImage>(files.length - 1);
                    FileSeekableStream[] stream = new FileSeekableStream[files.length];
                    for (int i i= 0; i < files.length; i++) {
                        stream[i] = new FileSeekableStream(
                                files[i].getCanonicalPath());
                    }
                    PlanarImage firstPage = JAI.create("stream", stream[0]);
                    for (int i = 1; i < files.length; i++) {
                        PlanarImage page = JAI.create("stream", stream[i]);
                        pages.add(page);

                    }
                    TIFFEncodeParam param = new TIFFEncodeParam();
                    File f = new File(toPath);
                    if (!f.exists()) {
                        f.mkdirs();
                    }
                    OutputStream os = new FileOutputStream(toPath + File.separator + distFileName);
                    ImageEncoder enc = ImageCodec.createImageEncoder("tiff",
                            os, param);
                    param.setExtraImages(pages.iterator());
                    enc.encode(firstPage);
                    for (int i = 0; i < files.length; i++) {
                        stream[i].close();
                    }
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
*/
