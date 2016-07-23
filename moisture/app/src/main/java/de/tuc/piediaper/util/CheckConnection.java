package de.tuc.piediaper.util;

import android.os.AsyncTask;

import com.journaldev.sqlite.R;
import com.ubidots.ApiClient;
import com.ubidots.Value;
import com.ubidots.Variable;

/**
 * Created by Triger on 2016/7/10.
 */
public class CheckConnection extends AsyncTask<Integer, Void, String> {

    public String api;
    public String varid;
    public Value[] variableValues;
    public CheckConnection(String api,String varid){
        this.api = api;
        this.varid = varid;
    }
    @Override
    protected String doInBackground(Integer... params){
        //System.out.println("Devcies : "+devices.size()+"-------------");
        try{
            ApiClient apiClient = new ApiClient(api);
            Variable batteryLevel = apiClient.getVariable(varid);
            variableValues = batteryLevel.getValues();
        }catch(Exception e){
            e.printStackTrace();
            return "f";
        }

        return "ok";
    }

    protected void onPostExecute(String string){

    }
}
