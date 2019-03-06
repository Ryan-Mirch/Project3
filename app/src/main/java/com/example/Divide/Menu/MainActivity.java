package com.example.Divide.Menu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.Divide.Credits.CreditsActivity;
import com.example.Divide.Game.GameActivity;
import com.example.Divide.Game.GameLogic;
import com.example.Divide.HowToPlay.HowToPlayActivity;
import com.example.Divide.R;

public class MainActivity extends AppCompatActivity {
    private Button button_howToPlay;
    private Button button_newGame;
    private Button button_continue;
    private Button button_credits;

    @Override
    protected void onPostResume() {
        super.onPostResume();
        button_continue.setEnabled(GameLogic.getGamePlaying());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_howToPlay = findViewById(R.id.button_howToPlay);
        button_continue = findViewById(R.id.button_continue);
        button_newGame = findViewById(R.id.button_newGame);
        button_credits = findViewById(R.id.button_credits);

        button_credits.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openCreditsActivity();
            }
        });

        button_howToPlay.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
               openHowToPlayActivity();
            }
        });

        button_newGame.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openGameActivity(true);
            }
        });

        button_continue.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openGameActivity(false);
            }
        });
    }

    public void openHowToPlayActivity(){
        Intent intent = new Intent(this, HowToPlayActivity.class);
        startActivity(intent);
    }

    public void openCreditsActivity(){
        Intent intent = new Intent(this, CreditsActivity.class);
        startActivity(intent);
    }

    public void openGameActivity(boolean startNewGame){
        Intent intent = new Intent(this, GameActivity.class);
        GameLogic.setStartNewGame(startNewGame);
        startActivity(intent);
    }
}
