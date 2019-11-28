package com.example.mbclient;

import android.os.AsyncTask;

import de.re.easymodbus.modbusclient.ModbusClient;

public class WriteToModbus extends AsyncTask<String, Void, Void>{
    Exception e;

    @Override
    protected Void doInBackground(String... params) {
        ModbusClient mClient = new ModbusClient();
        try {
            mClient.Connect(params[0], Integer.valueOf(params[1]));
            mClient.WriteSingleRegister(Integer.valueOf(params[2]), Integer.valueOf(params[3]));
        } catch (Exception e) {
            this.e = e;
            return null;
        } finally {

        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}