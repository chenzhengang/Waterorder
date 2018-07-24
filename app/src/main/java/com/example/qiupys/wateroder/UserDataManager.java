package com.example.qiupys.wateroder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;


public class UserDataManager {
    private static final String TAG = "UserDataManager";
    private static final String DB_NAME="User_data";
    private static final int DB_VERSION=2;

    private static final String USER_TABLE="Users";
    private static final String ID="ID";
    private static final String USER_NAME = "User_name";
    private static final String USER_PWD="User_pwd";
    private static final String USER_PHONE="Phone";
    private static final String USER_ADDRESS="Address";

    private static final String AKS_TABLE="AKS";
    private static final String AKS_ID="AKS_ID";
    private static final String AKS_SERIALNUMBER="AKS_SerialNumber";
    private static final String GOODS_NAME="Goods_Name";

    private static final String ORDER_TABLE="Orders";
    private static final String AMOUNT="Amount";
    private static final String DATE="Date";

    private static final String CARD_TABLE="Bankcard";
    private static final String CARD_NUMBER="Card_Number";
    private static final String CARD_BALANCE="Balance";

    private Context mContext;

    private static final String createUser="CREATE TABLE "+USER_TABLE+" ( "+ID+" integer primary key autoincrement, "+ USER_NAME+" varchar, "+USER_PWD+" varchar, "+USER_PHONE+" varchar, "+USER_ADDRESS+" varchar"+");";
    private static final String createAKS="CREATE TABLE "+AKS_TABLE+" ("+USER_NAME+" varchar, "+ AKS_SERIALNUMBER+" varchar, "+GOODS_NAME+" varchar, "+ AKS_ID+" integer primary key autoincrement"+");";
    private static final String createOrder="CREATE TABLE "+ORDER_TABLE+" ("+USER_NAME+" varchar, "+AKS_ID+" integer, "+AMOUNT+" real, "+DATE+" smalldatetime"+");";
    private static final String createCard="CREATE TABLE "+CARD_TABLE+" ("+USER_NAME+" varchar, "+CARD_NUMBER+" varchar primary key, "+CARD_BALANCE+" real"+");";

    private SQLiteDatabase mSQLiteDatabase=null;
    private DataBaseManagementHelper mDataBaseHelper=null;

    private static class DataBaseManagementHelper extends SQLiteOpenHelper{

