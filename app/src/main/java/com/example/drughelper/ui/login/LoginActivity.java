package com.example.drughelper.ui.login;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login2);

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

        // Retrofit 초기화
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://192.169.35.2:34535") // 본인 서버 포트(8080 등)에 맞게 설정
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        // 버튼 클릭 이벤트 설정
        btnLogin.setOnClickListener(v -> performLogin());
    } // 👈 onCreate 메서드가 여기서 깔끔하게 끝납니다!

    // 👈 onCreate 밖에 별도로 존재하는 독립된 메서드입니다.
    private void performLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // 간단한 유효성 검사
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "모든 필드를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        LoginRequest loginRequest = new LoginRequest(email, password);
        Call<LoginResponse> call = apiService.loginUser(loginRequest);

        // 비동기 네트워크 통신 실행
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getToken();
                    saveToken(token);

                    // 메인 화면으로 이동
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "로그인 실패: 이메일 또는 비밀번호를 확인하세요.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "네트워크 오류 발생: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    } // 👈 performLogin 메서드가 끝납니다.

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