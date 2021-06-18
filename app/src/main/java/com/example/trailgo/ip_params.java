package com.example.trailgo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ip_params extends Activity {

    TextView ip_address;
    Button ip_button;
    EditText ip_serv, num;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ip_params);

        ip_address = (TextView)findViewById(R.id.textView6);
        ip_button = (Button)findViewById(R.id.ip_button);
        ip_serv  = (EditText)findViewById(R.id.ip_serv);
        num = (EditText)findViewById(R.id.num2);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8), (int)(height*.7));
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;
        getWindow().setAttributes(params);

    }

    public void ip_params(View view){
        String text = ip_serv.getText().toString();
        intent = new Intent(getApplicationContext(),MainActivity.class);
        intent.putExtra("EditTextValue", num.getText().toString());
        intent.putExtra("EditIpAddressValue",text);
        startActivity(intent);
    }

}