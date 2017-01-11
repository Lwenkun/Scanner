package net.bingyan.hustpass.scanner.server;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.RemoteException;
import android.util.Log;

import net.bingyan.hustpass.scanner.Server;
import net.bingyan.hustpass.scanner.model.User;
import net.bingyan.hustpass.scanner.model.UserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * this class is used to implement the action defined in IQueryManager
 * Created by lwenkun on 2016/12/17.
 */

public class Proxy extends Server.Stub {

    private static final String TAG = "QueryManager";

    private DatabaseHelper databaseHelper;

    private Context context;
    private int versionCode;

    public Proxy(Context context, int versionCode) {
        super();
        this.context = context.getApplicationContext();
        this.versionCode = versionCode;
    }

    /**
     * query user info
     * @param key keyword
     * @return user info list that match this keyword
     * @throws RemoteException
     */
    @Override
    public List<UserInfo> queryUserInfo(String key) throws RemoteException {

        ensureQueryHelpNotNull();

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        String sql = "select * from " + DatabaseHelper.TABLE_USER_INFO +
                " where " + DatabaseHelper.UserInfo.COLUMN_NAME + " like '%" + key + "%' or "+
                DatabaseHelper.UserInfo.COLUMN_ADDRESS +" like '%" + key + "%' or " +
                DatabaseHelper.UserInfo.COLUMN_PHONE_NUM + " like '%" + key + "%' or " +
                DatabaseHelper.UserInfo.COLUMN_ID_NUM + " like '%" + key + "%' ";

        Log.d(TAG, "queryUserInfo() --> " + sql);

        List<UserInfo> userInfoList = new ArrayList<>();

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, null);

            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                UserInfo info = new UserInfo();
                info.id = cursor.getInt(DatabaseHelper.UserInfo.INDEX_ID);
                info.name = cursor.getString(DatabaseHelper.UserInfo.INDEX_NAME);
                info.address = cursor.getString(DatabaseHelper.UserInfo.INDEX_ADDRESS);
                info.phoneNum = cursor.getString(DatabaseHelper.UserInfo.INDEX_PHONE_NUM);
                info.idNum = cursor.getString(DatabaseHelper.UserInfo.INDEX_ID_NUM);
                info.received = cursor.getInt(DatabaseHelper.UserInfo.INDEX_RECEIVED) == 1;
                userInfoList.add(info);

                cursor.moveToNext();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
            cursor.close();
            databaseHelper.close();
        }

        Log.d(TAG, "queried users' info --> " + userInfoList.toString());
        return userInfoList;
    }


    /**
     * add a record of this user into database
     * @param info user info you want to add into database
     * @return row Id return by database after you insert this record
     * @throws RemoteException
     */
    @Override
    public long addUserInfo(UserInfo info) throws RemoteException {

        ensureQueryHelpNotNull();

        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.UserInfo.COLUMN_NAME, info.name);
        values.put(DatabaseHelper.UserInfo.COLUMN_ADDRESS, info.address);
        values.put(DatabaseHelper.UserInfo.COLUMN_PHONE_NUM, info.phoneNum);
        values.put(DatabaseHelper.UserInfo.COLUMN_ID_NUM, info.idNum);
        values.put(DatabaseHelper.UserInfo.COLUMN_RECEIVED, info.received ? 1 : 0);

        long rowId = db.insert(DatabaseHelper.TABLE_USER_INFO, null, values);

        databaseHelper.close();

        return rowId;
    }

    /**
     * attach this QRCode uri to specific user identified by its id
     * @param userId
     * @param key
     * @throws RemoteException
     */
    @Override
    public long attachQRCodeKey(int userId, String key) throws RemoteException {
        ensureQueryHelpNotNull();

        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.QRCode.COLUMN_USER_ID, userId);
        values.put(DatabaseHelper.QRCode.COLUMN_QR_CODE_URI, key);

        long rowId = db.insert(DatabaseHelper.TABLE_QR_CODE, null, values);

        databaseHelper.close();

        return rowId;
    }

    /**
     * get QRCode uri attached to this userId
     * @param userId user's id
     * @return QRCode uri
     * @throws RemoteException
     */
    @Override
    public String getQRCodeKey(int userId) throws RemoteException {
        ensureQueryHelpNotNull();

        Log.d(TAG, "userId --> " + userId);

        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        Cursor cursor = db.query(DatabaseHelper.TABLE_QR_CODE,
                new String[]{DatabaseHelper.QRCode.COLUMN_QR_CODE_URI},
                 DatabaseHelper.QRCode.COLUMN_USER_ID + " = ? ",
                new String[]{String.valueOf(userId)}, null, null, null);

        cursor.moveToFirst();

        String key = cursor.getString(cursor.getColumnIndex(DatabaseHelper.QRCode.COLUMN_QR_CODE_URI));

        cursor.close();
        databaseHelper.close();

        if (key == null) System.out.println("key is null");
        return key;
    }

    /**
     * a utility method used to ensure our QueryHelper is not null
     */
    private void ensureQueryHelpNotNull() {
        if (databaseHelper == null) {
            databaseHelper = new DatabaseHelper(context, DatabaseHelper.DATABASE_NAME, null, versionCode);
        }
    }

    /**
     * use this method to update a record of user info identified by it's id.
     * @param newInfo new info of this user.
     * @throws RemoteException
     */
    @Override
    public void updateUserInfo(UserInfo newInfo) throws RemoteException {
        ensureQueryHelpNotNull();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.UserInfo.COLUMN_NAME, newInfo.name);
        values.put(DatabaseHelper.UserInfo.COLUMN_ADDRESS, newInfo.address);
        values.put(DatabaseHelper.UserInfo.COLUMN_PHONE_NUM, newInfo.phoneNum);
        values.put(DatabaseHelper.UserInfo.COLUMN_ID_NUM, newInfo.idNum);
        values.put(DatabaseHelper.UserInfo.COLUMN_RECEIVED, newInfo.received ? 1 : 0);

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.update(DatabaseHelper.TABLE_USER_INFO, values,
                DatabaseHelper.UserInfo.COLUMN_ID + " = ?", new String[]{String.valueOf(newInfo.id)});
    }

    @Override
    public boolean login(String email, String pwdMd5) throws RemoteException {
        ensureQueryHelpNotNull();

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        String sql = "select * from " + DatabaseHelper.TABLE_USER + " where " +
                DatabaseHelper.User.COLUMN_EMAIL + "='" + email + "' and " +
                DatabaseHelper.User.COLUMN_PWD + "='" + pwdMd5 + "'";

        Log.d(TAG, "sql --> " + sql);

        Cursor cursor = db.rawQuery(sql, null);
        int count = cursor.getCount();
        cursor.close();

        Log.d(TAG, "count --> " + count);

        return count > 0;
    }


    @Override
    public long addUser(User user) throws RemoteException {
        ensureQueryHelpNotNull();

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.User.COLUMN_EMAIL, user.email);
        values.put(DatabaseHelper.User.COLUMN_PWD, user.pwdMd5);

        return db.insert(DatabaseHelper.TABLE_USER, null, values);
    }

}
