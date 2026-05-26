package com.example.drughelper;



import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.example.drughelper.ui.login.LoginActivity;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity implements MyAdapter.ItemClickListener {
    MyAdapter adapter;
    private Button button1;
    private Button button2;
    private Button button3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        String[] data = new String[5];
        for (int i=1; i <= 5; i++) { data[i-1] = "약 이름"+i; }

        button1 = findViewById(R.id.create);
        button2 = findViewById(R.id.button);
        button3 = findViewById(R.id.logout);

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
        button1.setOnClickListener(listener);
        button2.setOnClickListener(listener);
        button3.setOnClickListener(listener);
        if (id == R.id.create) {
            Intent intent = new Intent(MainActivity.this, StatsActivity2.class);
            startActivity(intent);
        }

        else if (id == R.id.button) {
            Intent intent = new Intent(MainActivity.this, AddMedicineActivity.class);
            startActivity(intent);
        }

        else if (id == R.id.logout) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
        }
    }
}