        DataBaseManagementHelper(Context context){super(context,DB_NAME,null,DB_VERSION);}

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            Log.i(TAG,"sqLiteDatabase.getVersion()="+sqLiteDatabase.getVersion());
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + USER_TABLE + ";");
            sqLiteDatabase.execSQL(createUser);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + AKS_TABLE + ";");
            sqLiteDatabase.execSQL(createAKS);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ORDER_TABLE + ";");
            sqLiteDatabase.execSQL(createOrder);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CARD_TABLE + ";");
            sqLiteDatabase.execSQL(createCard);
            Log.i(TAG, "sqLiteDatabase.execSQL(createDataBase)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            Log.i(TAG,"DataBaseManger onUpgrade.");
            onCreate(sqLiteDatabase);
        }
    }

    public UserDataManager(Context context){
        mContext=context;
        Log.i(TAG,"UserDataManager construction.");
    }

    public void openDatabase() throws SQLException{
        mDataBaseHelper=new DataBaseManagementHelper(mContext);
        mSQLiteDatabase=mDataBaseHelper.getWritableDatabase();
    }

    public void closeDatabase() throws SQLException{
        mDataBaseHelper.close();
    }

    //register
    public long insertUserData(UserData userData){
        String userName=userData.getUser_name();
        String userPwd=userData.getUser_pwd();

        ContentValues values=new ContentValues();
        values.put(USER_NAME,userName);
        values.put(USER_PWD,userPwd);
        long result1=mSQLiteDatabase.insert(USER_TABLE,ID,values);
        ContentValues avalues=new ContentValues();
        avalues.put(USER_NAME,userName);
        String serialnumber="AKSNOS"+userName.hashCode();
        avalues.put(AKS_SERIALNUMBER,serialnumber);
        long result2=mSQLiteDatabase.insert(AKS_TABLE,AKS_ID,avalues);
        ContentValues bvalues=new ContentValues();
        bvalues.put(USER_NAME,userName);
        long result3=mSQLiteDatabase.insert(CARD_TABLE,null,bvalues);
        if (result1>0&&result2>0&&result3>0)
            return 1;
        else
            return -1;
    }

    //update
    public boolean updateUserData(UserData userData){
        String userName=userData.getUser_name();
        String userPwd = userData.getUser_pwd();
        ContentValues values = new ContentValues();
        values.put(USER_NAME, userName);
        values.put(USER_PWD, userPwd);
        return mSQLiteDatabase.update(USER_TABLE, values,null, null) > 0;
    }

    public boolean updateUserDataByName(HashMap<String,String> data, String username){
        String phone=data.get("PHONE");
        String address=data.get("ADDRESS");
        String bankcard=data.get("BANK");

        ContentValues uvalues=new ContentValues();
        uvalues.put(USER_PHONE,phone);
        uvalues.put(USER_ADDRESS,address);
        boolean result_user=mSQLiteDatabase.update(USER_TABLE,uvalues,USER_NAME+"=?",new String[]{username})>0;
        ContentValues bvalues=new ContentValues();
        bvalues.put(CARD_NUMBER,bankcard);
        boolean result_bank=mSQLiteDatabase.update(CARD_TABLE,bvalues,USER_NAME+"=?",new String[]{username})>0;
        return result_user&&result_bank;
    }

    public boolean updateUserDataByID(String columnName,String columnValue,int id){
        ContentValues values=new ContentValues();
        values.put(columnName,columnValue);
        return mSQLiteDatabase.update(USER_TABLE,values,ID+"="+id,null)>0;
    }

    //delete
    public boolean deleteUserData(int id) {
        return mSQLiteDatabase.delete(USER_TABLE, ID + "=" + id, null) > 0;
    }
    public boolean deleteAllUserDatas() {
        return mSQLiteDatabase.delete(USER_TABLE, null, null) > 0;
    }

    //query
    public Cursor fetchUserData(String username) throws SQLException{
        Cursor cursor=mSQLiteDatabase.query(false,USER_TABLE,null,USER_NAME+"=?",new String[]{username},null,null,null,null);
        if (cursor!=null){
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor fetchCardData(String username) {
        Cursor cursor=mSQLiteDatabase.query(false,CARD_TABLE,null,USER_NAME+"=?",new String[]{username},null,null,null,null);
        if (cursor!=null){
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor fetchAKSData(String username) {
        Cursor cursor=mSQLiteDatabase.query(false,AKS_TABLE,null,USER_NAME+"=?",new String[]{username},null,null,null,null);
        if (cursor!=null){
            cursor.moveToFirst();
        }
        return cursor;
    }

    public String getStringByColumnName(String columnName,String username){
        Cursor cursor=fetchUserData(username);
        int columnIndex=cursor.getColumnIndex(columnName);
        String columnValue=cursor.getString(columnIndex);
        cursor.close();
        return columnValue;
    }

    //user exists?
    public boolean findUserByName(String userName){
        Log.i(TAG,"findUserByName , userName="+userName);
        String selection=USER_NAME+" = ? ";
        String[] selectionArgs=new String[]{userName};
        Cursor cursor=mSQLiteDatabase.query(USER_TABLE, null, selection, selectionArgs, null, null, null);
        int result=cursor.getCount();
        if (result!=0){
            Log.i(TAG,"User exits!");
            cursor.close();
            return true;
        }
        return false;
    }

    //user login
    public boolean userLogin(String userName,String userPwd){
        Log.i(TAG,"User Login.");
        String selection="User_name=? and User_pwd=?";
        String[] selectionArgs=new String[]{userName,userPwd};
        Cursor cursor=mSQLiteDatabase.query(USER_TABLE, null, selection, selectionArgs, null, null, null);
        int result=cursor.getCount();
        if (result!=0){
            Log.i(TAG,"User "+userName+" Login Success!");
            cursor.close();
            return true;
        }
        return false;
    }
}
