package com.example.stockwatch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class Database extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "StockAppDB";
    private static final String TABLE_NAME = "StockWatchTable";
    private static final String SYMBOL = "StockSymbol";
    private static final String COMPANY = "CompanyName";
    private static final int DATABASE_VERSION = 1;

    private SQLiteDatabase database;

    public Database(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        database = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " ( " + SYMBOL + " TEXT not null unique, " + COMPANY +" TEXT not null)");
    }

    public void addStock(Stock stock){
        ContentValues vals = new ContentValues();
        vals.put(SYMBOL, stock.getSymbol());
        vals.put(COMPANY, stock.getName());
        database.insert(TABLE_NAME, null, vals);
    }

    //copied from handout given
    public void deleteStock(String symbol){
        int cnt = database.delete(TABLE_NAME, SYMBOL +" = ?", new String[] {symbol});
    }

    public ArrayList<String[]> loadStocks(){
        ArrayList<String[]> stocks = new ArrayList<>();
        Cursor cursor = database.query(
                TABLE_NAME,
                new String[]{SYMBOL, COMPANY},
                null,
                null,
                null ,
                null,
                null);
        if (cursor !=null){
            cursor.moveToFirst();
            for (int i =0; i<cursor.getCount(); i++){
                String symbol = cursor.getString(0);
                String company = cursor.getString(1);
                stocks.add(new String[] {symbol, company});
                cursor.moveToNext();
            }
            cursor.close();
        }
        return stocks;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
