package unique.fancysherry.shr.ui.otto;

/**
 * Created by fancysherry on 15-11-29.
 */
public class DataChangeAction {
  // 定义了数据改变的类型
  public static final String CHANGE_AVATAR = "change avatar";
  public static final String ADD_GROUP = "add group";
  public static final String DELETE_GROUP = "delete group";

  private String str;

  public String getStr() {
    return str;
  }

  public void setStr(String pStr) {
    str = pStr;
  }
}
