package unique.fancysherry.shr.account;

import java.util.List;


public interface IAccountManager {
  public List<AccountBean> getAccounts();

  public List<UserBean> getUsers();

  public UserBean getCurrentUser();

  public int getUserIndex();

  public AccountBean getAccount();

  public void modifyAccount(AccountBean bean);

  public void addAccount(AccountBean bean);

  public void deleteAccount(AccountBean bean);

  public void deleteAccount(String userName);
}
