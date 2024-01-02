package com.example.xmr_gift_redeemer;

import static android.widget.Toast.LENGTH_LONG;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanIntentResult;
import com.journeyapps.barcodescanner.ScanOptions;
import com.example.xmr_gift_redeemer.QRCodeScannerActivity;

public class MainActivity extends AppCompatActivity {
    public ActivityResultLauncher<Intent> scanQrResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            resultData ->{
                if (resultData.getResultCode() == RESULT_OK) {
                    ScanIntentResult result = ScanIntentResult.parseActivityResult(resultData.getResultCode(), resultData.getData());

                    //this will be qr activity result
                    if (result.getContents() == null) {
                        Toast.makeText(this, "cancelled", LENGTH_LONG).show();
                        finish();

                    } else {
                        Toast.makeText(this, "Scanned: " + result.getContents(), LENGTH_LONG).show();
                        QRCodeScannerActivity.ScannedData = result.getContents();
                        // Makes new intent and changes activity
                        Intent inputIntent = new Intent(this, InputActivity.class);
                        startActivity(inputIntent);

                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        ScanOptions options = new ScanOptions();
        options.setOrientationLocked(true);
        options.setBeepEnabled(false);
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setCaptureActivity(QRCodeScannerActivity.class);
        scanQrResultLauncher.launch(new ScanContract().createIntent(this, options));
    }
}
