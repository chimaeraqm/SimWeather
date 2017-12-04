package util;

/**
 * Created by Administrator on 2017/11/23.
 */

public interface HttpCallListener {
    void onFinish(String response);
    void onError(Exception e);
}
