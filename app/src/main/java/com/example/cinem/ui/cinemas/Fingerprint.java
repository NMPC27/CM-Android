package com.example.cinem.ui.cinemas;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cinem.R;
import com.example.cinem.ui.ReminderBroadcast;



public class Fingerprint extends AppCompatActivity {

    String cinemaName;
    String filmeId;
    String filmeName;
    String hora;
    int numTickets;
    String email;
    double preco;

    TextView fingerprint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fingerprint);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            cinemaName = extras.getString("cinemaName");
            filmeId = extras.getString("filmeId");
            filmeName = extras.getString("filmeName");
            hora = extras.getString("hora");
            numTickets = extras.getInt("numTickets");
            email = extras.getString("email");
            preco = extras.getDouble("preco");
        }

        fingerprint = findViewById(R.id.fingerprint);
        fingerprint.setText(cinemaName+filmeId+hora+numTickets+email+preco);


        Intent intent = new Intent(Fingerprint.this, ReminderBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(Fingerprint.this,0,intent,0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        long timeAtButtonClick = System.currentTimeMillis();
        Log.d("noti", String.valueOf(timeAtButtonClick));
        long tenSecMillis = 1000*10;


        alarmManager.set(AlarmManager.RTC_WAKEUP,
                timeAtButtonClick+tenSecMillis,
                pendingIntent);

    }





}