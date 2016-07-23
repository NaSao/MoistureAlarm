package de.tuc.piediaper.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import de.tuc.piediaper.dao.DeviceDao;
import de.tuc.piediaper.model.Device;
import de.tuc.piediaper.util.DatabaseHelper;
import com.journaldev.sqlite.R;
import com.ubidots.ApiClient;
import com.ubidots.Value;
import com.ubidots.Variable;

import java.util.List;

public class DeviceListActivity extends ActionBarActivity {

    private DeviceDao deviceDao;

    private ListView listView;

    private SimpleCursorAdapter adapter;

    //private Map<Device,Double> moistureResults = new HashMap<Device,Double>();

//    private final String[] from = new String[] { DatabaseHelper._ID,
//            DatabaseHelper.NAME,DatabaseHelper.DEVICE_LAST_VALUE,DatabaseHelper.DEVICE_TYPE, DatabaseHelper.API_KEY,DatabaseHelper.VARIABLE_ID,DatabaseHelper.DEVICE_STATE};

    private final String[] from = new String[] { DatabaseHelper._ID,
            DatabaseHelper.NAME,DatabaseHelper.DEVICE_TYPE, DatabaseHelper.API_KEY,DatabaseHelper.VARIABLE_ID,DatabaseHelper.DEVICE_STATE};

//    private final int[] to = new int[] { R.id.id, R.id.list_name,R.id.list_value, R.id.list_type,R.id.list_api,R.id.list_varid,R.id.list_state};
    private final int[] to = new int[] { R.id.id, R.id.list_name, R.id.list_type,R.id.list_api,R.id.list_varid,R.id.list_state};

    private List<Device> devices;

    private Handler mHandler = new Handler();


    private static final int DIAPER= 500;
    private static final int WET = 600;
    private static final int MEDIUM = 700;
    private static final int DRY = 800;
    private static final String DIAPERMESSAGE= "You need to change diaper from ";
    private static final String PLANTMESSAGE = "You need to water ";


    private static final String DIAPERTYPE="Baby Diaper";
    private static final String PLANTWET="Plant Wet";
    private static final String PLANTDRY="Plant Wet";
    private static final String PLANTMEDIUM="Plant Medium";
    private final static int INTERVAL = 1000*60;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_emp_list);

        deviceDao = new DeviceDao(this);
        deviceDao.open();
        Cursor cursor = deviceDao.fetch();

        listView = (ListView) findViewById(R.id.list_view);
        listView.setEmptyView(findViewById(R.id.empty));

        adapter = new SimpleCursorAdapter(this, R.layout.activity_view_device, cursor, from, to, 0);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);

        // OnCLickListiner For List Items
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long viewId) {
                TextView idTextView = (TextView) view.findViewById(R.id.id);
                TextView nameTextView = (TextView) view.findViewById(R.id.list_name);
                TextView typeTextView = (TextView) view.findViewById(R.id.list_type);
                TextView apiTextView = (TextView) view.findViewById(R.id.list_api);
                TextView varidTextView = (TextView) view.findViewById(R.id.list_varid);
                TextView stateTextView = (TextView) view.findViewById(R.id.list_state);
                String id = idTextView.getText().toString();
                String name = nameTextView.getText().toString();
                String type = typeTextView.getText().toString();
                String api = apiTextView.getText().toString();
                String varid = varidTextView.getText().toString();
                String state = stateTextView.getText().toString();
                Intent modify_intent = new Intent(getApplicationContext(), ModifyDeviceActivity.class);
                modify_intent.putExtra("name", name);
                modify_intent.putExtra("type", type);
                modify_intent.putExtra("id", id);
                modify_intent.putExtra("api", api);
                modify_intent.putExtra("varid", varid);
                modify_intent.putExtra("state",state);
                startActivity(modify_intent);
            }
        });
        devices = deviceDao.readAllDevice();
        startRepeatingTask();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.add_record) {

            Intent add_mem = new Intent(this, AddDeviceActivity.class);
            startActivity(add_mem);

        }
        return super.onOptionsItemSelected(item);
    }


    //alarm function
    public void alarm(View view,String alarmMessage){
        try {
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            final Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), notification);
            ringtone.play();

            alertDialogBuilder.setMessage(alarmMessage)
                    .setPositiveButton("I know", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            ringtone.stop();

                        }
                    })
                    .create();
            alertDialogBuilder.show();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //background thread reading start
    Runnable mHandlerTask = new Runnable()
    {
        public void run() {
            devices = deviceDao.readAllDevice();
            new ApiUbidots().execute();
            //finish();
            //startActivity(getIntent());
            for(Device device : devices){
                //if device off, do not alarm
                //System.out.println("State: "+device.getState()+"------------");
                if("Off".equals(device.getState())) {
                    continue;
                }
                System.out.println("Moisture: "+device.getMoisture()+"+++++++++++++");
                if(DIAPERTYPE.equals(device.getType())){
                    if(device.getMoisture()<DIAPER){
                        alarm(findViewById(R.id.main_view),DIAPERMESSAGE+device.getName());
                    }else{
                        continue;
                    }
                }else if(PLANTWET.equals(device.getType())){
                    if(device.getMoisture()>WET){
                        alarm(findViewById(R.id.main_view),PLANTMESSAGE+device.getName());
                    }else{
                        continue;
                    }
                }else if(PLANTMEDIUM.equals(device.getType())){
                    if(device.getMoisture()>MEDIUM){
                        alarm(findViewById(R.id.main_view),PLANTMESSAGE+device.getName());
                    }else{
                        continue;
                    }
                }else if(PLANTDRY.equals(device.getType())){
                    if(device.getMoisture()>DRY){
                        alarm(findViewById(R.id.main_view),PLANTMESSAGE+device.getName());
                    }else{
                        continue;
                    }
                }else{
                    continue;
                }

            }

            mHandler.postDelayed(mHandlerTask, INTERVAL);
        }
    };

    void startRepeatingTask()
    {
        mHandlerTask.run();
    }

    void stopRepeatingTask()
    {
        mHandler.removeCallbacks(mHandlerTask);
    }



    public class ApiUbidots extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... params) {
            //System.out.println("Devcies : "+devices.size()+"-------------");
            try{

                for(int i=0;i<devices.size();i++){
                    Device device = devices.get(i);
                    //if device off, do not check
                    if("Off".equals(device.getState()))
                        continue;
                    ApiClient apiClient = new ApiClient(devices.get(i).getApi_key());
                    Variable batteryLevel = apiClient.getVariable(devices.get(i).getVariable_id());
                    Value[] variableValues = batteryLevel.getValues();
                    double value = variableValues[0].getValue();
                    System.out.println(value+" From Ubidots.+============");

                    device.setMoisture(value);
                    device.setPercent(castNumToPercent(value));
                    devices.set(i,device);
                    deviceDao.update(device);
                    //System.out.println("Update value"+j+"--------");
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String string){
            //setContentView(R.layout.activity_main);

        }
    }
    //background thread reading end
    public String castNumToPercent(double value){
        String percent = "1%";
        if(value>1000){
            percent= "1%";
        }else if (value>900){
            percent= "10%";
        }else if(value>800){
            percent="20%";
        }else if (value>700){
            percent="30%";
        }else if (value>600){
            percent="40%";
        }else if (value>500){
            percent="70%";
        }else if (value>400){
            percent="80%";
        }else{
            percent= "100%";
        }
        return percent;
    }
}