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

public class Filter_unit {
    Paint zPaint = new Paint();
    Paint rPaint = new Paint();
    Paint text_Paint = new Paint();
    Mini_Picture_Display jud;
    Main_View mView;
    Bitmap input_data;//手書き数字のサンプル画像1000枚
    Bitmap sample_meta, sample;

    int getAVG_COUNT = 0;
    int stuX=0,stuY=0,stuZ=0;//stuX、stuYは数字ごとの10×10=100枚のサンプルのどれを参照するか、stuZは1～9のどの数字を参照するか
    int filter[][][] = {//以下の3×3の4つのフィルターを使用
            {       {1, 1, 1},
                    {0, 0, 0},
                    {1, 1, 1}
            },
            {       {1, 0, 1},
                    {1, 0, 1},
                    {1, 0, 1}
            },
            {       {0, 1, 1},
                    {1, 0, 1},
                    {1, 1, 0}
            },
            {       {1, 1, 0},
                    {1, 0, 1},
                    {0, 1, 1}
            }
    };

    int filter_count[];

    public Filter_unit(Context context0,Main_View mv){

        mView = mv;
        zPaint.setTextSize(30);
        rPaint.setColor(Color.RED);
        rPaint.setStrokeWidth(5);
        zPaint.setARGB(255,0,0,0);
        text_Paint.setTextSize(30);
        text_Paint.setColor(Color.BLUE);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        sample_meta = BitmapFactory.decodeResource(context0.getResources(), R.drawable.sample, options);


        sample = Bitmap.createScaledBitmap(sample_meta,1800,180,true);

        input_data = Bitmap.createBitmap(sample,0,0,18,18);
    }

    //インスタンス生成とタイミングをずらして設定したい項目を設定する関数
    public void set_up_this(Mini_Picture_Display MPD0){
        jud = MPD0; filter_count = new int[4];

        //フィルターそれぞれにいくつ0があるか調べて記憶する
        for(int fil_page = 0; fil_page < filter_count.length;fil_page++){
            int count = 0;
            for(int i = 0;i < 3; i ++){
                for(int j = 0;j < 3; j ++){
                    if(filter[fil_page][i][j] == 0){
                        count ++;
                    }
                }
            }
            filter_count[fil_page] = count;
        }
    }

    //このクラスのメイン処理。
    public void DO_Filter(Canvas canvas){



        if(stuZ < 10){
            //重み平均値入手
            //学習対処のサンプルの任意の一枚を取得
            input_data = Bitmap.createBitmap(sample,stuZ*180+stuX*18,stuY*18,18,18);
            jud.input_Bitmap(input_data);//手書き文字入力フィールドにサンプルを入力
            stuX ++;

            //フィルタ処理をさせる
            judgement_1(canvas);

            //インプットニューロンごとの平均値の再計算
            for(int i = 0; i < 20; i++){
                canvas.drawText("" + mView.I_N[i].before_avg[stuZ],750,1300 + i * 30,text_Paint);

                //(現在の平均値 * 現在までのサンプル数 + 新規の入力値) / (現在までのサンプル数 + 1) = 新しい平均値
                mView.I_N[i].before_avg[stuZ] =
                        (mView.I_N[i].before_avg[stuZ] * mView.I_N[i].before_count[stuZ] + mView.I_N[i].input_value) /
                        (mView.I_N[i].before_count[stuZ] + 1);
                mView.I_N[i].before_count[stuZ] ++;
            }

            if(stuX > 9){
                stuX = 0;
                stuY ++;
            }
            if(stuY > 9){
                stuY = 0;
                stuZ ++;
            }
        }
        if(getAVG_COUNT <= 1000){
            getAVG_COUNT ++;
        }

    }

    //フィルター諸々計算
    public void judgement_1(Canvas canvas){

        //input_neuronの入力値初期化
        for(int i = 0; i < 20; i ++){
            mView.I_N[i].input_value = 0;
        }

        //特徴量マップ(数字ごとにどの図形がどの位置にどのくらいあるかを集計する)
        double Feature_value_Map[][][] = new double[4][18][18];

        //フィルター計算   インプットニューロンの0～3番目は4つのフィルターがどのくらい反応したかの合計値を入れる
        int count = 0;
        for(int f = 0; f < 4;f++){

            double map_sum = 0;

            for(int i = 1; i < 17; i++){
                for(int j = 1; j < 17; j++){

                    count = 0;
                    //特徴量抽出
                    for(int i0 = 0; i0 < 3; i0++){
                        for(int j0 = 0; j0 < 3; j0++){
                            if(filter[f][i0][j0] == 0 && jud.map[i + i0 -1][j + j0 -1] == 0){
                                count ++;
                            }
                        }
                    }
                    //0～1の範囲で特徴量を抽出
                    double percentage = Math.pow((double)count / (double)filter_count[f],3);
                    //特徴量mapに記憶
                    Feature_value_Map[f][i][j] = percentage;
                    map_sum += percentage;
                }
            }

            //インプットニューロンにフィルターの総抽出量を入力。必要に応じで除算で量を減らす
            mView.I_N[f].input_value = Math.pow(map_sum / 289 * 16,2);//17 * 17の最大値が289で、位置情報ニューロンに負けないように * 4
        }



        int input_count = 4;//インプットニューロンの入力は4番目から開始
        //インプットニューロンの4～19には、4つのフィルターが手書き文字入力フィールドの右上、右下、左上、左下のどこでどのくらい反応したかを入力
        for(int f = 0; f < 4; f++){

            //右上、右下、左上、左下のそれぞれの9×9マスで平均値を取っていく
            for(int i0 = 0; i0 < 2; i0 ++){
                for(int j0 = 0; j0 < 2; j0 ++){

                    double sum = 0;
                    int sum_count = 0;

                    for(int i = 0; i < 9; i ++){
                        for(int j = 0; j < 9; j ++){
                            if(Feature_value_Map[f][i0 * 9 + i][j0 * 9 +  j] != 0){
                                sum += Feature_value_Map[f][i0 * 9 + i][j0 * 9 +  j];
                                sum_count ++;
                            }
                        }
                    }

                    double mini_map_AVG = (Math.pow(sum/sum_count,2) * 40);
                    zPaint.setARGB(0,0,0,0);
                    //抽出量が小さい(0.2以下)のタイルは打消しとして使う
                    if(mini_map_AVG > 0.2){

                        //インプットニューロンに値を入力
                        mView.I_N[input_count].input_value = mini_map_AVG;

                        int Alpha = (int)(mini_map_AVG * 64);

                        if(Alpha > 255){
                            Alpha = 255;
                        }
                        zPaint.setARGB(Alpha,0,0,0);
                    }else if(0.2 >= mini_map_AVG){

                        //インプットニューロンに値を入力
                        mView.I_N[input_count].input_value = (mini_map_AVG - 0.2) * 50;

                        int Alpha = (int)((mini_map_AVG - 0.2) * 50 * 25);

                        if(Alpha > 255){
                            Alpha = 255;
                        }

                        zPaint.setARGB(Alpha,255,0,0);
                    }



                    canvas.drawRect(900 + j0*100,f * 300 + i0*100,900 + j0*100 + 100,f * 300 + i0*100 + 100,zPaint );
                    canvas.drawText("" + mini_map_AVG,900 + j0*100,f * 300 + i0*100,text_Paint);

                    input_count ++;
                }
            }
        }

        //次は抽出平均値の蓄積
        //ひとまずここまでだけ流せば抽出量平均は出せる

        //(前回までの平均値*計算に使ったサンプルの数量+(新しく加える数)/計算に使ったサンプル量+1)
    }
}
