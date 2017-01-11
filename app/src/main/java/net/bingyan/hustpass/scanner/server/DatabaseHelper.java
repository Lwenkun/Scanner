package net.bingyan.hustpass.scanner.server;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by lwenkun on 2016/12/17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public interface UserInfo {

        String COLUMN_ID = "id";
        String COLUMN_NAME = "name";
        String COLUMN_ADDRESS = "address";
        String COLUMN_PHONE_NUM = "phone_num";
        String COLUMN_ID_NUM = "id_num";
        String COLUMN_RECEIVED = "received";

        int INDEX_ID = 0;
        int INDEX_NAME = 1;
        int INDEX_ADDRESS = 2;
        int INDEX_PHONE_NUM = 3;
        int INDEX_ID_NUM = 4;
        int INDEX_RECEIVED = 5;
    }


    public interface QRCode {
        String COLUMN_ID = "id";
        String COLUMN_USER_ID = "user_id";
        String COLUMN_QR_CODE_URI = "qr_code_uri";

        int INDEX_ID = 0;
        int INDEX_USER_ID = 1;
        int INDEX_QR_CODE_URI = 2;
    }

    public interface User {
        String COLUMN_ID = "id";
        String COLUMN_EMAIL = "email";
        String COLUMN_PWD = "password";
    }

    public static final String DATABASE_NAME = "User.db";

    public static final String TABLE_USER_INFO = "user_info";
    public static final String TABLE_QR_CODE = "qr_code_uri";
    public static final String TABLE_USER = "user";

    private static final String CREATE_USER_INFO = "create table " + TABLE_USER_INFO + " (" +
            UserInfo.COLUMN_ID + " integer not null primary key autoincrement, " +
            UserInfo.COLUMN_NAME + " text, " +
            UserInfo.COLUMN_ADDRESS + " text, " +
            UserInfo.COLUMN_PHONE_NUM + " text, " +
            UserInfo.COLUMN_ID_NUM + " text, " +
            UserInfo.COLUMN_RECEIVED + " integer)";

    private static final String CREATE_QR_CODE = "create table " + TABLE_QR_CODE + "(" +
            QRCode.COLUMN_ID + " integer not null primary key autoincrement, " +
            QRCode.COLUMN_USER_ID + " text, " +
            QRCode.COLUMN_QR_CODE_URI + " text, " +
            "foreign key (" + QRCode.COLUMN_USER_ID + ") references " +
            TABLE_USER_INFO + "(" + UserInfo.COLUMN_ID + ")" +
            ")";

    private static final String CREATE_USER = "create table " + TABLE_USER + "(" +
            User.COLUMN_ID + " integer not null primary key autoincrement, " +
            User.COLUMN_EMAIL + " text not null unique, " +
            User.COLUMN_PWD + " text not null )";


    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        Log.d("CREATE_USER_INFO", "--> " + CREATE_USER_INFO);
        Log.d("CREATE_QR_CODE", "--> " + CREATE_QR_CODE);
        Log.d("CREATE_USER ", "--> " + CREATE_USER);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_USER_INFO);
        sqLiteDatabase.execSQL(CREATE_QR_CODE);
        sqLiteDatabase.execSQL(CREATE_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }



}
