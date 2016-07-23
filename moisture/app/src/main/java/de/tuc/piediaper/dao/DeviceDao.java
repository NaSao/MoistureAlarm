package de.tuc.piediaper.dao;

/**
 * Created by anupamchugh on 19/10/15.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import de.tuc.piediaper.model.Device;
import de.tuc.piediaper.util.DatabaseHelper;

public class DeviceDao {

    private DatabaseHelper dbHelper;

    private Context context;

    private SQLiteDatabase database;

    private  String[] columns = new String[] { DatabaseHelper._ID,
            DatabaseHelper.NAME, DatabaseHelper.API_KEY,DatabaseHelper.VARIABLE_ID,DatabaseHelper.DEVICE_TYPE,DatabaseHelper.DEVICE_STATE, DatabaseHelper.DEVICE_LAST_VALUE,DatabaseHelper.DEVICE_MOISTURE};

    public DeviceDao(Context c) {
        context = c;
    }

    public DeviceDao open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public void insert(Device device){
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.NAME, device.getName());
        contentValue.put(DatabaseHelper.API_KEY, device.getApi_key());
        contentValue.put(DatabaseHelper.VARIABLE_ID, device.getVariable_id());
        contentValue.put(DatabaseHelper.DEVICE_TYPE,device.getType());
        contentValue.put(DatabaseHelper.DEVICE_STATE,device.getState());
        database.insert(DatabaseHelper.TABLE_NAME, null, contentValue);
    }

    public Cursor fetch() {
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public int update(Device device) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.NAME, device.getName());
        contentValue.put(DatabaseHelper.API_KEY, device.getApi_key());
        contentValue.put(DatabaseHelper.VARIABLE_ID, device.getVariable_id());
        contentValue.put(DatabaseHelper.DEVICE_TYPE,device.getType());
        contentValue.put(DatabaseHelper.DEVICE_STATE,device.getState());
        contentValue.put(DatabaseHelper.DEVICE_LAST_VALUE,device.getPercent());
        contentValue.put(DatabaseHelper.DEVICE_MOISTURE,device.getMoisture());
        int i = database.update(DatabaseHelper.TABLE_NAME, contentValue, DatabaseHelper._ID + " = " + device.getId(), null);
        return i;
    }

    public void delete(long _id) {
        database.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper._ID + "=" + _id, null);
    }

    public List<Device> readAllDevice() {
        List<Device> devices = new ArrayList<Device>();
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, columns, null, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

            while(!cursor.isAfterLast()) {
                Device device = new Device( cursor.getLong(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5));
                device.setPercent(cursor.getString(6));
                device.setMoisture(cursor.getDouble(7));
                devices.add(device);
                cursor.moveToNext();
            }
        //System.out.println("Device number: "+devices.size()+"-------------");
        cursor.close();
        return devices;
    }

}
