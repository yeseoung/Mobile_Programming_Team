package com.example.drughelper;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.drughelper.ui.login.LoginActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SiginActivity extends AppCompatActivity {
    DBHelper helper;
    SQLiteDatabase db;
    private EditText idName;
    private EditText Password;
    private EditText Password2;
    int id = 0;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sigin);
        helper = new DBHelper(this);
        try {
            db = helper.getWritableDatabase();
        } catch (Exception e) {
            db = helper.getReadableDatabase();
        }
        if (Password.getText().toString().equals(Password2)) {

            // 1. 에디트텍스트에서 사용자가 입력한 진짜 '값'을 가져옵니다.
            String inputUsername = idName.getText().toString().trim();
            String inputPassword = Password.getText().toString().trim();

            // 2. ContentValues 객체를 생성하여 데이터를 Key-Value 형태로 담습니다.
            ContentValues values = new ContentValues();
            values.put("USERNAME", inputUsername); // "DB컬럼명", 자바변수값
            values.put("PASSWORD", inputPassword);

            // 3. db.insert() 메서드를 이용해 안전하게 삽입합니다.
            // 결과값으로 삽입된 행(Row)의 ID가 반환됩니다. (실패 시 -1)
            long result = db.insert("SIGN2", null, values);

            if (result != -1) {
                id++;
                Toast.makeText(this, "회원가입 성공!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SiginActivity.this, LoginActivity.class);
                startActivity(intent); // 회원가입이 끝났으니 현재 화면 종료 (로그인 화면으로 돌아감)
            } else {
                Toast.makeText(this, "회원가입 실패 (DB 오류)", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SiginActivity.this, SiginActivity.class); // 자기 자신 호출

                // 중요: 기존에 켜져 있던 메인 화면을 위로 올리고, 그 위에 쌓인 다른 화면이 있다면 제거합니다.
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                startActivity(intent);
            }

        } else {
            // 비밀번호와 비밀번호 확인이 일치하지 않을 때
            Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.signin2), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}