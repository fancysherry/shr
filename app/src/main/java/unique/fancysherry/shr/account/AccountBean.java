package unique.fancysherry.shr.account;

public class AccountBean {

  public String username;
  public String pwd;
  public String nickname;
  public String id;

  public AccountBean(String username, String pwd, String nickname, String id) {
    this.username = username;
    this.pwd = pwd;
    this.nickname = nickname;
    this.id = id;
  }

  public AccountBean(String username, String pwd) {
    this.username = username;
    this.pwd = pwd;
  }
}
