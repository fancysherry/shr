package unique.fancysherry.shr.io.request;

import com.android.volley.Response;

import unique.fancysherry.shr.io.model.Group;

/**
 * Created by fancysherry on 15-7-13.
 */
public class GroupShareRequest extends BaseRequest<Group> {
    public GroupShareRequest(int method, String url, Response.ErrorListener listener) {
        super(method, url, listener);
    }
}
