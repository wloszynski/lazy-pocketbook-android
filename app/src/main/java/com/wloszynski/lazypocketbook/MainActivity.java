package com.wloszynski.lazypocketbook;

import android.Manifest;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.jcraft.jsch.*;

import java.io.*;

public class MainActivity extends AppCompatActivity {

    public static String login;
    public static String password;
    public static String username = "root";
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
//        Toast.makeText(getApplicationContext(), file.toString(), Toast.LENGTH_LONG).show();

        if(file.exists()){
//            Toast.makeText(getApplicationContext(), "EXISTS", Toast.LENGTH_LONG).show();
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
            }
        }else{
//            Toast.makeText(getApplicationContext(), "DOES NOT EXIST", Toast.LENGTH_LONG).show();
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

        final Button button = findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (was_pressed) load_data();

                            System.out.println(username + login);
                            JSch jsch = new JSch();
                            Session session = jsch.getSession(username, login, 22);
                            session.setPassword(password);
                            session.setConfig("StrictHostKeyChecking", "no");
                            System.out.println("Establishing Connection...");
                            session.connect(4000);
                            System.out.println("Connection established.");

                            Channel channel = session.openChannel("exec");
                            ((ChannelExec) channel).setCommand("cat f.txt > /dev/input/event0");
                            channel.setInputStream(null);
                            ((ChannelExec) channel).setErrStream(System.err);

                            InputStream in = channel.getInputStream();
                            channel.connect();

                            channel.disconnect();
                            session.disconnect();

                        } catch (JSchException | IOException e) {
                            openDialog();
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            }
        });

        final Button button2 = findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Thread thread2 = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if(was_pressed) load_data();

                            JSch jsch = new JSch();
                            Session session = jsch.getSession(username, login, 22);
                            session.setPassword(password);
                            session.setConfig("StrictHostKeyChecking", "no");
                            System.out.println("Establishing Connection...");
                            session.connect(4000);
                            System.out.println("Connection established.");

                            Channel channel=session.openChannel("exec");
                            ((ChannelExec)channel).setCommand("cat b.txt > /dev/input/event0");
                            channel.setInputStream(null);
                            ((ChannelExec)channel).setErrStream(System.err);

                            InputStream in=channel.getInputStream();
                            channel.connect();
                            byte[] tmp=new byte[1024];
                            while(true){
                                while(in.available()>0){
                                    int i=in.read(tmp, 0, 1024);
                                    if(i<0)break;
                                    System.out.print(new String(tmp, 0, i));
                                }
                                if(channel.isClosed()){
                                    System.out.println("exit-status: "+channel.getExitStatus());
                                    break;
                                }
                                try{Thread.sleep(1000);}catch(Exception ee){}
                            }
                            channel.disconnect();
                            session.disconnect();
                            System.out.println("DONE");
                        } catch (JSchException | IOException e) {
                            openDialog();
                            e.printStackTrace();
                        }
                    }
                });
                thread2.start();}
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.item1:
                Intent intent = new Intent(this, credentials.class);
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

}
