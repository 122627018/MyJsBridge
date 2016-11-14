package com.wxxiaomi.ming.myjsbridge;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.wxxiaomi.ming.myjsbridge.bean.UserCommonInfo;

public class SecondActivity extends AppCompatActivity {

    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();

//                UserCommonInfo userInfo = new UserCommonInfo();
//                userInfo.emname = "122627018";
//                userInfo.head = "/asd.jpg";
//                userInfo.id = 25;
//                userInfo.name = "王浩明";
//                String json = new Gson().toJson(userInfo);
//                intent.putExtra("value",json);

//                intent.putExtra("value","{\"name\":\"HaoMing\"}");

                intent.putExtra("value","{\"refresh\":\"true\"}");

                setResult(1,intent);
                finish();

            }
        });
    }
}
