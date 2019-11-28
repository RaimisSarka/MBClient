package com.example.mbclient;

import android.os.AsyncTask;

import de.re.easymodbus.modbusclient.ModbusClient;

public class ReadFromModbus extends AsyncTask<String, Void, String> {
    Exception e;

    @Override
    protected String doInBackground(String... params) {
        ModbusClient mClient = new ModbusClient();
        String res = "";
        try {
            mClient.Connect(params[0], Integer.valueOf(params[1]));
            int[] input = mClient.ReadHoldingRegisters(Integer.valueOf(params[2]), Integer.valueOf(params[3]));
            res = String.valueOf(input[0]);

            return res;

        } catch (Exception e) {
            this.e = e;

            return null;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}