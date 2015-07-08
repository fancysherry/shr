package unique.fancysherry.shr.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import unique.fancysherry.shr.provider.ShrContract.*;

/**
 * Created by fancysherry on 15-7-8.
 */
public class ShrDatabaseHelper extends SQLiteOpenHelper {
  private final Context context;

  private static final String DATABASE_NAME = "shr.db";

  private static final int CUR_DATABASE_VERSION = 100; // 当前app 版本为 1.0.0



  interface Tables {
    String USERS = "users";
    String SHRS = "shrs";
    String INBOXS = "inboxs";
    String GROUPS = "groups";



    // When tables get deprecated, add them to this list (so they get correctly deleted
    // on database upgrades)
    interface DeprecatedTables {
      String TRACKS = "tracks";
      String SESSIONS_TRACKS = "sessions_tracks";
      String SANDBOX = "sandbox";
    }
  }


  // private interface Triggers {
  // // Deletes from dependent tables when corresponding sessions are deleted.
  // String SESSIONS_TAGS_DELETE = "sessions_tags_delete";
  // String SESSIONS_SPEAKERS_DELETE = "sessions_speakers_delete";
  // String SESSIONS_MY_SCHEDULE_DELETE = "sessions_myschedule_delete";
  // String SESSIONS_FEEDBACK_DELETE = "sessions_feedback_delete";
  //
  // // When triggers get deprecated, add them to this list (so they get correctly deleted
  // // on database upgrades)
  // interface DeprecatedTriggers {
  // String SESSIONS_TRACKS_DELETE = "sessions_tracks_delete";
  // }
  // }



  public ShrDatabaseHelper(Context context) {
    super(context, DATABASE_NAME, null, CUR_DATABASE_VERSION);
    this.context = context;
  }


  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL("CREATE TABLE " + Tables.USERS + " ("
        + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
        + UsersColumns.USER_UID + " TEXT NOT NULL,"

        + UsersColumns.USER_ID + " TEXT NOT NULL,"
        + UsersColumns.USER_NICKNAME + " TEXT NOT NULL,"
        + UsersColumns.USER_AVATAR + " TEXT NOT NULL,"
        + UsersColumns.USER_ISMAN + " TEXT NOT NULL,"
        + UsersColumns.USER_BRIEF + " TEXT NOT NULL,"
        + UsersColumns.USER_REGISTER_TIME + " TEXT NOT NULL,"

        + UsersColumns.USER_SHARES_SUM + " TEXT NOT NULL,"
        + UsersColumns.USER_GRATITUDES_SHARES_SUM + " TEXT NOT NULL,"
        + UsersColumns.USER_COMMMENTS_SUM + " TEXT NOT NULL,"
        + UsersColumns.USER_BLACK_USERS_SUM + " TEXT NOT NULL,"
        + UsersColumns.USER_FOLLOWERS_SUM + " TEXT NOT NULL,"
        + UsersColumns.USER_FOLLOWINGS_SUM + " TEXT NOT NULL,"

        + UsersColumns.USER_EMAIL + " TEXT NOT NULL,"
        + UsersColumns.USER_EDUCATION_INFORMATION + " TEXT NOT NULL,"
        + UsersColumns.USER_PHONE_NUMBER + " TEXT NOT NULL,"
        + UsersColumns.USER_PORTRAIT_PATH + " TEXT NOT NULL,"
        + "UNIQUE (" + UsersColumns.USER_ID + ") ON CONFLICT REPLACE)");


    db.execSQL("CREATE TABLE " + Tables.GROUPS + " ("
        + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
        + GroupssColumns.GROUP_ADMIN_ID + " TEXT NOT NULL,"
        + GroupssColumns.GROUP_ADMIN_NAME + " TEXT NOT NULL,"
        + GroupssColumns.GROUP_APPLY_USER_ID + " INTEGER NOT NULL,"
        + GroupssColumns.GROUP_APPLY_USER_NAME + " INTEGER NOT NULL,"
        + GroupssColumns.GROUP_CREATE_TIME + " TEXT,"
        + GroupssColumns.GROUP_ID + " TEXT,"
        + GroupssColumns.GROUP_NAME + " TEXT,"
        + GroupssColumns.GROUP_USERS_SUM + " TEXT,"

        + "UNIQUE (" + GroupssColumns.GROUP_ID + ") ON CONFLICT REPLACE)");



  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

  }


  public static void deleteDatabase(Context context) {
    context.deleteDatabase(DATABASE_NAME);
  }
}
