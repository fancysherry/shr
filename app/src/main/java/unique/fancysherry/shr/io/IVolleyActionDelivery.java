package unique.fancysherry.shr.io;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;

import unique.fancysherry.shr.io.model.Group;

/**
 * Created by suanmiao on 15/1/19.
 */
public interface IVolleyActionDelivery<T>{
    public Response<T> parseNetworkResponse(NetworkResponse response);


}