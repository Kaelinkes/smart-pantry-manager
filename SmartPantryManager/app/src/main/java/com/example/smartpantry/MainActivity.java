package com.example.smartpantry;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView title = new TextView(this);
        title.setText("Smart Pantry Manager");
        title.setTextSize(24);
        title.setPadding(32, 64, 32, 32);
        setContentView(title);
    }
}
