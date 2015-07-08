package unique.fancysherry.shr.io.model;

import java.util.List;

/**
 * Created by fancysherry on 15-7-8.
 */
public class Group {
    public String id;
    public String name;
    public String apply_user_id;
    public String apply_user_name;
    public String creater_time;
    public String admin_name;
    public String admin_id;
    public String group_user_sum;
    public List<User> users;
    public List<Share> shares;
}
