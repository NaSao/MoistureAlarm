package de.tuc.piediaper.activity;

/**
 * Created by anupamchugh on 19/10/15.
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import de.tuc.piediaper.dao.DeviceDao;
import de.tuc.piediaper.model.Device;
import de.tuc.piediaper.util.CheckConnection;

import com.journaldev.sqlite.R;

import java.util.concurrent.ExecutionException;

public class ModifyDeviceActivity extends Activity implements OnClickListener {

    private EditText nameText;
    private Button updateBtn, deleteBtn;
    private EditText apiText;
    private EditText varidText;
    private Spinner typeSelect;
    private RadioGroup stateRadio;
    private RadioButton stateButton;
    private long _id;

    private DeviceDao deviceDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Modify Device");

        setContentView(R.layout.activity_modify_device);

        deviceDao = new DeviceDao(this);
        deviceDao.open();

        nameText = (EditText) findViewById(R.id.editname);
        apiText = (EditText) findViewById(R.id.editapi);
        varidText = (EditText) findViewById(R.id.editvarid);
        typeSelect = (Spinner) findViewById(R.id.edittype);
        stateRadio = (RadioGroup) findViewById(R.id.editstate);
        updateBtn = (Button) findViewById(R.id.btn_update);
        deleteBtn = (Button) findViewById(R.id.btn_delete);

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        String name = intent.getStringExtra("name");
        String type = intent.getStringExtra("type");
        String api = intent.getStringExtra("api");
        String varid = intent.getStringExtra("varid");
        String state = intent.getStringExtra("state");
        _id = Long.parseLong(id);

        nameText.setText(name);
        apiText.setText(api);
        varidText.setText(varid);
        if("Baby Diaper".equals(type)){
            typeSelect.setSelection(0);
        }else if("Plant Dry".equals(type)){
            typeSelect.setSelection(1);
        }else if("Plant Medium".equals(type)){
            typeSelect.setSelection(2);
        }else{
            typeSelect.setSelection(3);
        }
        if("On".equals(state)){
            stateRadio.check(R.id.editon);
        }else{
            stateRadio.check(R.id.editoff);
        }

        updateBtn.setOnClickListener(this);
        deleteBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_update:
                Device device = new Device();
                String name = nameText.getText().toString();
                String api = apiText.getText().toString();
                String varid = apiText.getText().toString();
                device.setId(_id);
                device.setName(nameText.getText().toString());
                device.setApi_key(apiText.getText().toString());
                device.setVariable_id(varidText.getText().toString());
                device.setType(typeSelect.getSelectedItem().toString());
                int stateId = stateRadio.getCheckedRadioButtonId();
                stateButton = (RadioButton) findViewById(stateId);
                device.setState(stateButton.getText().toString());
                try{
                    CheckConnection checkConnection = new CheckConnection(device.getApi_key(),device.getVariable_id());
                    checkConnection.execute();
                    System.out.println(checkConnection.get()+"-------------Result");
                    if(checkConnection.variableValues ==null){
                        Toast.makeText(ModifyDeviceActivity.this,
                                getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();
                        break;
                    }
                }catch(RuntimeException e){
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                deviceDao.update(device);
                this.returnHome();
                break;

            case R.id.btn_delete:
                deviceDao.delete(_id);
                this.returnHome();
                break;
        }
    }

    public void returnHome() {
        Intent home_intent = new Intent(getApplicationContext(), DeviceListActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(home_intent);
    }
}
