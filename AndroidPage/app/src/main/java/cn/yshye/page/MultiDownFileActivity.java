package cn.yshye.page;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

public class MultiDownFileActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etUrl;
    private EditText etCount;
    private Button btnDown;
    private LinearLayout llProgress;
    private int blockSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_down_file);
        initView();

        etUrl.setText("http://v9app.mfzhaopu.com/app/apk/PMMobileToBV1.2.0.1911051734.apk");
        etCount.setText("4");
        btnDown.setOnClickListener(this);
    }

    private void initView() {
        etUrl = findViewById(R.id.et_url);
        etCount = findViewById(R.id.et_count);
        btnDown = findViewById(R.id.btn_down);
        llProgress = findViewById(R.id.ll_progress);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_down) {
            final String path = etUrl.getText().toString().trim();
            final int threadCount = Integer.valueOf(etCount.getText().toString().trim());
            llProgress.removeAllViews();
            for (int i = 0; i < threadCount; i++) {
                View.inflate(getApplicationContext(), R.layout.item_progress, llProgress);
            }

            new Thread() {
                @Override
                public void run() {
                    try {
                        URL url = new URL(path);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setConnectTimeout(10000);
                        int code = connection.getResponseCode();
                        if (code == 200) {
                            int length = connection.getContentLength();
                            RandomAccessFile file = new RandomAccessFile(getFileName(path), "rw");
                            file.setLength(length);
                            blockSize = length / threadCount;
                            for (int i = 0; i < threadCount; i++) {
                                int startIndex = i * blockSize;
                                int endIndex = (i + 1) * blockSize - 1;
                                if (i == threadCount - 1) {
                                    endIndex = length - 1;
                                }
                                ProgressBar progressBar = (ProgressBar) llProgress.getChildAt(i);
                                progressBar.setMax(endIndex - startIndex);
                                new DownLoadThred(startIndex, endIndex, i, path, progressBar).start();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }.start();
        }
    }

    private class DownLoadThred extends Thread {
        private int startIndex;
        private int endIndex;
        private int threadIndex;
        private String path;
        private ProgressBar progressBar;

        public DownLoadThred(int startIndex, int endIndex, int threadIndex, String path, ProgressBar progressBar) {
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.threadIndex = threadIndex;
            this.path = path;
            this.progressBar = progressBar;
        }

        @Override
        public void run() {
            try {
                // 读取出记录下来的位置
                File temp = new File(getFileName(path) + threadIndex + ".log");
                if (temp != null && temp.length() > 0) {
                    // 说明日志文件有内容
                    FileInputStream fis = new FileInputStream(temp);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
                    String result = reader.readLine();
                    // 读出记录下来的位置更新下载请求数据的起始位置
                    startIndex = Integer.parseInt(result);
                }

                URL url = new URL(path);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                // 设置Range头，用于计算好的开始索引和技术索引到服务器请求数据
                connection.setRequestProperty("Range", "bytes=" + startIndex + "-" + endIndex);
                if (connection.getResponseCode() == 206) {
//                    System.out.println("线程 " + threadIndex + "开始下载" + startIndex);
                    InputStream inputStream = connection.getInputStream();
                    int len = -1;
                    byte[] buffer = new byte[1024];
                    RandomAccessFile file = new RandomAccessFile(getFileName(path), "rw");
                    file.seek(startIndex);
                    int count = 0;
                    while ((len = inputStream.read(buffer)) != -1) {
                        file.write(buffer, 0, len);
                        count = count + len;
                        int position = count + startIndex;
                        progressBar.setProgress(position - threadIndex * blockSize);

                        // 断点下载存储位置
                        RandomAccessFile tempFile = new RandomAccessFile(getFileName(path) + threadIndex + ".log", "rwd");
                        tempFile.write(String.valueOf(position).getBytes());
                        // 一定要加在，因为android中，所有的IO操作最终都是需要句柄来操作文件实现的。
                        // 句柄数量对于每一个app都有一个上限，常见的是1024个。一旦某个app同时使用的句柄数超过了这个限制，就会看到这个异常：too many open files。
                        tempFile.close();
                    }
                    file.close();
                    inputStream.close();
                    // 下载完成后，删除日志
                    if (temp != null) {
                        temp.delete();
                    }
//                    System.out.println("线程 " + threadIndex + " 结束下载");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getFileName(String path) {
        String name = getCacheDir() + path.substring(path.lastIndexOf("/"));
        return name;
    }
}
