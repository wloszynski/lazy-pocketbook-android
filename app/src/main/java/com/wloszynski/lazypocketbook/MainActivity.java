package com.wloszynski.lazypocketbook;

import android.Manifest;
import android.app.ActionBar;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;

import java.io.*;

public class MainActivity extends AppCompatActivity {

    String login;
    String password;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.example_menu, menu);

        File root = new File(Environment.getExternalStorageDirectory(),"/DCIM/LAZYPOCKETBOOK");
        File filepath = new File (root, "credentials.txt");

        try {
            BufferedReader br = new BufferedReader(new FileReader(filepath));

            String line;

            line = br.readLine();
            String[] splitStr = line.split("\\s+");
            login = splitStr[0];
            password = splitStr[1];
            Toast.makeText(MainActivity.this, login + password, Toast.LENGTH_SHORT).show();
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
            Toast.makeText(MainActivity.this, "problems with finding file credentials.txt", Toast.LENGTH_SHORT).show();
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isReadStoragePermissionGranted();
        isWriteStoragePermissionGranted();

        basicFileAndStructure();

        final Button button = findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "_FORWARD_", Toast.LENGTH_SHORT).show();
            }
        });
        final Button button2 = findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "_BACKWARD_", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.item1:
                Intent intent = new Intent(this, credentials.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void basicFileAndStructure(){
        try{
            File root = new File(Environment.getExternalStorageDirectory(),"/DCIM/LAZYPOCKETBOOK");

            if(!root.exists()) {
                root.mkdir();
//                Toast.makeText(MainActivity.this, "folder created", Toast.LENGTH_SHORT).show();
            }

            File properLinks = new File (root, "credentials.txt");
            if(properLinks.exists()){
//                Toast.makeText(MainActivity.this, "PROPERLINKS FILE EXISTS", Toast.LENGTH_SHORT).show();
            }else{
//                Toast.makeText(MainActivity.this, "proplinks", Toast.LENGTH_SHORT).show();
                FileWriter writerProperLinks = new FileWriter(properLinks);
//                Toast.makeText(MainActivity.this, "writer", Toast.LENGTH_SHORT).show();

                String properLinksContent = "root@169.254.0.1 1257";
                writerProperLinks.append(properLinksContent);
                writerProperLinks.close();
//                Toast.makeText(MainActivity.this, "proplinks end", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e){
            e.printStackTrace();
//            Toast.makeText(MainActivity.this, "RESTART YOUR MOBILE", Toast.LENGTH_SHORT).show();
        }
    }

    public  boolean isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    public  boolean isWriteStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }
}
