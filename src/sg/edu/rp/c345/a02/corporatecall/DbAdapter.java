package sg.edu.rp.c345.a02.corporatecall;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class DbAdapter {
	// Database properties
	private static final String DATABASE_NAME = "corporatecall.db";
	private static final String DATABASE_TABLE_NAME = "contacts";
	private static final int DATABASE_VERSION = 1;

	public static final String KEY_CONTACT_ID = "_id";
	public static final String KEY_CONTACT_NAME = "name";
	public static final String KEY_CONTACT_FUNCTION = "function";
	public static final String KEY_CONTACT_COMPANY = "company";
	public static final String KEY_CONTACT_PHONE = "phone";

	// Create script
	private static final String DATABASE_CREATE_FAV = "create table "
			+ DATABASE_TABLE_NAME + " (" + KEY_CONTACT_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_CONTACT_NAME
			+ " text not null, " + KEY_CONTACT_FUNCTION + " text not null, "
			+ KEY_CONTACT_COMPANY + " text not null, " + KEY_CONTACT_PHONE
			+ " text not null);";

	private SQLiteDatabase db;
	private final Context context;
	private MyDbHelper myDbHelper;

	// constructor create the wrapper to open and close the db
	public DbAdapter(Context _context) {
		context = _context;
		myDbHelper = new MyDbHelper(context, DATABASE_NAME, null,
				DATABASE_VERSION);
	}

	public DbAdapter open() throws SQLException {
		db = myDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		db.close();
	}

	// Insert new entry to the database or update if the task exist in the
	// database

	public long insertContact(String _name, String _function, String _company,
			String _phone) {
		Cursor c = db.query(DATABASE_TABLE_NAME, new String[] { KEY_CONTACT_ID,
				KEY_CONTACT_NAME, KEY_CONTACT_FUNCTION, KEY_CONTACT_COMPANY,
				KEY_CONTACT_PHONE }, KEY_CONTACT_NAME + "=\"" + _name + "\"",
				null, null, null, null);

		int count = c.getCount();
		if (count > 0) {
			c.moveToFirst();
			int idColumn = c.getColumnIndex(KEY_CONTACT_ID);
			long id = c.getLong(idColumn);
			c.close();
			return updateContact(id, _name, _function, _company, _phone);
		} else {
			c.close();
			ContentValues contentValues = new ContentValues();
			contentValues.put(KEY_CONTACT_NAME, _name);
			contentValues.put(KEY_CONTACT_FUNCTION, _function);
			contentValues.put(KEY_CONTACT_COMPANY, _company);
			contentValues.put(KEY_CONTACT_PHONE, _phone);

			Long test = db.insert(DATABASE_TABLE_NAME, null, contentValues);

			return test;
		}
	}

	// remove entry from the database based on the task name
	public boolean removeContact(String _numb) {
		return db.delete(DATABASE_TABLE_NAME, KEY_CONTACT_PHONE + "='" + _numb
				+ "'", null) > 0;
	}

	// retrieve all the entries in the database
	public Cursor getAllContacts() {
		return db.query(DATABASE_TABLE_NAME, new String[] { KEY_CONTACT_ID,
				KEY_CONTACT_NAME, KEY_CONTACT_FUNCTION, KEY_CONTACT_COMPANY,
				KEY_CONTACT_PHONE }, null, null, null, null, null);
	}

	// retrieve all the entries in the database
	public Cursor getAllContactsSearchName(String name) {
		return db.query(DATABASE_TABLE_NAME, new String[] { KEY_CONTACT_ID,
				KEY_CONTACT_NAME, KEY_CONTACT_FUNCTION }, KEY_CONTACT_NAME
				+ " LIKE '%" + name + "%'", null, null, null, null);
	}

	public int updateContact(long _id, String _name, String _function,
			String _company, String _phone) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_CONTACT_NAME, _name);
		contentValues.put(KEY_CONTACT_FUNCTION, _function);
		contentValues.put(KEY_CONTACT_COMPANY, _company);
		contentValues.put(KEY_CONTACT_PHONE, _phone);

		return db.update(DATABASE_TABLE_NAME, contentValues, KEY_CONTACT_ID
				+ "=" + _id, null);
	}

	public boolean removeAllContacts() {
		return db.delete(DATABASE_TABLE_NAME, null, null) > 0;
	}

	private static class MyDbHelper extends SQLiteOpenHelper {

		public MyDbHelper(Context context, String name, CursorFactory factory,
				int version) {
			super(context, name, factory, version);
		}

		@Override
		// Only gets called if the database does not exist on the phone
		public void onCreate(SQLiteDatabase _db) {
			_db.execSQL(DATABASE_CREATE_FAV);

		}

		public void onUpgrade(SQLiteDatabase _db, int _oldVersion,
				int _newVersion) {
			// Drop old one
			_db.execSQL("DROP TABLE IF EXISTS " + DATABASE_CREATE_FAV);
			// Create new one
			onCreate(_db);
		}
	}
}
