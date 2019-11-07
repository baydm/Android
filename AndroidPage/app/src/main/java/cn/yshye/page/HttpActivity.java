package cn.yshye.page;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import cn.yshye.base.BaseActivity;
import cn.yshye.util.Utils;
import cz.msebera.android.httpclient.Header;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpActivity extends BaseActivity implements View.OnClickListener {

    private EditText etUname;
    private EditText etPwd;
    private Button btnGet1;
    private Button btnPost1;
    private Button btnGet2;
    private Button btnPost2;
    private Button btnGet3;
    private Button btnPost3;
    private TextView tv;
    final String PATH = "http://devapp.mfzhaopu.com/Auth/login";
    final String COMP_CODE = "V9Base";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_http);
        initView();
    }

    private void initView() {
        etUname = findViewById(R.id.et_uname);
        etPwd = findViewById(R.id.et_pwd);
        btnGet1 = findViewById(R.id.btn_get_1);
        btnPost1 = findViewById(R.id.btn_post_1);
        btnGet2 = findViewById(R.id.btn_get_2);
        btnPost2 = findViewById(R.id.btn_post_2);
        btnGet3 = findViewById(R.id.btn_get_3);
        btnPost3 = findViewById(R.id.btn_post_3);
        tv = findViewById(R.id.tv);

        etUname.setText("yeys");
        etPwd.setText("yeys123456");

        btnGet1.setOnClickListener(this);
        btnGet2.setOnClickListener(this);
        btnGet3.setOnClickListener(this);
        btnPost1.setOnClickListener(this);
        btnPost2.setOnClickListener(this);
        btnPost3.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String uName = etUname.getText().toString().trim();
        String pwd = Utils.md5Decode32(etPwd.getText().toString().trim());
        switch (v.getId()) {
            case R.id.btn_get_1:
                doHttpUrlGet(uName, pwd);
                break;
            case R.id.btn_post_1:
                doHttpUrlPost(uName, pwd);
                break;
            case R.id.btn_get_2:
                doOkHttpGet(uName, pwd);
                break;
            case R.id.btn_post_2:
                doOkHttpPost(uName, pwd);
                break;
            case R.id.btn_get_3:
                doAsyncHttpClientGet(uName, pwd);
                break;
            case R.id.btn_post_3:
                doAsyncHttpClientPost(uName, pwd);
                break;
            default:
                break;
        }
    }

    private void doHttpUrlGet(final String uName, final String pwd) {
        new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(PATH + "?CompCode=" + COMP_CODE + "&UserName=" + uName + "&Pwd=" + pwd);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(10000);
                    int code = connection.getResponseCode();
                    if (code == 200) {
                        InputStream stream = connection.getInputStream();
                        String count = Utils.getStringFromStream(stream);
                        showToast("请求成功！");
                        setTextCount(count);
                    } else {
                        setTextCount("请求错误！错误码：" + code);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    setTextCount("请求错误！异常：" + e.getMessage());
                }
            }
        }.start();
    }

    private void doHttpUrlPost(final String uName, final String pwd) {
        new Thread() {
            @Override
            public void run() {
                try {
                    String params = "CompCode=" + COMP_CODE + "&UserName=" + URLEncoder.encode(uName, "utf-8") + "&Pwd=" + URLEncoder.encode(pwd, "utf-8");
                    URL url = new URL(PATH);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(10000);
                    //设置跟post请求相关的请求头
                    //告诉服务器 要传递的数据类型
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    //告诉服务器,传递的数据长度
                    connection.setRequestProperty("Content-Length", String.valueOf(params.length()));
                    //打开输出流
                    connection.setDoOutput(true);
                    //通过流把请求体写到服务端
                    connection.getOutputStream().write(params.getBytes());
                    int code = connection.getResponseCode();
                    if (code == 200) {
                        InputStream stream = connection.getInputStream();
                        String count = Utils.getStringFromStream(stream);
                        showToast("请求成功！");
                        setTextCount(count);
                    } else {
                        setTextCount("请求错误！错误码：" + code);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    setTextCount("请求错误！异常：" + e.getMessage());
                }
            }
        }.start();
    }


    private void doAsyncHttpClientGet(String uName, String pwd) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("CompCode", COMP_CODE);
        params.add("UserName", uName);
        params.add("Pwd", pwd);
        client.get(PATH, params, new MyAsyncHttpResponseHandler());

    }

    private void doAsyncHttpClientPost(String uName, String pwd) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("CompCode", COMP_CODE);
        params.add("UserName", uName);
        params.add("Pwd", pwd);
        client.post(PATH, params, new MyAsyncHttpResponseHandler());
    }

    private void doOkHttpGet(String uName, String pwd) {
        OkHttpClient client = new OkHttpClient();
        try {
            URL url = new URL(PATH + "?CompCode=" + COMP_CODE + "&UserName=" + uName + "&Pwd=" + pwd);
            Request request = new Request.Builder().url(url).build();
            client.newCall(request).enqueue(new MyCallback());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    private void doOkHttpPost(String uName, String pwd) {
        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder().add("CompCode", COMP_CODE).add("UserName", uName).add("Pwd", pwd).build();
        Request request = new Request.Builder().url(PATH).post(body).build();
        client.newCall(request).enqueue(new MyCallback());
    }


    private void setTextCount(final String count) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv.setText(count);
            }
        });
    }

    class MyCallback implements Callback {

        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            setTextCount("请求失败：异常" + e.getMessage());
        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) {
            if (response.code() == 200) {
                try {
                    String count = response.body().string();
                    showToast("请求成功！");
                    setTextCount(count);
                } catch (IOException e) {
                    e.printStackTrace();
                    setTextCount("请求失败：异常" + e.getMessage());
                }
            } else {
                setTextCount("请求失败：错误码=" + response.code());
            }
        }
    }

    class MyAsyncHttpResponseHandler extends AsyncHttpResponseHandler {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            if (statusCode == 200) {
                try {
                    String count = new String(responseBody, "utf-8");
                    showToast("请求成功！");
                    setTextCount(count);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    setTextCount("请求失败：异常" + e.getMessage());
                }
            } else {
                setTextCount("请求失败：错误码=" + statusCode);
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            setTextCount("请求失败：异常" + error.getMessage());
        }
    }

}
