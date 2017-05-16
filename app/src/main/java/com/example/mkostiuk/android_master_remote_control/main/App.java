package com.example.mkostiuk.android_master_remote_control.main;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.mkostiuk.android_master_remote_control.R;
import com.example.mkostiuk.android_master_remote_control.upnp.Service;
import com.example.mkostiuk.android_master_remote_control.xml.GenerateurXml;

import org.fourthline.cling.android.AndroidUpnpServiceImpl;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class App extends AppCompatActivity {

    private Button connexion, start, stop, validation;
    private EditText keyText;
    private Service service;
    private ServiceConnection serviceConnection;
    private GenerateurXml gen;

    public void activate(Button ... buttons) {
        for (Button b : buttons)
            b.setClickable(true);
    }

    public void deactivate(Button ... buttons) {
        for (Button b : buttons)
            b.setClickable(false);
    }

    public void init() {
        connexion = (Button) findViewById(R.id.connexionButton);
        start = (Button) findViewById(R.id.startButton);
        stop = (Button) findViewById(R.id.stopButton);
        validation = (Button) findViewById(R.id.validationButton);
        keyText = (EditText) findViewById(R.id.editText);
        activate(connexion);
        deactivate(start, stop, validation);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        File dir;
        System.err.println(Build.BRAND);
        if (Build.BRAND.toString().equals("htc_europe"))
            dir = new File("/mnt/emmc/MasterRemoteControl/");
        else
            dir = new File(Environment.getExternalStorageDirectory().getPath() + "/MasterRemoteControl/");

        while (!dir.exists()) {
            dir.mkdir();
            dir.setReadable(true);
            dir.setExecutable(true);
            dir.setWritable(true);
        }

        service = new Service();
        serviceConnection = service.getService();

        gen = new GenerateurXml();

        getApplicationContext().bindService(new Intent(this, AndroidUpnpServiceImpl.class),
                serviceConnection,
                Context.BIND_AUTO_CREATE);

        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                setListeners();
            }
        }, 5000);
    }

    public void setListeners() {
        //Nothing
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);
        init();
    }

    public void onClickValidation(View view) {
        activate(start);
        deactivate(validation, connexion, stop);
    }

    public void onClickConnexion(View view) {
        activate(validation);
        deactivate(connexion, stop, start);
    }

    public void onClickStart(View view) {
        activate(stop);
        deactivate(validation, start, connexion);
    }

    public void onClickStop(View view) {
        activate(start);
        deactivate(stop, validation, connexion);
    }
}
