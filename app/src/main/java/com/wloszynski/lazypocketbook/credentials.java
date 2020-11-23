package com.wloszynski.lazypocketbook;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import java.io.*;

public class credentials extends AppCompatActivity {
String login;
String password;
String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credentials);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final TextView login_text = findViewById(R.id.login_text);
        final TextView password_text = findViewById(R.id.password_text);
        final Button save_button = findViewById(R.id.save_button);

        FileInputStream fis = null;
        try {
            fis = openFileInput("credentials.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            text = br.readLine();
//            Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        String[] strsplit = text.split("\\s");
        login = strsplit[0];
        password = strsplit[1];

        login_text.setText(login);
        password_text.setText(password);

        save_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FileOutputStream fos = null;
                login = login_text.getText().toString();
                password = password_text.getText().toString();
                String cred = login + " " + password;

                try {
                    fos = openFileOutput("credentials.txt", MODE_PRIVATE);
                    fos.write(cred.getBytes());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally{
                    if(fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    finish();
                }


            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}