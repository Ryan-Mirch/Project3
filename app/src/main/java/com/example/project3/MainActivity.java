package com.example.project3;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button button_howToPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_howToPlay = findViewById(R.id.button_howToPlay);
        button_howToPlay.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
               openHowToPlayActivity();
            }
        });
    }

    public void openHowToPlayActivity(){
        Intent intent = new Intent(this, HowToPlayActivity.class);
        startActivity(intent);
    }
}
