package de.tuc.piediaper.util;

/**
 * Created by anupamchugh on 19/10/15.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Table Name
    public static final String TABLE_NAME = "Devices";

    // Table columns
    public static final String _ID = "_id";
    public static final String NAME = "name";
    public static final String API_KEY = "api_key";
    public static final String VARIABLE_ID = "variable_id";
    public static final String DEVICE_TYPE = "device_type";
    public static final String DEVICE_STATE = "device_state";
    public static final String DEVICE_LAST_VALUE="device_last_value";
    public static final String DEVICE_MOISTURE = "device_moisture";
    // Database Information
    static final String DB_NAME = "PIEDDIAPER.DB";

    // database version
    static final int DB_VERSION = 8;

    // Creating table query
    private static final String CREATE_TABLE = "create table " + TABLE_NAME + "(" + _ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME + " TEXT NOT NULL, " + API_KEY + " TEXT, "+ VARIABLE_ID + " TEXT, "+ DEVICE_STATE + " TEXT, "+ DEVICE_LAST_VALUE + " TEXT, "+ DEVICE_MOISTURE + " DOUBLE, " + DEVICE_TYPE + " TEXT);";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
