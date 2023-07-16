    package com.example.troika2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;

    public class MainActivity extends AppCompatActivity {
        final static int REQUEST_OPEN_DUMP = 1;
        final static String INTENT_READ_DUMP = "com.example.troika2.INTENT_READ_DUMP";

        protected FloatingActionButton btnLoad;
        protected FloatingActionButton btnWrite;
        protected TextView info;

        protected NfcAdapter nfcAdapter;
        protected Dump dump;
        protected boolean writeMode = false;
        protected ProgressDialog pendingWriteDialog;


        @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_main);
            info = (TextView) findViewById(R.id.textView);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            NfcManager nfcManager = (NfcManager)getSystemService(Context.NFC_SERVICE);
            nfcAdapter = nfcManager.getDefaultAdapter();
            if (nfcAdapter == null){
                info.setText(R.string.error_no_nfc);
            }

            if (nfcAdapter != null && !nfcAdapter.isEnabled()){
                info.setText(R.string.error_nfc_is_disabled);
            }

            pendingWriteDialog = new ProgressDialog(MainActivity.this);
            pendingWriteDialog.setIndeterminate(true);
            pendingWriteDialog.setMessage("Waiting for card...");
            pendingWriteDialog.setCancelable(true);
            pendingWriteDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    writeMode = false;
                }
            });

            btnWrite = (FloatingActionButton) findViewById(R.id.btn_write);
            btnWrite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), DumpListActivity.class);
                    startActivityForResult(intent, REQUEST_OPEN_DUMP);
                }
            });

            Intent startIntent = getIntent();
            if (startIntent != null && startIntent.getAction().equals(NfcAdapter.ACTION_TECH_DISCOVERED)){
                handleIntent(startIntent);
            }
        }

        private void handleIntent(Intent intent) {
            info.setText("");
            File dumpsDir = getApplicationContext().getExternalFilesDir(null);
            String action = intent.getAction();
            boolean shouldSave = false;
            try {
                if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)){
                    Tag tag = intent.getParcelableExtra(nfcAdapter.EXTRA_TAG);
                    if (writeMode && dump != null){
                        pendingWriteDialog.hide();
                        info.append("Writing to card...");
                        dump.write(tag);
                    }
                }
            }
        }
    }