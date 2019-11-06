package cn.yshye.page;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import cn.yshye.base.BaseActivity;
import cn.yshye.util.Utils;

public class ShowImageActivity extends BaseActivity {

    private EditText et_url;
    private Button btn_show;
    private ImageView iv_pic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        et_url = findViewById(R.id.et_url);
        btn_show = findViewById(R.id.btn_show);
        iv_pic = findViewById(R.id.iv_pic);
        btn_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImage();
            }
        });
        et_url.setText("http://pic.people.com.cn/NMediaFile/2019/1105/MAIN201911051735000546857082333.jpg");
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            int what = msg.what;
            if (what == 1) {
                Bitmap bitmap = (Bitmap) msg.obj;
                iv_pic.setImageBitmap(bitmap);
            } else {
                if (msg.obj instanceof String) {
                    Toast.makeText(ShowImageActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    private void showImage() {
        new Thread() {
            @Override
            public void run() {
                Message message = new Message();
                try {
                    String path = et_url.getText().toString().trim();
                    URL url = new URL(path);
                    String name = path.substring(path.lastIndexOf("/"));
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    InputStream inputStream = connection.getInputStream();
                    File file = new File(getCacheDir() + "/" + name);
                    Bitmap bitmap = Utils.getBitmapFromStream(inputStream, file);
                    message.what = 1;
                    message.obj = bitmap;
                    handler.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                    message.what = 2;
                    message.obj = "请求错误！";
                    handler.sendMessage(message);
                }

            }
        }.start();

    }
}
