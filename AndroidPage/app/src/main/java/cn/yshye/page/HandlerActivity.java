package cn.yshye.page;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.yshye.base.BaseActivity;
import cn.yshye.widget.image.SmartImageView;

public class HandlerActivity extends BaseActivity {
    private TextView tv;
    private EditText et_url;
    private Button btn_show;
    private SmartImageView siv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handler);
        tv = findViewById(R.id.tv);
        tv.setTextSize(24);
        handler.sendEmptyMessage(1);


        et_url = findViewById(R.id.et_url);
        btn_show = findViewById(R.id.btn_show);
        siv = findViewById(R.id.siv);
        btn_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                siv.setImageUrl(et_url.getText().toString().trim(), R.mipmap.ic_launcher);
            }
        });
        et_url.setText("http://pic.people.com.cn/NMediaFile/2019/1105/MAIN201911051735000546857082333.jpg");
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == 1) {
                //创建一个日期对象
                Date d = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                //格式化为日期/时间字符串
                String cc = sdf.format(d);
                tv.setText(cc);
                // 延迟发送
                handler.sendEmptyMessageDelayed(1, 1000);
            }
        }
    };

}
