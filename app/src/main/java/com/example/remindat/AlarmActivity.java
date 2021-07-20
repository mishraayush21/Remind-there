package com.example.remindat;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AlarmActivity extends AppCompatActivity {

    TextView textView;
    Button btn;
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        textView = findViewById(R.id.taskDisplay);
        getSupportActionBar().hide();
        String passedArg = getIntent().getExtras().getString("arg");
        textView.setText(passedArg);
        btn = findViewById(R.id.okBtn);

        mp = MediaPlayer.create(this, R.raw.alarm);


        mp.start();
        mp.setLooping(true);





        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.stop();
                finish();
            }
        });
    }
}