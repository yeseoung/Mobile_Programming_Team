package com.example.drughelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "medicine_sign.db";

    // 💡 [중요] DB 버전을 3에서 4로 올립니다. 버전이 올라가야만 아래 onUpgrade가 실행되면서 테이블이 새로 만들어집니다!
    private static final int DATABASE_VERSION = 4;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. 기존에 쓰시던 contacts 테이블 유지
        db.execSQL("CREATE TABLE contacts ( _id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, tel TEXT)");

        // 2. 💡 [핵심 추가] 회원가입과 로그인에 꼭 필요한 sign2 테이블 생성 쿼리 (소문자 스키마 반영)
        String sql = "CREATE TABLE sign2 (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT NOT NULL, " +
                "password TEXT NOT NULL);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 💡 버전이 바뀌면 기존 테이블들을 깔끔하게 밀어버리고 다시 onCreate를 호출합니다.
        db.execSQL("DROP TABLE IF EXISTS contacts");
        db.execSQL("DROP TABLE IF EXISTS sign2"); // sign2 테이블도 같이 드랍 처리
        onCreate(db);
    }
}
