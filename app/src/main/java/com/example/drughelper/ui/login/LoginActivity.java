package com.example.drughelper.ui.login;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.example.drughelper.ApiService;
import com.example.drughelper.DBHelper;
import com.example.drughelper.LoginRequest;
import com.example.drughelper.LoginResponse;
import com.example.drughelper.MainActivity;
import com.example.drughelper.R;
import com.example.drughelper.SiginActivity;
import com.example.drughelper.StatsActivity2;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class LoginActivity extends AppCompatActivity {
    private DBHelper dbHelper;
    SQLiteDatabase db;
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private Button btnRegister;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login2);
        dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // 시스템 바 패딩 설정 (기존에 아래쪽에 꼬여있던 코드를 위로 올렸습니다)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.Loginmain), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 뷰 초기화
        etEmail = findViewById(R.id.username);
        etPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.signin);
        btnRegister = findViewById(R.id.register);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 버튼을 누른 순간의 입력값 가져오기
                String inputId = etEmail.getText().toString().trim();
                String inputPassword = etPassword.getText().toString().trim();

                // 입력값 공백 체크
                if (inputId.isEmpty() || inputPassword.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "아이디와 비밀번호를 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 💡 [수정] 스키마 스크린샷에 맞춰 테이블명과 컬럼명을 모두 소문자로 변경했습니다.
                Cursor cursor = db.rawQuery("SELECT username, password FROM sign2", null);
                boolean isLoginSuccess = false;

                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        // 💡 [수정] 대문자 "USERNAME" -> 소문자 "username"으로 일치시켜 에러를 방지합니다.
                        int userIndex = cursor.getColumnIndexOrThrow("username");
                        int passIndex = cursor.getColumnIndexOrThrow("password");
                        do {
                            String dbUsername = cursor.getString(userIndex);
                            String dbPassword = cursor.getString(passIndex);

                            // 입력한 값과 DB 값이 일치하는지 비교
                            if (inputId.equals(dbUsername) && inputPassword.equals(dbPassword)) {
                                isLoginSuccess = true;
                                break;
                            }
                        } while (cursor.moveToNext());
                    }
                    cursor.close(); // Cursor 객체는 사용 후 반드시 닫아주어야 메모리 누수가 없습니다.
                }

                // 결과 처리
                if (isLoginSuccess) {
                    Toast.makeText(LoginActivity.this, "로그인 성공!", Toast.LENGTH_SHORT).show();

                    // 메인 화면으로 전환
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    LoginActivity.this.startActivity(intent);
                    finish(); // 💡 로그인에 성공했으므로 로그인 화면은 종료하여 뒤로가기를 눌러도 안 나오게 막습니다.
                } else {
                    Toast.makeText(LoginActivity.this, "아이디 또는 비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SiginActivity.class);
                LoginActivity.this.startActivity(intent);
            }
        });
    }

    // 👈 토큰 암호화 저장 메서드
    private void saveToken(String token) {
        try {
            MasterKey masterKey = new MasterKey.Builder(this)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                    this,
                    "secret_shared_prefs",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            sharedPreferences.edit().putString("auth_token", token).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}