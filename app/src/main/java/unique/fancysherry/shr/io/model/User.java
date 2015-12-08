package unique.fancysherry.shr.io.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fancysherry on 15-7-8.
 */
public class User implements Parcelable, Comparable {
  public String reason;
  public String uid;
  public String id;
  public String nickname;
  public String name;
  public String avatar;
  public String brief;
  public String is_man;
  public String register_time;
  public List<Group> groups;
  public List<Share> shares;
  public List<Group> manager_groups;
  public int gratitude_shares_sum;
  public String comment_sum;
  public String black_users_sum;
  public String followers_sum;
  public String followings_sum;
  public String email;
  public String education_information;
  public String phone_number;
  public int share_sum;

  // 1.必须实现Parcelable.Creator接口,否则在获取Person数据的时候，会报错，如下：
  // android.os.BadParcelableException:
  // Parcelable protocol requires a Parcelable.Creator object called CREATOR on class
  // com.um.demo.Person
  // 2.这个接口实现了从Percel容器读取Person数据，并返回Person对象给逻辑层使用
  // 3.实现Parcelable.Creator接口对象名必须为CREATOR，不如同样会报错上面所提到的错；
  // 4.在读取Parcel容器里的数据事，必须按成员变量声明的顺序读取数据，不然会出现获取数据出错
  // 5.反序列化对象
  public static final Parcelable.Creator<User> CREATOR = new Creator<User>() {
    @Override
    public User createFromParcel(Parcel source) {
      // TODO Auto-generated method stub
      // 必须按成员变量声明的顺序读取数据，不然会出现获取数据出错
      return new User(source);
    }

    @Override
    public User[] newArray(int size) {
      // TODO Auto-generated method stub
      return new User[size];
    }
  };

  private User(Parcel in)
  {
    reason = in.readString();
    uid = in.readString();
    id = in.readString();
    nickname = in.readString();
    name = in.readString();
    avatar = in.readString();
    brief = in.readString();
    is_man = in.readString();
    register_time = in.readString();
    groups = new ArrayList<Group>();
    shares = new ArrayList<Share>();
    manager_groups = new ArrayList<Group>();
    in.readList(groups, getClass().getClassLoader());
    in.readList(shares, getClass().getClassLoader());
    in.readList(manager_groups, getClass().getClassLoader());
    gratitude_shares_sum = in.readInt();
    comment_sum = in.readString();
    black_users_sum = in.readString();
    followers_sum = in.readString();
    followings_sum = in.readString();
    email = in.readString();
    education_information = in.readString();
    phone_number = in.readString();
    share_sum = in.readInt();
  }


  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(reason);
    dest.writeString(uid);
    dest.writeString(id);
    dest.writeString(nickname);
    dest.writeString(name);
    dest.writeString(avatar);
    dest.writeString(brief);
    dest.writeString(is_man);
    dest.writeString(register_time);
    dest.writeList(groups);
    dest.writeList(shares);
    dest.writeList(manager_groups);
    dest.writeInt(gratitude_shares_sum);
    dest.writeString(comment_sum);
    dest.writeString(black_users_sum);
    dest.writeString(followers_sum);
    dest.writeString(followings_sum);
    dest.writeString(email);
    dest.writeString(education_information);
    dest.writeString(phone_number);
    dest.writeInt(share_sum);

  }

  /**
   * * 这里表示按id从小到大排序，如果该对象小于、等于或大于指定对象Object o，则分别返回负整数、零或正整数
   * 如果需要从大到小排序，则如果该对象小于、等于或大于指定对象Object o，则分别返回正整数、零或负整数
   * 
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   * @param another
   * @return
   */
  @Override
  public int compareTo(Object another) {
    User mUser = (User) another;
    if (share_sum < mUser.share_sum) {
      return 1;
    }
    if (share_sum > mUser.share_sum) {
      return -1;
    }
    return 0;
  }
}
