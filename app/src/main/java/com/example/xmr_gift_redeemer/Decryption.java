package com.example.xmr_gift_redeemer;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Decryption {
    public String rawb64;
    public String keyb64;
    public byte[] nonce;
    public byte[] tag;

    private byte[] key;
    private byte[] ct;

    public static String plaintext;

    public Decryption(String userInput) throws RuntimeException {
        keyb64 = userInput;
        key = Base64.decode(keyb64, Base64.NO_WRAP);
        if (key.length * Byte.SIZE != 256) {
            throw new RuntimeException("Inputted key is improper length, check input");
        }
        rawb64 = QRCodeScannerActivity.ScannedData;

        byte[] raw = Base64.decode(rawb64, Base64.NO_WRAP);

        nonce = Arrays.copyOfRange(raw, 0, 16);
        tag = Arrays.copyOfRange(raw, 16, 32);
        ct = Arrays.copyOfRange(raw, 32,raw.length);

    }

    public void decrypt() throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        SecretKeySpec skeySpec = new SecretKeySpec(key,"AES");

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding","BC");
        GCMParameterSpec params = new GCMParameterSpec(tag.length * Byte.SIZE, nonce);
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, params);

        byte[] ciphertag = new byte[ct.length+tag.length];
        System.arraycopy(ct,0,ciphertag,0,ct.length);
        System.arraycopy(tag,0,ciphertag,ct.length,tag.length);

        byte[] decrypted = cipher.doFinal(ciphertag);
        plaintext = new String(decrypted);
        //Log.d("DECRYPTION", String.valueOf(plaintext.length()));
        Log.d("DECRYPTION", plaintext);
    }


}
