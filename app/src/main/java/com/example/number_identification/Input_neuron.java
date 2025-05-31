package com.example.number_identification;


public class Input_neuron {

    double input_value = 0;
    double before_avg[] = new double[10];//数字ごとの平均値。例えば、"7"ならこのニューロンは平均どのくらいの数値を出力するか
    int before_count[] = new int[10];
}
