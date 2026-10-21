package com.example.smartpantry.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

/** Local SQLite persistence for pantry items. */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_PANTRY = "pantry_items";
    public static final String COL_ID = "_id";
    public static final String COL_NAME = "name";
    public static final String COL_QTY = "quantity";
    public static final String COL_UNIT = "unit";

    private static DatabaseHelper instance;
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) instance = new DatabaseHelper(context.getApplicationContext());
        return instance;
    }
    private DatabaseHelper(Context context) { super(context, DATABASE_NAME, null, DATABASE_VERSION); }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_PANTRY + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT NOT NULL, " +
                COL_QTY + " REAL NOT NULL, " +
                COL_UNIT + " TEXT NOT NULL, expiry_date TEXT)");
    }
    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY); onCreate(db);
    }
    public long insertPantryItem(PantryItem item) {
        ContentValues v = values(item); return getWritableDatabase().insert(TABLE_PANTRY, null, v);
    }
    public int updatePantryItem(PantryItem item) {
        return getWritableDatabase().update(TABLE_PANTRY, values(item), COL_ID + "=?", new String[]{String.valueOf(item.getId())});
    }
    public int deletePantryItem(long id) {
        return getWritableDatabase().delete(TABLE_PANTRY, COL_ID + "=?", new String[]{String.valueOf(id)});
    }
    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> items = new ArrayList<>();
        Cursor c = getReadableDatabase().query(TABLE_PANTRY, null, null, null, null, null, COL_NAME + " COLLATE NOCASE ASC");
        while (c.moveToNext()) {
            PantryItem i = new PantryItem();
            i.setId(c.getLong(c.getColumnIndexOrThrow(COL_ID)));
            i.setName(c.getString(c.getColumnIndexOrThrow(COL_NAME)));
            i.setQuantity(c.getDouble(c.getColumnIndexOrThrow(COL_QTY)));
            i.setUnit(c.getString(c.getColumnIndexOrThrow(COL_UNIT)));
            items.add(i);
        }
        c.close();
        return items;
    }
    private ContentValues values(PantryItem i) {
        ContentValues v=new ContentValues();
        v.put(COL_NAME,i.getName()); v.put(COL_QTY,i.getQuantity()); v.put(COL_UNIT,i.getUnit()); v.put("expiry_date",i.getExpiryDate());
        return v;
    }
}
