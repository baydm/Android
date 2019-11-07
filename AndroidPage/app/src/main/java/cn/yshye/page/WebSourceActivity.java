package cn.yshye.page;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import cn.yshye.base.BaseActivity;
import cn.yshye.util.Utils;

/**
 * 显示网页内容
 */
public class WebSourceActivity extends BaseActivity {
    private EditText et_url;
    private Button btn_show;
    private TextView tv_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_source);
        et_url = findViewById(R.id.et_url);
        btn_show = findViewById(R.id.btn_show);
        tv_code = findViewById(R.id.tv_code);

        et_url.setText("https://www.baidu.com");
        btn_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWebSource();
            }
        });

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            int what = msg.what;
            if (what == 1) {
                String value = (String) msg.obj;
                tv_code.setText(value);
            }

        }
    };

    private void showWebSource() {
        new Thread() {
            @Override
            public void run() {
                try {
                    String path = et_url.getText().toString().trim();
                    URL url = new URL(path);
                    //                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    HttpsURLConnection connection1 = (HttpsURLConnection) url.openConnection();
                    connection1.setRequestMethod("GET");
                    connection1.setConnectTimeout(10000);
                    int responseCode = connection1.getResponseCode();
                    Log.d("D", "结果码：" + responseCode);
                    if (responseCode == 200) {
                        InputStream inputStream = connection1.getInputStream();
                        String result = Utils.getStringFromStream(inputStream);
                        Log.d("D", "请求结果：" + result);
                        Message message = new Message();
                        message.what = 1;
                        message.obj = result;
                        handler.sendMessage(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(WebSourceActivity.this, "网络链接异常", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }.start();


    }
}
