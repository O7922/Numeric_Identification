package com.example.number_identification;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.util.AttributeSet;
public class Main_View extends View {


    Paint zPaint = new Paint();
    Paint rPaint = new Paint();
    Paint text_Paint = new Paint();

    Paint h1_Paint = new Paint();
    Paint h2_Paint = new Paint();
    Paint h3_Paint = new Paint();
    Mini_Picture_Display jud;

    Filter_unit F_U;

    Input_neuron I_N[] = new Input_neuron[20];//フィルター4個分の「数」とそれから出る４つの特徴(4 * 4) で合計20


    public Main_View(Context context,AttributeSet attrs){
        super(context,attrs);
        zPaint.setTextSize(30);
        rPaint.setColor(Color.RED);
        rPaint.setStrokeWidth(5);
        zPaint.setARGB(255,0,0,0);
        text_Paint.setTextSize(30);
        text_Paint.setColor(Color.BLUE);
        h1_Paint.setTextSize(300);
        h2_Paint.setTextSize(200);
        h3_Paint.setTextSize(100);


        F_U = new Filter_unit(context,this);

    }
    public void set_up_this(Mini_Picture_Display MPD1){
        jud = MPD1;

        F_U.set_up_this(jud);

        //input_neuron新規生成
        for(int i = 0; i < 20; i ++){
            I_N[i] = new Input_neuron();
        }


    }
    public void onDraw(Canvas canvas){

        if(F_U.stuZ < 10){
            //学習モード
            F_U.DO_Filter(canvas);
            canvas.drawText("学習中...",100,900,h3_Paint);
        }else{
            //判別モード
            for(int i = 0; i < 20; i++){
                canvas.drawText("" + I_N[i].input_value,750,1300 + i * 30,text_Paint);
            }
            F_U.judgement_1(canvas);
            //差分を比較してもっとも近い数字を出力結果とする
            double sample_sum[] = new double[10];//0～9の数字
            for(int i = 0; i < 10; i ++){
                for(int j = 0; j < 20; j++){
                    sample_sum[i] += Math.pow(I_N[j].before_avg[i] - I_N[j].input_value,2);
                }
            }

            double max = 10000000;
            int ID=0;

            //学習したデータと比較して最も差異が小さかった数字を出力結果として選択する
            for(int i = 0; i < 10   ; i++){
                if(max > sample_sum[i]){
                    max = sample_sum[i];
                    ID = i;
                }
            }
            canvas.drawText("予測結果",100,900,h3_Paint);
            if(jud.no_input() == true){
                canvas.drawText("文字を書いてください",60,1000,h3_Paint);
            }else{
                canvas.drawText(""+ID,500,1000,h1_Paint);
            }
        }

        invalidate();//キャンバスの更新
    }

}
