package android.example.com.studentlife_01.helper

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.example.com.studentlife_01.model.User
import android.util.Log


class SQLiteHandler(context: Context,
                    factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME,
        factory, DATABASE_VERSION) {

    private val TAG = SQLiteHandler::class.java.simpleName
    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "studentlife_api"
        val TABLE_NAME = "user"
        // Login Table Columns names
        private val KEY_ID = "id"
        private val KEY_NAME = "name"
        private val KEY_EMAIL = "email"
        private val KEY_UID = "uid"
        private val KEY_CREATED_AT = "created_at"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_LOGIN_TABLE = ("CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE," + KEY_UID + " TEXT,"
                + KEY_CREATED_AT + " TEXT" + ")")
        db.execSQL(CREATE_LOGIN_TABLE)

        Log.d(TAG, "Database tables created")
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }

    fun addUser(user: User) {
        val db = this.writableDatabase
        val values = ContentValues()

        values.put(KEY_NAME, user.name); // Name
        values.put(KEY_EMAIL, user.email); // Email
        values.put(KEY_UID, user.uid); // Email
        values.put(KEY_CREATED_AT, user.created_at); // Created At

        val id =  db.insert(TABLE_NAME, null, values)
        db.close()

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    /**
     * Getting user data from database
     */
    fun getUserDetails(): HashMap<String, String> {
        val user = HashMap<String, String>()
        val selectQuery = "SELECT  * FROM $TABLE_NAME"

        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        // Move to first row
        cursor.moveToFirst()
        if (cursor.count > 0) {
            user["name"] = cursor.getString(1)
            user["email"] = cursor.getString(2)
            user["uid"] = cursor.getString(3)
            user["created_at"] = cursor.getString(4)
        }
        cursor.close()
        db.close()
        // return user
        Log.d(TAG, "Fetching user from Sqlite: $user")

        return user
    }

    /**
     * Re crate database Delete all tables and create them again
     */
    fun deleteUsers() {
        val db = this.writableDatabase
        // Delete All Rows
        db.delete(TABLE_NAME, null, null)
        db.close()

        Log.d(TAG, "Deleted all user info from sqlite")
    }
}