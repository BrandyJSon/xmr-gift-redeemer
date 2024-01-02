package com.example.xmr_gift_redeemer;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.Layout;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WalletActivity extends AppCompatActivity {

    public void autoRedeem(String seed, String creation, String[] TXids, String address, String node){
        Log.d("AUTO-Redemption","Attempting auto redemption to " + address + " using remote node " + node + " there are " + String.valueOf(TXids.length) + " TXids to be scanned");

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wallet);

        ScrollView manualLayout = findViewById(R.id.manualRedemptionView);
        ScrollView autoLayout = findViewById(R.id.automaticRedemptionView);

        TextView seedText = findViewById(R.id.seedText);
        TextView restoreDate = findViewById(R.id.restoreDate);
        TextView stepOne = findViewById(R.id.stepOne);
        TextView stepSeven = findViewById(R.id.stepSeven);

        ImageButton copySeed = findViewById(R.id.copyButtonSeed);
        ImageButton copyDate = findViewById(R.id.copyButtonDate);

        Switch redepmtionState = findViewById(R.id.redemptionSwitch);

        redepmtionState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    autoLayout.setVisibility(View.GONE);
                    manualLayout.setVisibility(View.VISIBLE);
                } else {
                    autoLayout.setVisibility(View.VISIBLE);
                    manualLayout.setVisibility(View.GONE);
                }
            }
        });


        stepOne.setMovementMethod(LinkMovementMethod.getInstance());
        stepSeven.setMovementMethod(LinkMovementMethod.getInstance());
        try {
            JSONObject ptJSON = new JSONObject(Decryption.plaintext);
            String seed = ptJSON.getString("Seed");
            String restore = ptJSON.getString("CreationDate");
            String[] TXids = ptJSON.getString("TXids").split(",");

            copySeed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("seed", seed);
                    clipboard.setPrimaryClip(clip);
                }
            });
            copyDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("restore", restore);
                    clipboard.setPrimaryClip(clip);
                }
            });

            seedText.setText(seed);
            restoreDate.setText(restore);
            Log.d("WALLET",Decryption.plaintext);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


    }
}
