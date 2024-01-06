package com.example.xmr_gift_redeemer;

import static java.time.Instant.now;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import monero.daemon.MoneroDaemon;
import monero.daemon.MoneroDaemonRpc;

public class WalletActivity extends AppCompatActivity {
    private static String[] TXids;
    private static String seed;
    private static String creation;
    private static String node = "http://selsta2.featherwallet.net:18081";

    private static String uri;
    private static String address;
    private static final String tag = "AUTO-Redepmtion";

    private static long restoreHeight;
    public void autoRedeem(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    //Test values
                    seed = "deftly large tirade gumball android leech sidekick opened iguana voice gels focus poaching itches network espionage much jailed vaults winter oatmeal eleven science siren winter";
                    address = "9wQ4NxV7aUdYUDGUmDWeSMADtxZPGRqCu9uQLCYcvgM4YWfeL42hUq7axrsAdE9npJJS5mMeUxRpgcw88ZjjUeHVQe3q2dD";



                    Log.d(tag,"Attempting auto redemption to " + address + " using remote node " + node + " there are " + String.valueOf(TXids.length) + " TXids to be scanned");
                    MoneroDaemon daemon = new MoneroDaemonRpc(node,"","");
                    long blockHeight = daemon.getHeight();
                    Log.d(tag,"Connected Succesfully to node, blockheight is " + String.valueOf(blockHeight));


                    //Log.d(tag, "Current wallet balance is :" + String.valueOf(walletFull.getBalance()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
        Log.d(tag,"waiting");


    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wallet);

        ScrollView manualLayout = findViewById(R.id.manualRedemptionView);
        manualLayout.setVisibility(View.VISIBLE);
        ScrollView autoLayout = findViewById(R.id.automaticSendView);

        TextView seedText = findViewById(R.id.seedText);
        TextView restoreDate = findViewById(R.id.restoreDate);
        TextView stepOne = findViewById(R.id.stepOne);
        TextView stepSeven = findViewById(R.id.stepSeven);

        ImageButton copySeed = findViewById(R.id.copyButtonSeed);
        ImageButton copyDate = findViewById(R.id.copyButtonDate);

        Switch redepmtionState = findViewById(R.id.redemptionSwitch);
        Button autoRedeemButton = findViewById(R.id.autoRedemptionButton);
        Button advancedAutoRedeemButton = findViewById(R.id.advancedRedemptionButton);
        EditText addressInput = findViewById(R.id.xmrAddressInput);
        EditText nodeInput = findViewById(R.id.nodeInput);
        autoLayout.setVisibility(View.GONE);

        Button uriRedeem = findViewById(R.id.redeemButton);
        Button cakeWallet = findViewById(R.id.cakeWalletButton);
        Button moneroCom = findViewById(R.id.moneroComButton);
        Button cakeWallet2 = findViewById(R.id.cakeWalletButton2);
        Button moneroCom2 = findViewById(R.id.moneroComButton2);
        cakeWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("market://details?id=com.cakewallet.cake_wallet")));
            }
        });
        cakeWallet2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("market://details?id=com.cakewallet.cake_wallet")));
            }
        });
        moneroCom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.monero.app")));
            }
        });

        moneroCom2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.monero.app")));
            }
        });




        ScrollView redeemLayout = findViewById(R.id.automaticRedeemView);
        redeemLayout.setVisibility(View.GONE);

        redepmtionState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    redeemLayout.setVisibility(View.GONE);
                    manualLayout.setVisibility(View.VISIBLE);
                    redepmtionState.setText("Mode: Manual");
                } else {
                    redeemLayout.setVisibility(View.VISIBLE);
                    manualLayout.setVisibility(View.GONE);
                    redepmtionState.setText("Mode: Auto");
                }
            }
        });


        stepOne.setMovementMethod(LinkMovementMethod.getInstance());
        stepSeven.setMovementMethod(LinkMovementMethod.getInstance());
        try {
            JSONObject ptJSON = new JSONObject(Decryption.plaintext);
            seed = ptJSON.getString("Seed");
            creation = ptJSON.getString("CreationDate");
            TXids = ptJSON.getString("TXids").split(",");

            Thread blockHeightGrab = new Thread(new Runnable() {
                @Override
                public void run() {
                    try  {
                        MoneroDaemon daemon = new MoneroDaemonRpc(node,"","");
                        long blockHeight = daemon.getHeight();
                        Log.d(tag,"Connected Succesfully to node, blockheight is " + String.valueOf(blockHeight));
                        LocalDate past = LocalDate.parse(creation, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
                        past.minusDays(5);
                        long restoreTime = past.atStartOfDay().toEpochSecond(ZoneOffset.UTC);

                        restoreHeight = blockHeight - (now().getEpochSecond() - restoreTime) / 120;
                        WalletActivity.uri += "&height=" + String.valueOf(restoreHeight);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            blockHeightGrab.start();
            uriRedeem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                     uri = "monero-wallet:?seed=" + seed.replace(' ','+');
                    Log.d(tag,"Length of TXids[0] is " + TXids[0].length());
                     if (TXids[0].length() > 0){
                        uri += "&txid=";
                        for (String txid: TXids){
                            uri+=txid + ";";
                        }
                    }else {
                        try {
                            blockHeightGrab.join();
                        }  catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    Log.d(tag,"URI is set to : " + uri);

                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
                }
            });

            autoRedeemButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (addressInput.getText().length() == 95){
                        autoRedeemButton.setError("");
                        address = String.valueOf(addressInput.getText());
                        autoRedeem();
                    } else {
                        autoRedeemButton.setError("Improper monero address, check to make sure only the address was entered and it is of proper length");
                    }
                }
            });

            advancedAutoRedeemButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (addressInput.getText().length() == 95 && URLUtil.isValidUrl(String.valueOf(nodeInput.getText()))){
                        advancedAutoRedeemButton.setError("");
                        autoRedeemButton.setError("");
                        address = String.valueOf(addressInput.getText());
                        node = String.valueOf(nodeInput.getText());
                        autoRedeem();
                    } else if (!URLUtil.isValidUrl(String.valueOf(nodeInput.getText()))){
                        advancedAutoRedeemButton.setError("Improper node url, make sure the node you specified is a valid clearnet url. Use manual mode if you want to use a tor node");
                    } else {
                        autoRedeemButton.setError("Improper monero address, check to make sure only the address was entered and it is of proper length");
                    }
                }
            });
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
                    ClipData clip = ClipData.newPlainText("restore", creation);
                    clipboard.setPrimaryClip(clip);
                }
            });

            seedText.setText(seed);
            try{
                blockHeightGrab.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            restoreDate.setText(String.valueOf((int) restoreHeight));
            Log.d("WALLET",Decryption.plaintext);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


    }
}
