package unique.fancysherry.shr.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import static unique.fancysherry.shr.util.LogUtil.makeLogTag;


/**
 * Created by fancysherry on 15-7-8.
 */
public class ShrProvider extends ContentProvider {
  private static final String TAG = makeLogTag(ShrProvider.class);

  private ShrDatabaseHelper shrDatabaseHelper;

  private static final UriMatcher sUriMatcher = buildUriMatcher();


  private static final int USERS = 100;
  private static final int USERS_ID = 101;

  private static final int GROUPS = 200;
  private static final int GROUPS_ID = 201;



  private static UriMatcher buildUriMatcher() {
    final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
    final String authority = ShrContract.CONTENT_AUTHORITY;

    matcher.addURI(authority, "users", USERS);
    matcher.addURI(authority, "users/*", USERS_ID);
    matcher.addURI(authority, "groups", GROUPS);
    matcher.addURI(authority, "groups/*", GROUPS_ID);


    return matcher;



  }

  @Override
  public boolean onCreate() {
    shrDatabaseHelper = new ShrDatabaseHelper(getContext());
    return false;
  }

  private void deleteDatabase()
  {
    // TODO: wait for content provider operations to finish, then tear down
    shrDatabaseHelper.close();
    Context context = getContext();
    ShrDatabaseHelper.deleteDatabase(context);
    shrDatabaseHelper = new ShrDatabaseHelper(getContext());

  }

  @Override
  public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
      String sortOrder) {
    return null;
  }

  @Override
  public String getType(Uri uri) {
    final int match = sUriMatcher.match(uri);
    return null;
  }

  @Override
  public Uri insert(Uri uri, ContentValues values) {
    return null;
  }

  @Override
  public int delete(Uri uri, String selection, String[] selectionArgs) {
    return 0;
  }

  @Override
  public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
    return 0;
  }
}
