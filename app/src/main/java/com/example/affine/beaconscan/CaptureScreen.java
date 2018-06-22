package com.example.affine.beaconscan;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class CaptureScreen extends AppCompatActivity {
    public TextView P1;
    public EditText Scenario;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.capture_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Scenario = findViewById(R.id.scenario);
        findViewById(R.id.P1).setOnClickListener(onClickListener);
        findViewById(R.id.P2).setOnClickListener(onClickListener);
        findViewById(R.id.P3).setOnClickListener(onClickListener);
        findViewById(R.id.P4).setOnClickListener(onClickListener);
        findViewById(R.id.P5).setOnClickListener(onClickListener);
        findViewById(R.id.P6).setOnClickListener(onClickListener);
        findViewById(R.id.P7).setOnClickListener(onClickListener);
        findViewById(R.id.P8).setOnClickListener(onClickListener);
        findViewById(R.id.P9).setOnClickListener(onClickListener);
        findViewById(R.id.P10).setOnClickListener(onClickListener);
        findViewById(R.id.P11).setOnClickListener(onClickListener);
        findViewById(R.id.P12).setOnClickListener(onClickListener);
        findViewById(R.id.P13).setOnClickListener(onClickListener);
        findViewById(R.id.P14).setOnClickListener(onClickListener);
        findViewById(R.id.P15).setOnClickListener(onClickListener);
        findViewById(R.id.P16).setOnClickListener(onClickListener);
        findViewById(R.id.P17).setOnClickListener(onClickListener);
        findViewById(R.id.P18).setOnClickListener(onClickListener);
        findViewById(R.id.P19).setOnClickListener(onClickListener);
        findViewById(R.id.P20).setOnClickListener(onClickListener);
        findViewById(R.id.P21).setOnClickListener(onClickListener);
        findViewById(R.id.P22).setOnClickListener(onClickListener);
        findViewById(R.id.P23).setOnClickListener(onClickListener);
        findViewById(R.id.P24).setOnClickListener(onClickListener);
        findViewById(R.id.P25).setOnClickListener(onClickListener);
        findViewById(R.id.P26).setOnClickListener(onClickListener);
        findViewById(R.id.P27).setOnClickListener(onClickListener);
        findViewById(R.id.P28).setOnClickListener(onClickListener);
        findViewById(R.id.P29).setOnClickListener(onClickListener);
        findViewById(R.id.P30).setOnClickListener(onClickListener);
        findViewById(R.id.P31).setOnClickListener(onClickListener);
        findViewById(R.id.P32).setOnClickListener(onClickListener);
        findViewById(R.id.P33).setOnClickListener(onClickListener);
        findViewById(R.id.P34).setOnClickListener(onClickListener);
        findViewById(R.id.P35).setOnClickListener(onClickListener);
        findViewById(R.id.P36).setOnClickListener(onClickListener);
        findViewById(R.id.P37).setOnClickListener(onClickListener);
        findViewById(R.id.P38).setOnClickListener(onClickListener);
        findViewById(R.id.P39).setOnClickListener(onClickListener);
        findViewById(R.id.P40).setOnClickListener(onClickListener);
        findViewById(R.id.P41).setOnClickListener(onClickListener);
        findViewById(R.id.P42).setOnClickListener(onClickListener);
        findViewById(R.id.P43).setOnClickListener(onClickListener);
        findViewById(R.id.P44).setOnClickListener(onClickListener);
        findViewById(R.id.P45).setOnClickListener(onClickListener);
        findViewById(R.id.P46).setOnClickListener(onClickListener);
        findViewById(R.id.P47).setOnClickListener(onClickListener);
        findViewById(R.id.P48).setOnClickListener(onClickListener);
        findViewById(R.id.P49).setOnClickListener(onClickListener);
        findViewById(R.id.P50).setOnClickListener(onClickListener);
        findViewById(R.id.P51).setOnClickListener(onClickListener);
        findViewById(R.id.P52).setOnClickListener(onClickListener);
        findViewById(R.id.P53).setOnClickListener(onClickListener);
        findViewById(R.id.P54).setOnClickListener(onClickListener);
        findViewById(R.id.P55).setOnClickListener(onClickListener);
        findViewById(R.id.P56).setOnClickListener(onClickListener);
        findViewById(R.id.P57).setOnClickListener(onClickListener);
        findViewById(R.id.P58).setOnClickListener(onClickListener);
        findViewById(R.id.P59).setOnClickListener(onClickListener);
        findViewById(R.id.P60).setOnClickListener(onClickListener);
        findViewById(R.id.P61).setOnClickListener(onClickListener);
        findViewById(R.id.P62).setOnClickListener(onClickListener);

    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent myIntent = new Intent(CaptureScreen.this, MainActivity.class);
            myIntent.putExtra("Point", getId(v));
            myIntent.putExtra("Scenario", Scenario.getText().toString());
            CaptureScreen.this.startActivity(myIntent);
        }
    };

    private String getId(View view) {
        TextView textView = (TextView) view;
        return textView.getText().toString();
    }
}
