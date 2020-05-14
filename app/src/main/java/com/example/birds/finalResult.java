package com.example.birds;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.birds.Adapters.resAdapter;
import com.example.birds.Model.Result;

import java.util.ArrayList;
import java.util.List;

public class finalResult extends AppCompatActivity {

    TextView text1,text2,text3,text4,text5,cl1,cl2,cl3,cl4,cl5;
    resAdapter resAdapter;
    RecyclerView recyclerView;
    String response ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.final_res);
        Intent in = getIntent();
        response = in.getStringExtra("response");
        recyclerView = (RecyclerView) findViewById(R.id.res_rv);
//        text1 = (TextView) findViewById(R.id.text1);
//        text2 = (TextView) findViewById(R.id.text2);
//        text3 = (TextView) findViewById(R.id.text3);
//        text4 = (TextView) findViewById(R.id.text4);
//        text5 = (TextView) findViewById(R.id.text5);
//        cl1 = (TextView) findViewById(R.id.cl1);
//        cl2 = (TextView) findViewById(R.id.cl2);
//        cl3 = (TextView) findViewById(R.id.cl3);
//        cl4 = (TextView) findViewById(R.id.cl4);
//        cl5 = (TextView) findViewById(R.id.cl5);
//        text1.setText(arrOfStr[0]);
//        text2.setText(arrOfStr[2]);
//        text3.setText(arrOfStr[4]);
//        text4.setText(arrOfStr[6]);
//        text5.setText(arrOfStr[8]);
//        cl1.setText(arrOfStr[1]);
//        cl2.setText(arrOfStr[3]);
//        cl3.setText(arrOfStr[5]);
//        cl4.setText(arrOfStr[7]);
//        cl5.setText(arrOfStr[9]);

        String[] arrOfStr = response.split("@");
        List<Result> result = new ArrayList<>();
        Result res = new Result(arrOfStr[0], arrOfStr[1]);
        result.add(res);
        res = new Result(arrOfStr[2], arrOfStr[3]);
        result.add(res);
        res = new Result(arrOfStr[4], arrOfStr[5]);
        result.add(res);
        res = new Result(arrOfStr[6], arrOfStr[7]);
        result.add(res);
        res = new Result(arrOfStr[8], arrOfStr[9]);
        result.add(res);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        resAdapter = new resAdapter(this, result);
        recyclerView.setAdapter(resAdapter);

    }
}
