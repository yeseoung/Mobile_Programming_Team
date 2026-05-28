package com.example.drughelper;



import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.widget.Toolbar;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.drughelper.ui.login.LoginActivity;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements MyAdapter.ItemClickListener {
    MyAdapter adapter;
    private ImageButton imageButton1;
    private ImageButton imageButton2;
    private ImageButton imageButton3;
    private ImageButton imageButton4;
    private ImageButton imageButton5;
    private View viewFab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        // 기본 액션바 타이틀 숨기기 (커스텀 텍스트뷰를 사용할 것이므로)
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // 2. 날짜 텍스트뷰에 현재 날짜 주입
        TextView tvToolbarDate = findViewById(R.id.tv_toolbar_date);

        // 원하는 포맷 결정: 2026.05.25 (월)
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd (E)", Locale.KOREAN);
        String currentDate = formatter.format(new Date());

        tvToolbarDate.setText(currentDate);
        String[] data = new String[5];
        for (int i=1; i <= 5; i++) { data[i-1] = "약 이름"+i; }

        imageButton1 = findViewById(R.id.setting_image);
        imageButton2 = findViewById(R.id.record);
        imageButton3 = findViewById(R.id.statics_image);
        imageButton4 = findViewById(R.id.home_image);
        imageButton5 = findViewById(R.id.logout_image);
        viewFab = findViewById(R.id.fab);
        RecyclerView recyclerView = findViewById(R.id.timeMedicine);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyAdapter(this,data);
        adapter.setClickListener(this);

        recyclerView.setAdapter(adapter);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    @Override
    public void onItemClick(View view, int position) {
        Log.i("TAG","item: " + adapter.getItem(position)+"number: " + position);
    }

    public void onClick(View view) {
        int id = view.getId();

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.onClick(v); // 기존에 만드신 onClick 메서드 호출
            }
        };
        imageButton1.setOnClickListener(listener);
        imageButton2.setOnClickListener(listener);
        imageButton3.setOnClickListener(listener);
        imageButton4.setOnClickListener(listener);
        imageButton5.setOnClickListener(listener);
        viewFab.setOnClickListener(listener);
        if (id == R.id.statics_layout) {
            Intent intent = new Intent(MainActivity.this, StatsActivity2.class);
            startActivity(intent);
        }

        else if (id == R.id.fab) {
            Intent intent = new Intent(MainActivity.this, AddMedicineActivity.class);
            startActivity(intent);
        }

        else if (id == R.id.logout_image) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
        }
        else if (id == R.id.home_layout) {
            Intent intent = new Intent(MainActivity.this, MainActivity.class); // 자기 자신 호출

            // 중요: 기존에 켜져 있던 메인 화면을 위로 올리고, 그 위에 쌓인 다른 화면이 있다면 제거합니다.
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            startActivity(intent);
        }
        else if (id == R.id.setting_layout) {
            Intent intent = new Intent(MainActivity.this, RecordActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.record) {
            Intent intent = new Intent(MainActivity.this, RecordActivity.class);
            startActivity(intent);
        }
    }
}

