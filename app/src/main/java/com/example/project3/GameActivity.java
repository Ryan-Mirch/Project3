package com.example.project3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class GameActivity extends AppCompatActivity {

    GameLayout gameLayoutView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gameLayoutView = new GameLayout(this);
        setContentView(gameLayoutView);

    }
}