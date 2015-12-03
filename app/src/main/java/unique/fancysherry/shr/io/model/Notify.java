package unique.fancysherry.shr.io.model;

/**
 * Created by fancysherry on 15-12-3.
 */
public class Notify {
    /**
     * NOTIFY TYPE
     */
  public static final String COMMENT = "COMMENT";
  public static final String SHARE = "SHARE";
  public static final String FOLLOW = "FOLLOW";
  public static final String GRATITUDE = "GRATITUDE";
  public static final String ADMIN = "ADMIN";
  public static final String INVITE = "INVITE";
  public static final String FRESH_MEMBER = "FRESH_MEMBER";

  public String id;
  public String notify_type;
  public String time;
  public String key;
  public String group_id;
  public String group_name;
  public String user_id;
  public String nickname;
  public String avatar;
  public boolean isfinish=false;
}
