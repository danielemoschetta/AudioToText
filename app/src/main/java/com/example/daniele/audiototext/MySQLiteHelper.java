package com.example.daniele.audiototext;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

    private static final String TAG = "MyLog_MySQLiteHelper";
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "ResultDB";
    // results table name
    private static final String TABLE_RESULTS = "results";

    // results Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TEXT = "text";
    private static final String KEY_TIME = "time";

    private static final String[] COLUMNS = {KEY_ID,KEY_TEXT,KEY_TIME};

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create result table
        String CREATE_RESULT_TABLE = "CREATE TABLE results ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "text TEXT, "+
                "time TEXT )";

        // create results table
        db.execSQL(CREATE_RESULT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older results table if existed
        db.execSQL("DROP TABLE IF EXISTS results");

        // create fresh results table
        this.onCreate(db);
    }

    public void addResult(Result r){
        //for logging
        Log.d("addResult", r.toString());

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_TEXT, r.getText()); // get title
        values.put(KEY_TIME, r.getTime()); // get author

        // 3. insert
        db.insert(TABLE_RESULTS, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    public Result getResult(int id){

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(TABLE_RESULTS, // a. table
                        COLUMNS, // b. column names
                        " id = ?", // c. selections
                        new String[] { String.valueOf(id) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();

        // 4. build result object
        Result r = new Result();
        r.setId(Integer.parseInt(cursor.getString(0)));
        r.setText(cursor.getString(1));
        r.setTime(cursor.getString(2));

        //log
        Log.d("getResult("+id+")", r.toString());

        // 5. return result
        return r;
    }

    public List<Result> getAllResults() {
        List<Result> results = new LinkedList<Result>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_RESULTS + " ORDER BY time DESC";

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build result and add it to list
        Result r = null;
        if (cursor.moveToFirst()) {
            do {
                r = new Result();
                r.setId(Integer.parseInt(cursor.getString(0)));
                r.setText(cursor.getString(1));
                r.setTime(cursor.getString(2));

                // Add result to results
                results.add(r);
            } while (cursor.moveToNext());
        }

        Log.d("getAllResults()", results.toString());

        // return results
        return results;
    }

    public int updateResult(Result r) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put("text", r.getText()); // get title
        values.put("time", r.getTime()); // get author

        // 3. updating row
        int i = db.update(TABLE_RESULTS, //table
                values, // column/value
                KEY_ID+" = ?", // selections
                new String[] { String.valueOf(r.getId()) }); //selection args

        // 4. close
        db.close();

        return i;

    }

    public void deleteResult(Result r) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(TABLE_RESULTS, //table name
                KEY_ID+" = ?",  // selections
                new String[] { String.valueOf(r.getId()) }); //selections args

        // 3. close
        db.close();

        //log
        Log.d("deleteResult", r.toString());

    }

    public void deleteAllResults() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DROP TABLE IF EXISTS results");
        onCreate(db);
        db.close();
        //log
        Log.d("deleteAllResults", "Done.");

    }
}