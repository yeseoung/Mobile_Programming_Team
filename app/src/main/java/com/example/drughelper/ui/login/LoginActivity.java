package com.example.drughelper.ui.login;

import static androidx.core.content.ContextCompat.startActivity;

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
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login2);
        dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // 시스템 바 패딩 설정 (기존에 아래쪽에 꼬여있던 코드를 위로 올렸습니다)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 뷰 초기화
        etEmail = findViewById(R.id.username);
        etPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.signin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 버튼을 누른 '이 순간'의 입력값을 가져와야 합니다.
                String inputId = etEmail.getText().toString().trim();
                String inputPassword = etPassword.getText().toString().trim();

                // 입력값이 비어있는지 먼저 간단히 체크해 주면 좋습니다.
                if (inputId.isEmpty() || inputPassword.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "아이디와 비밀번호를 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Cursor cursor = db.rawQuery("SELECT USERNAME, PASSWORD FROM SIGN2", null);
                boolean isLoginSuccess = false;

                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        int userIndex = cursor.getColumnIndexOrThrow("USERNAME");
                        int passIndex = cursor.getColumnIndexOrThrow("PASSWORD");
                        do {
                            String dbUsername = cursor.getString(userIndex);
                            String dbPassword = cursor.getString(passIndex);

                            if (inputId.equals(dbUsername) && inputPassword.equals(dbPassword)) {
                                isLoginSuccess = true;
                                break;
                            }
                        } while (cursor.moveToNext());
                    }
                    cursor.close();
                }

                // 3. 결과 처리 (클릭 리스너 안이므로 login_activity2.this 처럼 컨텍스트를 명시)
                if (isLoginSuccess) {
                    Toast.makeText(LoginActivity.this, "로그인 성공!", Toast.LENGTH_SHORT).show();
                    // 여기에 다음 화면으로 넘어가는 Intent 코드를 넣으시면 됩니다.
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "아이디 또는 비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                }
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