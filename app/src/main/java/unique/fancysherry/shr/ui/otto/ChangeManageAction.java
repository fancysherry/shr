package unique.fancysherry.shr.ui.otto;

/**
 * Created by fancysherry on 15-12-3.
 */
public class ChangeManageAction {
  public final static String VERIFY_YES="yes";
  public final static String VERIFY_NO="no";
  private String verify="default";
  private String user_id;
  private String user_name;
  private boolean finished=false;

  public String getUser_name() {
    return user_name;
  }

  public void setUser_name(String pUser_name) {
    user_name = pUser_name;
  }

  public String getUser_id() {
    return user_id;
  }

  public void setUser_id(String pUser_id) {
    user_id = pUser_id;
  }

  public String getVerify() {
    return verify;
  }

  public void setVerify(String pVerify) {
    verify = pVerify;
  }

  public boolean isFinished() {
    return finished;
  }

  public void setFinished(boolean pFinished) {
    finished = pFinished;
  }
}
