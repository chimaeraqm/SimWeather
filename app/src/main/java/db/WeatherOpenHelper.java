package db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2017/11/21.
 */

public class WeatherOpenHelper extends SQLiteOpenHelper{
    /**
     * create province, city, county database separately
     * original database format is
     *
     * create table county (
     *          id integer primary key autoincrement,
     *          county_name text,
     *          county_code text,
     *          county_id integer)
     */
    public static final String CREATE_PROVINCE = "create table province ("
            + "id integer primary key autoincrement,"
            + "province_name text,"
            + "province_code text)";

    public static final String CREATE_CITY = "create table city ("
            + "id integer primary key autoincrement,"
            + "city_name text,"
            + "city_code text,"
            + "province_id integer)";

    public static final String CREATE_COUNTY = "create table county ("
            + "id integer primary key autoincrement,"
            + "county_name text,"
            + "county_code text,"
            + "city_id integer)";

    public WeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        /**
         * create databases
         */
        sqLiteDatabase.execSQL(CREATE_COUNTY);
        sqLiteDatabase.execSQL(CREATE_PROVINCE);
        sqLiteDatabase.execSQL(CREATE_CITY);
    }
}
