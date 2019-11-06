package cn.yshye;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import cn.yshye.page.HandlerActivity;
import cn.yshye.page.R;
import cn.yshye.page.ShowImageActivity;
import cn.yshye.page.WebSourceActivity;

public class MainActivity extends AppCompatActivity {
    private PageBean[] pages = new PageBean[]{
            new PageBean("WebSource", WebSourceActivity.class),
            new PageBean("ShowImage", ShowImageActivity.class),
            new PageBean("HandlerDelayed", HandlerActivity.class),
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView lv = findViewById(R.id.lv);
        lv.setAdapter(new ArrayAdapter<PageBean>(this, android.R.layout.simple_list_item_1, pages));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(MainActivity.this, pages[position].getClassName()));
            }
        });
    }


    class PageBean {
        private String label;
        private Class className;

        public PageBean(String label, Class className) {
            this.label = label;
            this.className = className;
        }

        public String getLabel() {
            return label;
        }

        public Class getClassName() {
            return className;
        }

        @NonNull
        @Override
        public String toString() {
            return this.label;
        }
    }
}


