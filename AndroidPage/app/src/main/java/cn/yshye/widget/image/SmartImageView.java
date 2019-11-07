package cn.yshye.widget.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.DimenRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

///
/// 叶院生 2019/11/7
///
public class SmartImageView extends ImageView {


    private static final int GET_PIC_SUCCESS = 0;
    private static final int GET_PIC_FAIL = 1;


    //一个参数构造  给程序员通过代码的方式动态创建一个View对象使用的
    public SmartImageView(Context context) {
        super(context);
    }

    //两个参数的构造 多了一个AttributeSet属性集合 在解析xml文件  android:layout_width
    //android:textSize 这些属性 解析之后都会封装到AttributeSet中
    //系统在解析xml布局文件时创建View对象 就会调用这个两个参数的构造
    public SmartImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    //三个参数构造 多了一个 defStyleAttr 样式  是一个自定义属性,
    public SmartImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // 对view设置属性有4个途径,分别是
    // 1.直接在xml对其定义.
    // 2.在xml中用style对view的属性进行定义.
    // 3.通过theme对其定义.
    // 4.如果第3条没满足或被置为0,可定义一个style设置给它.
    // defStyleRes 这个参数在构造方法中接收的一样是一个int类型的style,我们依然可以按照R.style.xxx的方式设置给它
    public SmartImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case GET_PIC_SUCCESS:
                    setImageBitmap((Bitmap) msg.obj);
                    break;
                case GET_PIC_FAIL:
                    setImageResource(msg.arg1);
                    break;
            }
        }
    };

    public void setImageUrl(final String path, final @DimenRes int resId) {
        new Thread() {
            @Override
            public void run() {
                Message message = Message.obtain();
                try {
                    URL url = new URL(path);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(10000);
                    int code = connection.getResponseCode();
                    if (code == 200) {
                        InputStream inputStream = connection.getInputStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        message.what = GET_PIC_SUCCESS;
                        message.obj = bitmap;
                    } else {
                        message.what = GET_PIC_FAIL;
                        message.arg1 = resId;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    message.what = GET_PIC_FAIL;
                    message.arg1 = resId;
                }

                handler.sendMessage(message);
            }
        }.start();

    }
}
