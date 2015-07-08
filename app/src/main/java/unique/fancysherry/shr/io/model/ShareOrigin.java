package unique.fancysherry.shr.io.model;

import java.util.List;

/**
 * Created by fancysherry on 15-7-8.
 */
public class ShareOrigin {
    private String nickname;//name of fisrt author
    private String id; //id of first author
    private String avatar;
    private List<User> others; //其他转发者
}
