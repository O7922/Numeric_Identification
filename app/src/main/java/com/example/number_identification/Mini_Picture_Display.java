package com.example.number_identification;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.util.AttributeSet;

//手書き文字入力フィールド
public class Mini_Picture_Display extends View {


    Paint zPaint = new Paint();
    Paint rPaint = new Paint();

    int disp_w,disp_h;
    float dot_size;

    int map[][] = new int[18][18];
    public Mini_Picture_Display(Context context,AttributeSet attrs){
        super(context,attrs);
        zPaint.setTextSize(30);
        rPaint.setColor(Color.RED);
        rPaint.setStrokeWidth(5);

        for(int i = 0; i < 18; i++){
            for(int j = 0; j < 18; j++){
                map[i][j] = 1;
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        disp_w = this.getWidth();
        disp_h = this.getHeight();
        dot_size = (float) disp_w / (float)18;
    }

    public void onDraw(Canvas canvas){
        canvas.drawColor(Color.WHITE);
        //手書き文字入力フィールドの描画
        for(int i = 0; i < 18; i++){
            for(int j = 0; j < 18; j++){
                if(map[i][j] == 1){
                    canvas.drawRect(j*dot_size,i*dot_size,j*dot_size+dot_size-1,i*dot_size+dot_size-1,zPaint);
                }
            }
        }
        invalidate();
    }

    //リセット処理
    public void clear(){
        for(int i = 0; i < 18; i++){
            for(int j = 0; j < 18; j++){
                map[i][j] = 1;
            }
        }
    }

    //ビットマップを読み込み手書き文字入力フィールドに変換する特殊な用途
    public void input_Bitmap(Bitmap bmp){
        int W = 18;
        int H = 18;

        for(int i = 0; i < H; i ++){
            for(int j = 0; j < W; j ++){
                if(bmp.getPixel(j,i) == -16777216){
                    map[i][j] = 1;
                }else{
                    map[i][j] = 0;
                }
            }
        }
    }

    //フィールドに何も書かれていないかの判定
    public boolean no_input(){
        for(int i = 0; i < 18; i ++){
            for(int j = 0; j < 18; j ++){
                if( map[i][j] == 0){
                    return false;
                }
            }
        }
        return true;
    }

    //指のスワイプによりピクセルの値を変更する
    public boolean onTouchEvent(MotionEvent event){

        int count = event.getPointerCount();

        int ex = (int)(event.getX()/dot_size);
        int ey = (int)(event.getY()/dot_size);

        if(0 <= ex && ex < 18 && 0 <= ey && ey < 18){
            map[ey][ex] = 0;
        }

        return true;
    }
}
