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

public class AddDeviceActivity extends Activity implements OnClickListener {

    private Button addTodoBtn;
    private EditText nameText;
    private EditText apiText;
    private EditText varidText;
    private Spinner typeSelect;
    private RadioGroup stateRadio;
    private RadioButton stateButton;

    private DeviceDao deviceDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Add Device");

        setContentView(R.layout.activity_add_device);

        nameText = (EditText) findViewById(R.id.addname);
        apiText = (EditText) findViewById(R.id.addapi);
        varidText = (EditText) findViewById(R.id.addvarid);
        typeSelect = (Spinner) findViewById(R.id.addtype);
        stateRadio = (RadioGroup) findViewById(R.id.addstate);
        addTodoBtn = (Button) findViewById(R.id.add_record);

        deviceDao = new DeviceDao(this);
        deviceDao.open();
        addTodoBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_record:
                Device device = new Device();
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
                        Toast.makeText(AddDeviceActivity.this,
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
                deviceDao.insert(device);
                Intent main = new Intent(AddDeviceActivity.this, DeviceListActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(main);
                break;
        }
    }

}