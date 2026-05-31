package com.example.drughelper;

import android.content.ContentValues;
import android.content.Intent;
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

import com.example.drughelper.ui.login.LoginActivity;

public class SiginActivity extends AppCompatActivity {
    DBHelper helper;
    SQLiteDatabase db;
    private EditText idName;
    private EditText Password;
    private EditText Password2;
    private Button SignButton;
    int id = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sigin);

        // 1. 뷰 컴포넌트 초기화
        helper = new DBHelper(this);
        idName = findViewById(R.id.idName);
        Password = findViewById(R.id.Password);
        Password2 = findViewById(R.id.Password2);
        SignButton = findViewById(R.id.signin);

        // 데이터베이스 초기화
        try {
            db = helper.getWritableDatabase();
        } catch (Exception e) {
            db = helper.getReadableDatabase();
        }

        // 시스템 바 패딩 설정 (안전하게 try-catch 적용)
        try {
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.signin2), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 2. 💡 [핵심] 사용자가 가입 버튼을 누르는 순간 실행되는 리스너 인터페이스 구현
        SignButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 버튼을 누른 순간 입력값 가져오기
                String inputUsername = idName.getText().toString().trim();
                String inputPassword = Password.getText().toString().trim();
                String inputPassword2 = Password2.getText().toString().trim();

                // 방어 코드: 입력란이 하나라도 비어있는지 체크
                if (inputUsername.isEmpty() || inputPassword.isEmpty() || inputPassword2.isEmpty()) {
                    Toast.makeText(SiginActivity.this, "모든 정보를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 💡 [수정] 비밀번호와 비밀번호 확인 '텍스트' 문자열 비교 수행
                if (inputPassword.equals(inputPassword2)) {

                    // ContentValues 객체에 데이터 담기
                    ContentValues values = new ContentValues();
                    values.put("username", inputUsername);
                    values.put("password", inputPassword);

                    // 버튼이 클릭된 상태에서 실제 안전하게 DB 삽입 진행
                    long result = db.insert("sign2", null, values);

                    if (result != -1) {
                        id++;
                        Toast.makeText(SiginActivity.this, "회원가입 성공!", Toast.LENGTH_SHORT).show();

                        // 회원가입 완료 후 로그인 페이지로 깔끔하게 전환
                        Intent intent = new Intent(SiginActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish(); // 현재 회원가입 화면은 스택에서 완전히 닫아 뒤로가기 시 안 나오게 방지
                    } else {
                        Toast.makeText(SiginActivity.this, "회원가입 실패 (DB 오류)", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    // 비밀번호 확인이 실패했을 때의 예외 메시지 처리
                    Toast.makeText(SiginActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        }); // 👈 깔끔하게 맞아떨어지는 리스너 중괄호 마무리
    }
}