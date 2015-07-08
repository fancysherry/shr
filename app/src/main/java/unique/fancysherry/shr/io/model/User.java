package unique.fancysherry.shr.io.model;

import java.util.List;

/**
 * Created by fancysherry on 15-7-8.
 */
public class User {
    public String uid;
    public String nickname;
    public String avatar;
    public String brief;
    public boolean is_man;
    public String register_time;
    public List<Group> groups;
    public List<Share> shares;

    public String gratitude_shares_sum;
    public String commment_sum;
    public String black_users_sum;
    public String followers_sum;
    public String followings_sum;

    public String email;
    public String education_information;
    public String phone_number;

}
