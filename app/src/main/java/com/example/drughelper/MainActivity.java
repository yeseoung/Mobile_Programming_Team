package com.example.drughelper;



import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import androidx.appcompat.widget.Toolbar;

import android.widget.ImageButton;
import android.widget.TextView;

import com.example.drughelper.ui.login.LoginActivity;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements MyAdapter.ItemClickListener, View.OnClickListener, Runnable {
    MyAdapter adapter;
    private ImageButton imageButton1;
    private View imageButton2;
    private ImageButton imageButton3;
    private ImageButton imageButton4;
    private ImageButton imageButton5;
    private List<Medicine> medicineList = new ArrayList<>();
    private View viewFab;
    private static final String SERVICE_KEY = "a36646983d6f22d35479f4c6f89b15aa734627b67d45ad3bd7f0077a4479db1a";
    private RecyclerView rwMedicineList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // 1. 리사이클러뷰 초기화 및 레이아웃 매니저 결합 (중복 라인 제거)
        rwMedicineList = findViewById(R.id.timeMedicine);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rwMedicineList.setLayoutManager(layoutManager);

        medicineList = new ArrayList<>();

        // 2. 초기 빈 어댑터 바인딩해 두기
        adapter = new MyAdapter(this, medicineList);
        rwMedicineList.setAdapter(adapter);
        adapter.setClickListener(this);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(new Runnable() {
            @Override
            public void run() {
                String[] targetQueries = {"비타민"};
                medicineList.clear();
                for (String query : targetQueries) {
                    String jsonResult = searchMedicineList(query);

                    Log.d("DrugHelperAPI", query + " 결과값: " + jsonResult);
                    if (jsonResult != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(jsonResult);
                            JSONObject body = jsonObject.optJSONObject("body");

                            if (body != null) {
                                int totalCount = body.optInt("totalCount", 0);
                                Log.d("DrugHelperAPI", query + " 검색된 개수: " + totalCount);

                                if (totalCount > 0) {
                                    JSONArray items = body.getJSONArray("items");

                                    // 누적으로 쌓아야 하므로 clear() 없이 계속 add 해줍니다.
                                    for (int i = 0; i < items.length(); i++) {
                                        JSONObject item = items.getJSONObject(i);
                                        String name = item.optString("itemName", "알 수 없는 약");
                                        String itemImage = item.optString("itemImage", "").replaceAll("<[^>]*>", "");

                                        medicineList.add(new Medicine(name, itemImage));
                                    }
                                }
                            }
                        } catch (Exception e) {
                            Log.e("ADAPTER_VER", query + " 파싱 에러: " + e.getMessage());
                        }
                    }
                } // For문 끝 (모든 약 데이터가 medicineList에 누적됨)
                Log.d("DrugHelperAPI", "최종 리스트 사이즈: " + medicineList.size());
                // 2. UI 스레드에서 어댑터 생성 및 연결
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // ✨ [핵심 수정] 'this' 대신 'MainActivity.this'를 사용하여 컨텍스트 호환성 문제를 해결합니다.
                        adapter = new MyAdapter(MainActivity.this, medicineList);
                        rwMedicineList.setAdapter(adapter);

                        // 만약 onClickListener 세팅이 필요하다면 여기에 이어서 작성합니다.
                        adapter.setClickListener(MainActivity.this);
                        adapter.notifyDataSetChanged();
                    }
                });

            }
        });


        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        // 기본 액션바 타이틀 숨기기
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // 날짜 텍스트뷰에 현재 날짜 주입
        TextView tvToolbarDate = findViewById(R.id.tv_toolbar_date);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd (E)", Locale.KOREAN);
        String currentDate = formatter.format(new Date());
        tvToolbarDate.setText(currentDate);

        // 하단 탭 및 FAB 컴포넌트 연결
        imageButton1 = findViewById(R.id.setting_image);
        imageButton2 = findViewById(R.id.record);
        imageButton3 = findViewById(R.id.statics_image);
        imageButton4 = findViewById(R.id.home_image);
        imageButton5 = findViewById(R.id.logout_image);
        viewFab = findViewById(R.id.fab);

        // 클릭 리스너 등록
        imageButton1.setOnClickListener(this);
        imageButton2.setOnClickListener(this);
        imageButton3.setOnClickListener(this);
        imageButton4.setOnClickListener(this);
        imageButton5.setOnClickListener(this);
        viewFab.setOnClickListener(this);

        // 시스템 바 인셋 설정 (EdgeToEdge 호환)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    } // === [onCreate 끝] ===
    private String searchMedicineList(String medName) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        try {
            String encodedMedName = URLEncoder.encode(medName, "UTF-8");
            StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1471000/DrbEasyDrugInfoService/getDrbEasyDrugList");
            urlBuilder.append("?serviceKey=").append(SERVICE_KEY);
            urlBuilder.append("&itemName=").append(encodedMedName);
            urlBuilder.append("&type=json");
            urlBuilder.append("&numOfRows=3"); // 여러 항목을 보기 위해 3개 지정
            urlBuilder.append("&pageNo=1");

            URL url = new URL(urlBuilder.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                return result.toString();
            }
        } catch (Exception e) {
            Log.e("ADAPTER_VER", "통신 실패: " + e.getMessage());
        } finally {
            if (reader != null) { try { reader.close(); } catch (Exception ignored) {} }
            if (urlConnection != null) { urlConnection.disconnect(); }
        }
        return null;
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

        if (id == R.id.statics_image) {
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
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.record) {
            Intent intent = new Intent(MainActivity.this, RecordActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void run() {

    }
}

