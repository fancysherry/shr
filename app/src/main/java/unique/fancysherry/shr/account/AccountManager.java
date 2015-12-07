package unique.fancysherry.shr.account;

import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import unique.fancysherry.shr.util.LogUtil;
import unique.fancysherry.shr.util.config.LocalConfig;

public class AccountManager implements IAccountManager {

  private List<UserBean> userModelList;
  private Gson mGson;

  private static AccountManager instance;
  private int userIndex = -1;

  public static AccountManager getInstance() {
    if (instance == null) {
      instance = new AccountManager();
    }
    return instance;
  }

  public AccountManager() {
    mGson = new Gson();
    List<AccountBean> accountBeanList;
    String accountString = LocalConfig.getUserAccountString();
    userIndex = LocalConfig.getUserIndex();
    if (!TextUtils.isEmpty(accountString)) {
      accountBeanList =
          mGson.fromJson(accountString, new TypeToken<ArrayList<AccountBean>>() {}.getType());
    } else {
      accountBeanList = new ArrayList<>();
    }
    userModelList = new ArrayList<>();
    for (AccountBean accountBean : accountBeanList) {
      userModelList.add(new UserBean(accountBean));
    }
  }

  @Override
  public List<AccountBean> getAccounts() {
    List<AccountBean> result = new ArrayList<>();
    for (UserBean model : userModelList) {
      result.add(model.getAccountBean());
    }
    return result;
  }

  @Override
  public List<UserBean> getUsers() {
    return userModelList;
  }

  @Override
  public UserBean getCurrentUser() {
    if (userIndex >= 0 && userIndex < userModelList.size()) {
      return userModelList.get(userIndex);
    } else {
      return null;
    }
  }

  @Override
  public int getUserIndex() {
    return userIndex;
  }

  @Override
  public AccountBean getAccount() {
    if (userIndex >= 0 && userIndex < userModelList.size()) {
      return userModelList.get(userIndex).getAccountBean();
    } else {
      return null;
    }
  }

  @Override
  public void modifyAccount(AccountBean bean) {
    int index = getAccountIndex(bean.username);
    if (index != -1) {
      userModelList.get(index).setAccountBean(bean);
    }
    saveAccountData();
  }

  /***
   * 只有登陆时会调用这个方法，故做了有该账号和无该账号的处理。
   * 
   * @param bean
   */
  @Override
  public void addAccount(AccountBean bean) {
    int index = getAccountIndex(bean.username);
    if (index == -1) {
      userModelList.add(new UserBean(bean));
      userIndex = userIndex + 1;
    }
    else
      userIndex = index;// userindex 记录的是当前用户index
    saveAccountData();
  }

  private int getAccountIndex(String accountName) {
    for (int i = 0; i < userModelList.size(); i++) {
      AccountBean bean = userModelList.get(i).getAccountBean();
      if (TextUtils.equals(accountName, bean.username)) {
        return i;
      }
    }
    return -1;
  }

  @Override
  public void deleteAccount(AccountBean bean) {
    int accountIndex = getAccountIndex(bean.username);
    if (accountIndex != -1) {
      saveAccountData();
    }
  }

  private void saveAccountData() {
    LocalConfig.putUserAccountString(mGson.toJson(getAccounts()));
    LogUtil.e("accounts:" + mGson.toJson(getAccounts()));
    LocalConfig.putUserIndex(userIndex);
  }

  @Override
  public void deleteAccount(String username) {
    int accountIndex = getAccountIndex(username);
    if (accountIndex != -1) {
      saveAccountData();
    }
  }

  public void saveNewPassword(String new_password) {
    getCurrentUser().mAccountBean.pwd=new_password;
    saveAccountData();
  }
}
