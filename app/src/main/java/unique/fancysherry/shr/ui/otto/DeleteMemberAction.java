package unique.fancysherry.shr.ui.otto;

import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by fancysherry on 15-12-3.
 */
public class DeleteMemberAction {
  public final static String VERIFY_YES="yes";
  public final static String VERIFY_NO="no";
  private String verify="default";
  private String user_id;
  private String user_name;

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
}
