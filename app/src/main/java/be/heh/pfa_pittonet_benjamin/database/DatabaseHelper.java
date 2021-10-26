package be.heh.pfa_pittonet_benjamin.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "pfa_bugdroid.db";
    public static final String USERS_TABLE = "users";
    public static final String COL_1_ID="id";
    public static final String COL_2_NAME="name";
    public static final String COL_3_SURNAME="surname";
    public static final String COL_4_MAIL="mail";
    public static final String COL_5_PSSWD="password";
    public static final String COL_6_ISADMIN="isAdmin";

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, 1);
    }

    //Creating DB
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, surname TEXT, mail TEXT UNIQUE,  password , isAdmin BOOLEAN)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USERS_TABLE);
        onCreate(db);
    }

    public Cursor getAllUser() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + USERS_TABLE, null);
        return cursor;
    }

    public Cursor getPrevious() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + USERS_TABLE + " WHERE " + COL_6_ISADMIN + " IS " + 1 + ";", null);
        return cursor;
    }

    public Cursor getOneUser(String loginMail) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + USERS_TABLE + " WHERE " + COL_4_MAIL + " IS " + "'" + loginMail + "';", null);
        return cursor;
    }

    public void deleteUser (User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(USERS_TABLE, COL_4_MAIL + " = " + "'" + user.getMail() + "'", null);
    }

    public long addSuperUser(String name, String surname, String mail, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("surname", surname);
        contentValues.put("mail", mail);
        contentValues.put("password", password);
        contentValues.put("isAdmin", true);
        long res = db.insert("users", null, contentValues);
        db.close();
        return res;
    }

    public long addUser(String name, String surname, String mail, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("surname", surname);
        contentValues.put("mail", mail);
        contentValues.put("password", password);
        contentValues.put("isAdmin", false);
        long res = db.insert("users", null, contentValues);
        db.close();
        return res;
    }

    public void updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2_NAME, user.getName());
        contentValues.put(COL_3_SURNAME, user.getSurname());
        contentValues.put(COL_5_PSSWD, user.getPassword());
        db.update(USERS_TABLE, contentValues, COL_4_MAIL + " = " + "'" + user.getMail() + "'", null);
    }

    public void updateUserPerms(User user){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_6_ISADMIN, user.getIsAdmin());
        db.update(USERS_TABLE, contentValues, COL_4_MAIL + " = " + "'" + user.getMail() + "'", null);
    }
}
