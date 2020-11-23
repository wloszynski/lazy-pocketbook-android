package com.wloszynski.lazypocketbook;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import com.jcraft.jsch.*;

import java.io.*;

public class MainActivity extends AppCompatActivity {

    public static String login;
    public static String password;
    boolean was_pressed = false;

    public void openDialog(){
        ConnectionDialog connectionDialog = new ConnectionDialog();
        connectionDialog.show(getSupportFragmentManager(), "Connection dialog");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.example_menu, menu);
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isReadStoragePermissionGranted();
        isWriteStoragePermissionGranted();

        File file = new File(getFilesDir()+"/credentials.txt");

        if(file.exists()){
            load_data();
        }else{
            create_file();
        }

        final Button forward_button = findViewById(R.id.button1);
        forward_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Thread forward_thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ssh_connection("forward");
                    }
                });
                forward_thread.start();
            }
        });

        final Button backward_button = findViewById(R.id.button2);
        backward_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Thread backward_thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                       ssh_connection("backward");
                    }
                });
                backward_thread.start();}
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.item1:
                Intent intent = new Intent(this, SettingsChanger.class);
                startActivity(intent);
                was_pressed = true;
                return true;
            default:
                return super.onOptionsItemSelected(item);
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


    public void create_file(){
        FileOutputStream fos = null;
        login = "192.168.1.25";
        password ="1257";
        String cred = login + " " + password;

        try {
            fos = openFileOutput("credentials.txt", MODE_PRIVATE);
            fos.write(cred.getBytes());
//                Toast.makeText(getApplicationContext(), "Saved to " + getFilesDir() + "/credentials.txt", Toast.LENGTH_SHORT).show();
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
        }
    }

    public  void load_data(){
        FileInputStream fis = null;
        try {
            fis = openFileInput("credentials.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String text;
            text = br.readLine();
            String[] text_split = text.split("\\s");
            login = text_split[0];
            password = text_split[1];
//                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
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
            was_pressed = false;
        }
    }

    public void ssh_connection(String com_name){
        String command = "";
        try {
            if (was_pressed) load_data();
            if(com_name == "forward"){
                command = "cat f.txt > /dev/input/event0";
            }else {
                command = "cat b.txt > /dev/input/event0";
            }

            JSch jsch = new JSch();
            Session session = jsch.getSession("root", login, 22);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            System.out.println("Establishing Connection...");
            session.connect(4000);
            System.out.println("Connection established.");

            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);
            channel.setInputStream(null);
            ((ChannelExec) channel).setErrStream(System.err);

            channel.connect();
            channel.disconnect();
            session.disconnect();

        } catch (JSchException e) {
            openDialog();
            e.printStackTrace();
        }
    }
}
