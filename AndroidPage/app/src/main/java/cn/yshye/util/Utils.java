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
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

///
/// 叶院生 2019/11/5
///
public class Utils {

    /**
     *  32位MD5加密
     * @param content 加密内容
     * @return
     */
    public static String md5Decode32(String content) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(content.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("NoSuchAlgorithmException",e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UnsupportedEncodingException", e);
        }
        //对生成的16字节数组进行补零操作
        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10){
                hex.append("0");
            }
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString().toLowerCase();
    }
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
