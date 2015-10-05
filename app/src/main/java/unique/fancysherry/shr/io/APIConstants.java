package unique.fancysherry.shr.io;

/**
 * Created by fancysherry on 15-7-5.
 */
public class APIConstants {


  /**
   * base url
   */
   public static final String BASE_URL = "http://104.236.46.64:8888";

//  public static final String BASE_URL = "http://139.129.22.121:23333";


  /***
   * user
   * 查看个人主页内容，包括分享的share. 如果加上可选的uid，则可以看到这个uid对应用户的信息，
   * 其同组的share可见，否则不可见. 带optional的返回字段仅自己可见.
   */
  public static final String PATH_USER_INVITE = "/user/invite"; // post
  public static final String PATH_USER_NOTIFY = "/user/notify"; // get
  public static final String PATH_USER_LOGIN = "/login"; // POST


  public static final String PATH_GROUP_APPLY = "/group/apply";
  public static final String PATH_LOGOUT = "/logout";
  public static final String PATH_USER_REGISTER_KEY = "/register/:key"; // key of invite
  public static final String PATH_USER_REGISTER = "/register"; // 直接注册
  public static final String PATH_USER_ACCEPT = "/user/accept/:key";
  public static final String PATH_USER_BLACK = "/user/black"; // 拉黑
  public static final String PATH_USER_CANCEL_BLACK = "/user/cancel_black"; // 拉黑
  public static final String PATH_USER_FOLLOW = "/user/follow"; // ?
  public static final String PATH_USER_CANCEL_FOLLOW = "/user/cancel_follow"; //
  public static final String PATH_USER_RESET_SETTING = "/setting"; // 修改个人设置 post
  public static final String PATH_USER_SETTING = "/setting"; // 获取个人设置 get
  public static final String PATH_USER_INFORMATION = "/homepage[?uid=:uid]"; // 个人主页 登陆权限

  /**
   * ShareGroup
   */
  public static final String PATH_GROUP_ACCEPT = "/group/accept";
  public static final String PATH_GROUP_REJECT = "/group/reject";
  public static final String PATH_GROUP_SEARCH = "/group"; // ?group_name=:group_name 搜索share组
  public static final String PATH_GROUP_CREATE = "/group"; // 新建一个share组
  public static final String PATH_GROUP_INFO = "/group/info"; // 查询组信息，包括成员
  public static final String PATH_GROUP_CHANGE_ADMIN = "/group/change_admin"; // 管理员转让
  public static final String PATH_GROUP_APPLY_USERS = "/group/apply_users";// ?group_id=:group_id
  public static final String PATH_GROUP_GETSHARES = "/group/shares"; // ?group_id=:group_id
  public static final String PATH_GROUP_GETUSERS = "/group/users"; // group_id=:group_id 获取组内的user信息

  /**
   * Share
   */
  public static final String PATH_SHARE_DELETE = "/share"; // delete share
  public static final String PATH_SHARE_CANCEL_GRATITUDE = "/gratitude"; // cancel gratitude
  public static final String PATH_SHARE_GRATITUDE = "/gratitude"; // gratitude
  public static final String PATH_SHARE = "/share"; // share from other app
  public static final String PATH_SHARE_SEARCH = "/share"; // 获取组内的某条share share_id=:share_id


  /**
   * InboxShare
   */


  public static final String PATH_INBOX_SHARE_DELETE = "/inbox_share"; // delete
  public static final String PATH_INBOX_SHARE = "/share"; // 与投递到某个组的接口几乎相同，仅是groups为空数组.投递InboxShare（外部）
  public static final String PATH_INBOX_TO_GROUP = "/inbox_share"; // 通过inbox_share_id和group_id推送InboxShare到特定的组.
  public static final String PATH_INBOX_SHARE_GET = "/inbox_share"; // 根据inbox_share_id从inbox中获取share，即从@me中获取share.
  // ?inbox_share_id=:inbox_share_id

  /**
   * Comment
   */
  public static final String PATH_COMMENT_DELETE = "/comment"; // delete
  public static final String PATH_COMMENT_SEND = "/comment"; // send
  public static final String PATH_COMMENT_GET = "/comment"; // ?share_id=:share_id


}
