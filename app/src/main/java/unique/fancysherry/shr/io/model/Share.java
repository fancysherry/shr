package unique.fancysherry.shr.io.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by fancysherry on 15-7-8.
 */
public class Share implements Parcelable {
  public String id;
  public String title;
  public String comment_sum;
  public String url;
  public String intro;
  public String group; // share 所在组
  public String share_time;
  public User origin;
  public String gratitude_sum;
  public String type;

  public List<User> others;

  public Share()
  {}

  public Share(String type) {
    this.type = type;
  }

  // 1.必须实现Parcelable.Creator接口,否则在获取Person数据的时候，会报错，如下：
  // android.os.BadParcelableException:
  // Parcelable protocol requires a Parcelable.Creator object called CREATOR on class
  // com.um.demo.Person
  // 2.这个接口实现了从Percel容器读取Person数据，并返回Person对象给逻辑层使用
  // 3.实现Parcelable.Creator接口对象名必须为CREATOR，不如同样会报错上面所提到的错；
  // 4.在读取Parcel容器里的数据事，必须按成员变量声明的顺序读取数据，不然会出现获取数据出错
  // 5.反序列化对象
  public static final Parcelable.Creator<Share> CREATOR = new Creator<Share>() {
    @Override
    public Share createFromParcel(Parcel source) {
      // TODO Auto-generated method stub
      // 必须按成员变量声明的顺序读取数据，不然会出现获取数据出错
      return new Share(source);
    }

    @Override
    public Share[] newArray(int size) {
      // TODO Auto-generated method stub
      return new Share[size];
    }
  };

  private Share(Parcel in)
  {
    id = in.readString();
    title = in.readString();
    comment_sum = in.readString();
    url = in.readString();
    intro = in.readString();
    group = in.readString();
    share_time = in.readString();
    origin = in.readParcelable(getClass().getClassLoader());
    gratitude_sum = in.readString();
    type = in.readString();
    in.readList(others, getClass().getClassLoader());


  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(id);
    dest.writeString(title);
    dest.writeString(comment_sum);
    dest.writeString(url);
    dest.writeString(intro);
    dest.writeString(group);
    dest.writeString(share_time);
    dest.writeParcelable(origin, flags);
    dest.writeString(gratitude_sum);
    dest.writeList(others);
    dest.writeString(type);
  }

  public String getType()
  {
    return type;
  }
}
