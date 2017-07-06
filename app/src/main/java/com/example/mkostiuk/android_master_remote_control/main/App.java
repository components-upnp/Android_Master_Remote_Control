package com.example.mkostiuk.android_master_remote_control.main;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.mkostiuk.android_master_remote_control.R;
import com.example.mkostiuk.android_master_remote_control.upnp.Service;
import com.example.mkostiuk.android_master_remote_control.xml.GenerateurXml;

import org.fourthline.cling.android.AndroidUpnpServiceImpl;
import org.w3c.dom.Document;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import xdroid.toaster.Toaster;

public class App extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_STORAGE = 1;

    private Button connexion, start, stop;
    private EditText keyText;
    private Service service;
    private ServiceConnection serviceConnection;
    private GenerateurXml gen;
    private String key;

    public void activate(Button ... buttons) {
        for (Button b : buttons)
            b.setClickable(true);
    }

    public void deactivate(Button ... buttons) {
        for (Button b : buttons)
            b.setClickable(false);
    }

    public void setButtonActivityLayout(){
        connexion = (Button) findViewById(R.id.connexionButton);
        start = (Button) findViewById(R.id.startButton);
        stop = (Button) findViewById(R.id.stopButton);
    }

    public void init() {

        setButtonActivityLayout();

       // keyText = (EditText) findViewById(R.id.editText);
        activate(connexion);
        deactivate(start, stop);

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
        //Vérification que l'autorisation d'accès au système de stockage est accrodée
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                //Cela signifie que la permission à déjà était
                //demandé et l'utilisateur l'a refusé
                //Vous pouvez aussi expliquer à l'utilisateur pourquoi
                //cette permission est nécessaire et la redemander
                Toaster.toast("Vous avez refusé l'accés au Stockage, fermeture");
                finish();
            } else {
                //Sinon demander la permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_STORAGE);
            }
        }
        else {
            //Permission déjà accrodée
            init();
        }
    }

    public void onClickValidation(View view) {
        key = String.valueOf(keyText.getText());
        setContentView(R.layout.activity_app);
        setButtonActivityLayout();
        activate(start);
        deactivate( connexion, stop);
    }

    public void onClickConnexion(View view) {
        activate();
        deactivate(connexion, stop, start);
        setContentView(R.layout.key_layout);
        keyText = (EditText) findViewById(R.id.editText);
    }

    public void onClickStart(View view) throws TransformerException, ParserConfigurationException {
        activate(stop);
        deactivate( start, connexion);

        String com = genCommande();

        System.err.println(com);

        service.getMasterCommandService().getManager().getImplementation()
                .setCommande(com);
    }

    public void onClickStop(View view) throws TransformerException, ParserConfigurationException {
        activate(start);
        deactivate(stop, connexion);
        service.getMasterCommandService().getManager().getImplementation()
                .setCommande(genCommande());
    }

    public String genCommande() throws TransformerException, ParserConfigurationException {
        return  gen.getDocXml(service.getUdn().toString(),"CENTRE",key);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // La permission est garantie on initialise les services et boutons
                    init();
                } else {
                    Toaster.toast("Permission refusée, fermeture");
                    finish();
                }
                return;
            }
        }
    }
}
