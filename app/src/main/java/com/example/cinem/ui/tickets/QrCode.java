package com.example.cinem.ui.tickets;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cinem.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QrCode extends AppCompatActivity {

    ImageView iv_qr;

    String qrcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qrcode);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            qrcode = extras.getString("qrcode");
        }

        iv_qr = findViewById(R.id.iv_qr);


        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            BitMatrix matrix = writer.encode(qrcode, BarcodeFormat.QR_CODE,1000,1000);

            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.createBitmap(matrix);
            iv_qr.setImageBitmap(bitmap);


        } catch (WriterException e) {
            e.printStackTrace();
        }

    }


}
