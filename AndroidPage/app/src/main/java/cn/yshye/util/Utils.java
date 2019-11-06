package cn.yshye.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

///
/// 叶院生 2019/11/5
///
public class Utils {
    public static String getStringFromStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream baso = new ByteArrayOutputStream();
        int len = -1;
        byte[] buffer = new byte[1024];
        while ((len = inputStream.read(buffer)) != -1) {
            baso.write(buffer, 0, len);
        }
        inputStream.close();
        byte[] byteArray = baso.toByteArray();
        return new String(byteArray);
    }

    public static Bitmap getBitmapFromStream(InputStream inputStream, File saveFile) throws IOException {
        if (saveFile != null && saveFile.length() > 0) {
            Bitmap bm = BitmapFactory.decodeFile(saveFile.getAbsolutePath());
            return bm;
        }
        //获取流
        FileOutputStream fos = new FileOutputStream(saveFile);
        int len = -1;
        byte buffer[] = new byte[1024];
        while ((len = inputStream.read(buffer)) != -1) {
            fos.write(buffer, 0, len);
        }
        fos.close();
        inputStream.close();
        //通过流创建一个bitmap对象
        //Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        Bitmap bitmap = BitmapFactory.decodeFile(saveFile.getAbsolutePath());
        return bitmap;
    }
}
