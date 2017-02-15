package com.iwit.rodney.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper{
	private final String T  = "DBHelper";
	private Context ctx;
	private static DBHelper instance;
	
	public static DBHelper getInstance(Context ctx, String dbName, int dbVersion){
		if (instance == null) {
			instance = new DBHelper(ctx, dbName, dbVersion);
		}
		return instance;
	}
	
	private DBHelper (Context ctx, String dbName, int dbVersion) {
		this(ctx, dbName, null, dbVersion);
	}
	
	/**
	 * 
	 * @param context
	 * @param name
	 * @param factory
	 * @param version
	 */
    private DBHelper(Context context, String name, CursorFactory factory,  int version) {  
        super(context, name, factory, version);  
        ctx = context;
    }

	@Override
	public void onCreate(SQLiteDatabase db) {
		BufferedReader bReader = null;
		InputStream is = null;
		
		try {
			is = ctx.getAssets().open("lwdbscript");
			bReader = new BufferedReader(new InputStreamReader(is));
			String line = null;
		
			while ((line = bReader.readLine()) != null) {
				if (!TextUtils.isEmpty(line) && (!line.startsWith("#"))) {
					db.execSQL(line);
				}
			}
			Log.i(T, "db has been created!");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try{
				if (bReader != null) {
					bReader.close();
				}
				if (is != null) {
					is.close();
				}
			} catch(IOException e){}
		}
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int v1, int v2) {
		if (v1 != v2) {
			onCreate(db);
		}
	}  
}
