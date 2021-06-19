package com.example.trailgo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    String ADRESSE = "";
    public static final int PORT = 48569;

    private Socket mSocket;
    private BufferedReader mReader;
    private PrintWriter mWriter;
    private EditText etRequete;
    private TextView tvReponse;
    private String mTexte;

    TextView num_dossard, test;

    Button button;

    FusedLocationProviderClient fusedLocationProviderClient;

    private boolean mConnexionActive = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etRequete = (EditText)findViewById(R.id.etRequete);
        tvReponse = (TextView) findViewById(R.id.tvReponse);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        button = (Button) findViewById(R.id.button);
        num_dossard = (TextView) findViewById(R.id.num_dossard);
        test = (TextView)findViewById(R.id.test);

        //enlever le titre de l'application
        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e) {
        }

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;
        getWindow().setAttributes(params);

        num_dossard.setText(getIntent().getStringExtra("EditTextValue"));

        ADRESSE = getIntent().getStringExtra("EditIpAddressValue");
        test.setText(ADRESSE);
    }

    public void info(View view) {
        Intent i = new Intent(getApplicationContext(), PopActivity.class);
        startActivity(i);
    }

    public void ip(View view){
        Intent i = new Intent(getApplicationContext(), ip_params.class);
        startActivity(i);

    }


    private void connecter() {
        mConnexionActive = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mSocket = new Socket(ADRESSE, PORT);
                    mReader = new BufferedReader(
                            new InputStreamReader(mSocket.getInputStream()));
                    mWriter = new PrintWriter(
                            new OutputStreamWriter(mSocket.getOutputStream()));
                } catch (IOException ioe) {
                    mTexte = ioe.getMessage();
                    afficher();
                }
            }
        }).start();
    }


    public void check (View view){
        if(num_dossard.getText().length() > 0){
            ouvrir_port();
        } else {
            Toast.makeText(this, "Renseignez votre numéro de dossard !", Toast.LENGTH_SHORT).show();
        }
    }


    public void ouvrir_port (){
        final String requete = "La course va debuter";
        Toast.makeText(this, "Ouverture des ports !", Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!mConnexionActive) {
                    connecter();

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ie) {

                    }
                }


                mWriter.println(requete);
                mWriter.flush();
                try {
                    // réception de la réponse
                    char buf[] = new char [20];
                    int i =mReader.read(buf);
                    mTexte = new String(buf);
                } catch (IOException ioe) {
                    mTexte = ioe.getMessage();
                }
                afficher();
            }
        }).start();
    }

    private void afficher() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mTexte.matches("go(.*)")) {
                    gps();
                }
            }
        });
    }

    public void gps (){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                        Date date = new Date();
                        String dateformatted = dateFormat.format(date);
                        Location location = task.getResult();
                        if (location != null) {
                            try {
                                Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                etRequete.setText(String.valueOf("Num dossard : " + num_dossard.getText() + " Heure : " + dateformatted + " Lat : " + addresses.get(0).getLatitude() + " long : " + addresses.get(0).getLongitude()));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }});

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (!mConnexionActive) {
                            connecter();
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException ie) {

                            }
                        }


                        mWriter.println(etRequete.getText());
                        mWriter.flush();
                        try {
                            // réception de la réponse
                            char buf[] = new char [20];
                            int i =mReader.read(buf);
                            mTexte = new String(buf);

                        } catch (IOException ioe) {
                            mTexte = ioe.getMessage();
                        }
                        afficher();
                    }


                }).start();
            }
        },0,5000);
    }
}
