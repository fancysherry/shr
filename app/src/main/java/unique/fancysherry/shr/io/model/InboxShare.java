package unique.fancysherry.shr.io.model;

/**
 * Created by fancysherry on 15-7-8.
 */
public class InboxShare {
  public String id;
  public String title;
  public String url;
  public String send_time;
  public String nickname;
  public String uid;// id of user
  public String avatar;// avatar of user
  public String type;
  public String content;

  public InboxShare(String type) {
    this.type = type;
  }

  public String getType()
  {
    return type;
  }
}
