package unique.fancysherry.shr.io.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fancysherry on 15-7-8.
 */
public class ShareOrigin implements Parcelable {
  private String nickname;// name of fisrt author
  private String id; // id of first author
  private String avatar;
  private List<User> others; // 其他转发者

  // 1.必须实现Parcelable.Creator接口,否则在获取Person数据的时候，会报错，如下：
  // android.os.BadParcelableException:
  // Parcelable protocol requires a Parcelable.Creator object called CREATOR on class
  // com.um.demo.Person
  // 2.这个接口实现了从Percel容器读取Person数据，并返回Person对象给逻辑层使用
  // 3.实现Parcelable.Creator接口对象名必须为CREATOR，不如同样会报错上面所提到的错；
  // 4.在读取Parcel容器里的数据事，必须按成员变量声明的顺序读取数据，不然会出现获取数据出错
  // 5.反序列化对象
  public static final Parcelable.Creator<ShareOrigin> CREATOR = new Creator<ShareOrigin>() {
    @Override
    public ShareOrigin createFromParcel(Parcel source) {
      // TODO Auto-generated method stub
      // 必须按成员变量声明的顺序读取数据，不然会出现获取数据出错
      return new ShareOrigin(source);
    }

    @Override
    public ShareOrigin[] newArray(int size) {
      // TODO Auto-generated method stub
      return new ShareOrigin[size];
    }
  };

  private ShareOrigin(Parcel in)
  {
    nickname = in.readString();
    id = in.readString();
    avatar = in.readString();

    others = new ArrayList<>();
    in.readList(others, getClass().getClassLoader());
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(nickname);
    dest.writeString(id);
    dest.writeString(avatar);
    dest.writeList(others);
  }
}
