package com.example.drughelper;

// 데이터 클래스(DTO)들이 같은 패키지에 있다면 자동으로 인식되지만,
// 다른 패키지에 있다면 아래처럼 import가 필요할 수 있습니다.


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService { // <- 제네릭 꺾쇠괄호 삭제

    @POST("/api/login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    @POST("/api/register")
    Call<Void> registerUser(@Body LoginRequest registerRequest); // 회원가입
}