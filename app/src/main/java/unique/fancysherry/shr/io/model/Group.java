package unique.fancysherry.shr.io.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fancysherry on 15-7-8.
 */
public class Group implements Parcelable {
  public String group_id;
  public String group_name;
  public String apply_user_id;
  public String apply_user_name;
  public String create_time;
  public String admin_name;
  public String admin_id;
  public String group_user_sum;
  public List<User> users;
  public List<Share> shares;
  // public List<User> admin;
  public User admin;
  public String group_intro;
  public String name;
  public String id;

  public Group()
  {}

  // 1.必须实现Parcelable.Creator接口,否则在获取Person数据的时候，会报错，如下：
  // android.os.BadParcelableException:
  // Parcelable protocol requires a Parcelable.Creator object called CREATOR on class
  // com.um.demo.Person
  // 2.这个接口实现了从Percel容器读取Person数据，并返回Person对象给逻辑层使用
  // 3.实现Parcelable.Creator接口对象名必须为CREATOR，不如同样会报错上面所提到的错；
  // 4.在读取Parcel容器里的数据事，必须按成员变量声明的顺序读取数据，不然会出现获取数据出错
  // 5.反序列化对象
  public static final Parcelable.Creator<Group> CREATOR = new Creator<Group>() {
    @Override
    public Group createFromParcel(Parcel source) {
      // TODO Auto-generated method stub
      // 必须按成员变量声明的顺序读取数据，不然会出现获取数据出错
      return new Group(source);
    }

    @Override
    public Group[] newArray(int size) {
      // TODO Auto-generated method stub
      return new Group[size];
    }
  };

  private Group(Parcel in)
  {
    group_id = in.readString();
    group_name = in.readString();
    apply_user_id = in.readString();
    apply_user_name = in.readString();
    create_time = in.readString();
    admin_name = in.readString();
    admin_id = in.readString();
    group_user_sum = in.readString();

    users = new ArrayList<>();
    in.readList(users, getClass().getClassLoader());
    shares = new ArrayList<>();
    in.readList(shares, getClass().getClassLoader());
    admin = in.readParcelable(getClass().getClassLoader());
    group_intro = in.readString();
    name = in.readString();
    id = in.readString();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(group_id);
    dest.writeString(group_name);
    dest.writeString(apply_user_id);
    dest.writeString(apply_user_name);
    dest.writeString(create_time);
    dest.writeString(admin_name);
    dest.writeString(admin_id);
    dest.writeString(group_user_sum);
    dest.writeList(users);
    dest.writeList(shares);
    dest.writeParcelable(admin, flags);
    dest.writeString(group_intro);
    dest.writeString(name);
    dest.writeString(id);


  }
}
