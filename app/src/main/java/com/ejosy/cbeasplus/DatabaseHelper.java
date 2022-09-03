package com.ejosy.cbeasplus;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper{

    // The Android's default system path
    // of your application database.
//Constants for Database name, table name, and column names

    public static final String DB_NAME = "cbeasDB";
    public static final String TABLE_NAME = "cbeas_subscription";
    public static final String TABLE_NAME_reg = "cbeas_reg";
    //
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DESIGNATION = "designation";
    public static final String COLUMN_AVENUE = "avenue";
    public static final String COLUMN_STREET = "street";
    //
    //database version
    private static final int DB_VERSION = 1;

    public DatabaseHelper(Context context)
    {
        super(context,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME
                + "(" + COLUMN_ID + " INTEGER, "
                + COLUMN_PHONE  + " VARCHAR, "
                + COLUMN_NAME + " VARCHAR, "
                + COLUMN_DESIGNATION + " VARCHAR, "
                + COLUMN_AVENUE  + " VARCHAR, "
                + COLUMN_STREET + " VARCHAR);";
        db.execSQL(sql);
         //
        String sql_reg = "CREATE TABLE " + TABLE_NAME_reg + "(" + COLUMN_PHONE + " VARCHAR  );";
        db.execSQL(sql_reg);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        String sql = "DROP TABLE IF EXISTS "+TABLE_NAME;
        db.execSQL(sql);
        onCreate(db);
    }

    public void  addClient(Integer id, String phone, String name,String designation,String avenue, String street)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_ID, id);
        contentValues.put(COLUMN_PHONE, phone);
        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_DESIGNATION, designation);
        contentValues.put(COLUMN_AVENUE, avenue);
        contentValues.put(COLUMN_STREET, street);

        db.insert(TABLE_NAME, null, contentValues);
                db.close();

    }
    public void  addClient_reg(String phone)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_PHONE, phone);
        db.insert(TABLE_NAME_reg, null, contentValues);
        db.close();

    }

    public void  updateClient_reg(String prev_phone,String new_phone )
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //
        contentValues.put(COLUMN_PHONE, new_phone);
        //
        db.update(TABLE_NAME_reg, contentValues, "phone=?", new String[]{prev_phone});
       //
        db.close();
    }

    public void delete(String phone_no){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+ TABLE_NAME + " WHERE " + COLUMN_PHONE + " = " + phone_no +"");
        db.close();
    }

    public Cursor getContent()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT phone, name  FROM " + TABLE_NAME + " ORDER BY " + COLUMN_PHONE + " ASC;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    @SuppressLint("Range")
    public String getContent_reg()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT phone  FROM " + TABLE_NAME ;
        Cursor c = db.rawQuery(sql, null);
        String phone = "";
        if (c.moveToFirst()) {
           phone = c.getString(c.getColumnIndex("phone"));
        }
            return phone;
    }

    public void delete()
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        //deleting rows
        sqLiteDatabase.delete(TABLE_NAME, null, null);
        sqLiteDatabase.close();
    }



    public ObjectClient readSingleRecord(String phone_no)
    {
        ObjectClient objectClient = null;
        String sql = "SELECT * FROM " +TABLE_NAME  + " WHERE phone=" + phone_no.toString();
        //String sql = "SELECT * FROM cisas WHERE phone=" + "8033927733";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        int x = cursor.getCount();
        if (cursor.moveToFirst()) {
            @SuppressLint("Range") int id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("id")));
            @SuppressLint("Range") String phone = cursor.getString(cursor.getColumnIndex("phone"));
            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("name"));
            @SuppressLint("Range") String designation = cursor.getString(cursor.getColumnIndex("designation"));
            @SuppressLint("Range") String avenue = cursor.getString(cursor.getColumnIndex("avenue"));
            @SuppressLint("Range") String street = cursor.getString(cursor.getColumnIndex("street"));
            //
            objectClient = new ObjectClient( id, phone, name, designation,avenue, street);

        }
        cursor.close();
        db.close();
        return objectClient;
    }

    public boolean PhoneExist_subscription(String phone_no)
    {

        String sql = "SELECT phone FROM " +TABLE_NAME  + " WHERE phone=" + phone_no.toString();
        //String sql = "SELECT * FROM cisas WHERE phone=" + "8033927733";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        boolean status = false;
        if (cursor.moveToFirst())
        {
            status = true;

        }
        else
        {
            status = false;
        }
        cursor.close();
        db.close();
        return status;

    }

    public boolean PhoneExist_reg()
    {

        String sql = "SELECT phone FROM " + TABLE_NAME_reg ;
        //String sql = "SELECT * FROM cisas WHERE phone=" + "8033927733";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        boolean status = false;
        if (cursor.moveToFirst())
        {
            status = true;

        }
        else
        {
            status = false;
        }
        cursor.close();
        db.close();
        return status;

    }

    public void  addClient(String phone)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_PHONE, phone);
        db.insert(TABLE_NAME_reg, null, contentValues);
        db.close();

    }



    public void delete_reg(String phone_no){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+ TABLE_NAME_reg + " WHERE " + COLUMN_PHONE + " = " + phone_no +"");
        db.close();
    }

    public Cursor getContent_reg(String phone_no)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT phone  FROM " + TABLE_NAME_reg + " WHERE " + COLUMN_PHONE + " = " + phone_no +"";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    public void delete_reg()
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        //deleting rows
        sqLiteDatabase.delete(TABLE_NAME_reg, null, null);
        sqLiteDatabase.close();
    }

}
