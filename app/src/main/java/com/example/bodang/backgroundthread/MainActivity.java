package com.example.bodang.backgroundthread;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private MyService myService;
    private Button timerSwitch;
    private TextView time;
    private ServiceConnection conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        timerSwitch = (Button)findViewById(R.id.TimerSwitch);
        time = (TextView)findViewById(R.id.Time);
        final Intent intent = new Intent(MainActivity.this, MyService.class);
        startService(intent);
        timerSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (timerSwitch.getText().toString()){
                    case "Start":
//                        myService.startTimer();
                        listenTimer();
                        timerSwitch.setText("Stop");
                        break;
                    case "Stop":
                        myService.stopTimer();
                        stopService(intent);
                        timerSwitch.setText("Start");
                        break;
                    default:
                        break;
                }
            }
        });
        conn = new ServiceConnection() {

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                myService = ((MyService.MyBinder)service).getService();

            }
        };
//        intent = new Intent(MainActivity.this, MyService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    public void listenTimer(){
        new Thread(new Runnable() {

            @Override
            public void run() {
                while(true){
                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                time.setText(Integer.toString(myService.getTime()));
                            }
                        });
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }
    Handler handler = new Handler();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
    }
}
