package com.example.mbclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private Handler     handler;

    public EditText     mStatusWordRes;
    public EditText     mControlWordRes;
    public TextView     mCycleReadIndication;

    public String       IP =            "80.50.0.37";   //IP of Modbus TCP server
    public String       port =          "502";          //Default Modbus TCP port
    public String       statusWord =    "50";           //Holding register 40050
    public String       controlWord =   "1";            //Holding register 40001

    public boolean      cycleRead = false;


    class CycleReadModbus implements Runnable{
        boolean allways = true;

        @Override
        public void run() {
            while (allways){
                try {
                    if (cycleRead) {
                        handler.obtainMessage(1).sendToTarget();
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException ie){
                    Log.d("debug", ie.getMessage());
                }
            }
        }
    }

    class UpdateModbusWords extends Handler{

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1){
                readDataFromModbus();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStatusWordRes =        (EditText) findViewById(R.id.SW_editText);
        mControlWordRes =       (EditText) findViewById(R.id.CW_editText);
        mCycleReadIndication =  (TextView) findViewById(R.id.cycle_read_indication_textView);

        Button mReadDataOnce =  (Button) findViewById(R.id.read_data_once_button);
        Button mReadDataCycle = (Button) findViewById(R.id.cycle_read_data_button);
        Button mWriteDataToCW = (Button) findViewById(R.id.write_data_button);

        updateIndication();

        mReadDataOnce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readDataFromModbus();
            }
        });


        mControlWordRes.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    offCycleRead();
                } else {
                    writeDataToModbus(mControlWordRes.getText().toString());
                    onCycleRead();
                }
                updateIndication();
            }
        });

        mWriteDataToCW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeDataToModbus(mControlWordRes.getText().toString());
                mControlWordRes.clearFocus();
            }
        });

        handler = new UpdateModbusWords();
        final Thread updaterThread = new Thread(new CycleReadModbus());
        updaterThread.start();

        mReadDataCycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cycleRead){
                    cycleRead = false;
                } else {
                    cycleRead = true;
                }
                updateIndication();
            }
        });
    }


    public void readDataFromModbus(){
        String[] paramsSW = {"","","",""};
        String[] paramsCW = {"","","",""};

        paramsSW[0] = IP;
        paramsSW[1] = port;
        paramsSW[2] = statusWord;
        paramsSW[3] = "1";          //Amount of words to read

        paramsCW[0] = IP;
        paramsCW[1] = port;
        paramsCW[2] = controlWord;
        paramsCW[3] = "1";          //Amount of words to read

        ReadFromModbus mReadSW = new ReadFromModbus();
        ReadFromModbus mReadCW = new ReadFromModbus();
        try {
            mStatusWordRes.setText(mReadSW.execute(paramsSW).get());
            mControlWordRes.setText(mReadCW.execute(paramsCW).get());
        } catch (ExecutionException e){
            System.out.println(e.getMessage());
        } catch (InterruptedException ie){
            System.out.println(ie.getMessage());
        }
    }

    public void writeDataToModbus(String value){
        String[] paramsCW = {"","","",""};

        paramsCW[0] = IP;
        paramsCW[1] = port;
        paramsCW[2] = controlWord;
        paramsCW[3] = value;

        WriteToModbus mWriteCW = new WriteToModbus();
        mWriteCW.execute(paramsCW);

    }


    public void offCycleRead(){
        cycleRead = false;
    }


    public void onCycleRead(){
        cycleRead = true;
    }


    public void updateIndication(){
        if (cycleRead){
            mCycleReadIndication.setText(this.getResources().getString(R.string.cycle_read_on_label_text));
        } else {
            mCycleReadIndication.setText(this.getResources().getString(R.string.cycle_read_off_label_text));
        }
    }
}
