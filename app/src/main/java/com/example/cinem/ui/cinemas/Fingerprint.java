package com.example.cinem.ui.cinemas;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.example.cinem.R;
import com.example.cinem.ui.ReminderBroadcast;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.Executor;


public class Fingerprint extends AppCompatActivity {

    String cinemaName;
    String filmeId;
    String filmeName;
    String hora;
    int numTickets;
    String email;
    double preco;

    Button btn_fp,btn_fppin;

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

        btn_fp = findViewById(R.id.btn_fp);
        btn_fppin = findViewById(R.id.btn_fppin);

        checkBioMetricSupported();

        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(Fingerprint.this,executor,new BiometricPrompt.AuthenticationCallback() {

            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString){
                super.onAuthenticationError(errorCode,errString);
                Toast.makeText(Fingerprint.this,"Auth error: "+ errString,Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result){
                super.onAuthenticationSucceeded(result);
                Toast.makeText(Fingerprint.this,"Auth succeeded",Toast.LENGTH_SHORT).show();
                insertDBticket();
                notification();
            }
            @Override
            public void onAuthenticationFailed(){
                super.onAuthenticationFailed();
                Toast.makeText(Fingerprint.this,"Auth failed",Toast.LENGTH_SHORT).show();
            }

        });

        // for fingerprint only
        btn_fp.setOnClickListener(view->{
            BiometricPrompt.PromptInfo.Builder promptInfo = dialogMetric();
            promptInfo.setNegativeButtonText("Cancel");
            biometricPrompt.authenticate(promptInfo.build());
        });
        // for button fingerprint or pattern or pin
        btn_fppin.setOnClickListener(view -> {
            BiometricPrompt.PromptInfo.Builder promptInfo = dialogMetric();
            promptInfo.setDeviceCredentialAllowed(true);
            biometricPrompt.authenticate(promptInfo.build());
        });

    }

    private void insertDBticket() {

        Map<String, Object> data = new HashMap<>();
        data.put("cinema", cinemaName);
        data.put("filme", filmeId);
        data.put("name", filmeName);
        data.put("numTickets", numTickets);
        data.put("schedule", hora);
        data.put("qrCode","Cinema: "+cinemaName+", Filme: "+filmeId+", Hora: "+hora+", NumTickets: "+numTickets+", email: "+email);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("tickets").document(email).collection("tickets").add(data);

        Map<String, Object> data2 = new HashMap<>();
        data2.put("notification", filmeName+" starts at "+hora+" in "+cinemaName);
        db.collection(email).add(data2);

    }

    private void notification(){
        Intent intent = new Intent(Fingerprint.this, ReminderBroadcast.class).putExtra("filmeName",filmeName);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(Fingerprint.this,0,intent,0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        long timeAtButtonClick = System.currentTimeMillis();
        Log.d("noti", String.valueOf(timeAtButtonClick));
        long tenSecMillis = 1000*10;

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Lisbon"));
        String[] arrOfStr = hora.split("h", 2);

        Date sessao_time = new Date(cal.get(Calendar.YEAR)-1900,cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH),Integer.parseInt(arrOfStr[0]),Integer.parseInt(arrOfStr[1]));

        long seconds_diff = (sessao_time.getTime()-timeAtButtonClick) - 5*60;

        Log.d("tag", String.valueOf(seconds_diff));

        alarmManager.set(AlarmManager.RTC_WAKEUP,
                timeAtButtonClick+seconds_diff,
                pendingIntent);
    }

    BiometricPrompt.PromptInfo.Builder dialogMetric() {

        return new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Login")
                .setSubtitle("Login using your biometric credential");

    }

    void enableButton(boolean enable){

        btn_fp.setEnabled(enable);
        btn_fppin.setEnabled(true);
    }

    void enableButton(boolean enable,boolean enroll){

        enableButton(enable);
        if(!enroll) return;

        Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);

        enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                BiometricManager.Authenticators.BIOMETRIC_STRONG
                        | BiometricManager.Authenticators.BIOMETRIC_WEAK);

        startActivity(enrollIntent);

    }

    private void checkBioMetricSupported() {

        String info = "";

        BiometricManager manager = BiometricManager.from(this);

        switch (manager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK
                | BiometricManager.Authenticators.BIOMETRIC_STRONG))
        {
            case BiometricManager.BIOMETRIC_SUCCESS:
                info = "App can authenticate using biometrics";
                enableButton(true);
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                info = "No biometric features available on this device";
                enableButton(false);
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                info = "Biometric features are currently unavailable";
                enableButton(false);
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                info = "Need register at least one fingerprint.";
                enableButton(false,true);
                break;
            default:
                info = "Unknown cause";
                enableButton(false);
                break;
        }

        TextView txinfo = findViewById(R.id.tx_info);
        txinfo.setText(info);

    }



}