package com.example.drughelper;

import android.app.Activity;
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
    private EditText etEmail, etPassword, etPasswordConfirm;
    private Button btnRegister;
    private ApiService apiService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sigin);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.signin), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 뷰 초기화 (작성하신 id 그대로 매칭)
        etEmail = findViewById(R.id.idName);
        etPassword = findViewById(R.id.Password);
        etPasswordConfirm = findViewById(R.id.Password2);
        btnRegister = findViewById(R.id.signin);

        // Retrofit 초기화 (LoginActivity와 동일하게 reqres.in 가짜 주소 사용)
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://reqres.in/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        // 💡 버튼 클릭 시 회원가입 로직 실행하도록 연결
        btnRegister.setOnClickListener(v -> performRegister());
    } // 👈 onCreate 끝

    // 💡 회원가입 핵심 로직 메서드
    private void performRegister() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String passwordConfirm = etPasswordConfirm.getText().toString().trim();

        // 1. 유효성 검사 (빈칸 체크)
        if (email.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()) {
            Toast.makeText(this, "모든 필드를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. 비밀번호와 비밀번호 확인이 일치하는지 체크
        if (!password.equals(passwordConfirm)) {
            Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 서버로 보낼 데이터 구조 생성
        LoginRequest registerRequest = new LoginRequest(email, password);
        Call<Void> call = apiService.registerUser(registerRequest);

        // 3. 비동기 네트워크 통신 시작
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SigninActivity.this, "회원가입 성공! 로그인해 주세요.", Toast.LENGTH_SHORT).show();
                    finish(); // 회원가입 화면을 닫고 이전 로그인 화면으로 돌아갑니다.
                } else {
                    Toast.makeText(SigninActivity.this, "회원가입 실패: 이미 존재하거나 올바르지 않은 계정입니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(SigninActivity.this, "네트워크 오류 발생: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}