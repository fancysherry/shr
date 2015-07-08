package unique.fancysherry.shr.provider;

import android.app.SearchManager;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.text.format.DateUtils;

/**
 * Created by fancysherry on 15-7-8.
 */
public class ShrContract {

  public interface SyncColumns {
    /** Last time this entry was updated or synchronized. */
    String UPDATED = "updated";
  }

  // 关注的人列表 在线加载
  // 评论列表 在线加载

  interface UsersColumns {
    String USER_UID = "user_uid";
    String USER_ID = "user_id";
    String USER_NICKNAME = "user_nickname";
    String USER_AVATAR = "user_avatar";
    String USER_ISMAN = "user_isman";
    String USER_BRIEF = "user_brief";
    String USER_REGISTER_TIME = "user_register_time";

    String USER_SHARES_SUM = "user_shares_sum";
    String USER_GRATITUDES_SHARES_SUM = "user_gratitude_shares_sum";
    String USER_COMMMENTS_SUM = "user_comments_sum";
    String USER_BLACK_USERS_SUM = "user_black_users_sum";
    String USER_FOLLOWERS_SUM = "user_followers_sum";
    String USER_FOLLOWINGS_SUM = "user_followings_sum";

    String USER_EMAIL = "user_email";
    String USER_EDUCATION_INFORMATION = "user_education_information";
    String USER_PHONE_NUMBER = "user_phone_number";
    String USER_PORTRAIT_PATH = "user_portrait_path";



  }

  interface GroupssColumns {
    String GROUP_ID = "group_id";
    String GROUP_APPLY_USER_ID = "group_apply_user_id";
    String GROUP_APPLY_USER_NAME = "group_apply_user_name";
    String GROUP_CREATE_TIME = "group_creater_time";
    String GROUP_NAME = "group_name";
    String GROUP_ADMIN_NAME = "group_admin_name";
    String GROUP_ADMIN_ID = "group_admin_id";
    String GROUP_USERS_SUM = "group_user_sum";
  }

  // interface ShrsColumns{
  // String SHR_="";
  // String SHR_="";
  // String SHR_="";
  // String SHR_="";
  // String SHR_="";
  // String SHR_="";
  // String SHR_="";
  // String SHR_="";
  // }
  //
  // interface InboxsColumns{
  // String INBOX_="";
  // String INBOX_="";
  // String INBOX_="";
  // String INBOX_="";
  // String INBOX_="";
  // String INBOX_="";
  // String INBOX_="";
  // String INBOX_="";
  // String INBOX_="";
  //
  // }
  public static final String CONTENT_AUTHORITY = "unique.fancysherry.shr";
  public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);



  private static final String PATH_USERS = "users";
  private static final String PATH_GROUPS = "groups";


  public static final String[] TOP_LEVEL_PATHS = {
      PATH_USERS,
      PATH_GROUPS,

  };


  public static final String[] USER_DATA_RELATED_PATHS = {
      // PATH_SESSIONS,
      // PATH_MY_SCHEDULE
      };

  public static class Users implements UsersColumns, BaseColumns {
    // public static final String BLOCK_TYPE_FREE = "free";
    // public static final String BLOCK_TYPE_BREAK = "break";
    // public static final String BLOCK_TYPE_KEYNOTE = "keynote";
    //
    // public static final boolean isValidBlockType(String type) {
    // return BLOCK_TYPE_FREE.equals(type) || BLOCK_TYPE_BREAK.equals(type)
    // || BLOCK_TYPE_KEYNOTE.equals(type);
    // }

    public static final Uri CONTENT_URI =
        BASE_CONTENT_URI.buildUpon().appendPath(PATH_USERS).build();

    // public static final String CONTENT_TYPE =
    // "vnd.android.cursor.dir/vnd.iosched2014.block";
    // public static final String CONTENT_ITEM_TYPE =
    // "vnd.android.cursor.item/vnd.iosched2014.block";
    //
    // /**
    // * "ORDER BY" clauses.
    // */
    // public static final String DEFAULT_SORT = UsersColumns.BLOCK_START + " ASC, "
    // + UsersColumns.BLOCK_END + " ASC";
    //
    // /**
    // * Build {@link Uri} for requested {@link #BLOCK_ID}.
    // */
    // public static Uri buildBlockUri(String blockId) {
    // return CONTENT_URI.buildUpon().appendPath(blockId).build();
    // }
    //
    // /**
    // * Read {@link #BLOCK_ID} from {@link Blocks} {@link Uri}.
    // */
    // public static String getBlockId(Uri uri) {
    // return uri.getPathSegments().get(1);
    // }
    // /**
    // * Generate a {@link #BLOCK_ID} that will always match the requested
    // * {@link Blocks} details.
    // * @param startTime the block start time, in milliseconds since Epoch UTC
    // * @param endTime the block end time, in milliseconds since Epoch UTF
    // */
    // public static String generateBlockId(long startTime, long endTime) {
    // startTime /= DateUtils.SECOND_IN_MILLIS;
    // endTime /= DateUtils.SECOND_IN_MILLIS;
    // return ParserUtils.sanitizeId(startTime + "-" + endTime);
    // }

  }

  public static Uri addCallerIsSyncAdapterParameter(Uri uri) {
    return uri.buildUpon().appendQueryParameter(
        ContactsContract.CALLER_IS_SYNCADAPTER, "true").build();
  }

  public static boolean hasCallerIsSyncAdapterParameter(Uri uri) {
    return TextUtils.equals("true",
        uri.getQueryParameter(ContactsContract.CALLER_IS_SYNCADAPTER));
  }

  /**
   * Adds an account override parameter to the URI.
   * The override parameter instructs the Content Provider to ignore the currently logged in
   * account and use the provided account when fetching account-specific data
   * (such as sessions in My Schedule).
   *
   */

  public static final String OVERRIDE_ACCOUNTNAME_PARAMETER = "overrideAccount";

  public static Uri addOverrideAccountName(Uri uri, String accountName) {
    return uri.buildUpon().appendQueryParameter(
        OVERRIDE_ACCOUNTNAME_PARAMETER, accountName).build();
  }

  public static String getOverrideAccountName(Uri uri) {
    return uri.getQueryParameter(OVERRIDE_ACCOUNTNAME_PARAMETER);
  }



}
