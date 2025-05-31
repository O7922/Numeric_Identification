package com.example.number_identification;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Mini_Picture_Display MPD;

    Main_View M_View;//キャンバスの作成
    Button clear_button;//手書き文字リセット用ボタン

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //手書き文字
        MPD = findViewById(R.id.MPD);
        MPD.setX(100);
        MPD.setY(100);

        M_View = findViewById(R.id.M_View);
        M_View.set_up_this(MPD);

        //リセットボタンの処理
        clear_button = findViewById(R.id.clear_button);
        clear_button.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent event){
                int action = event.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        MPD.clear();
                        break;
                }
                return false;
            }
        });
    }
}