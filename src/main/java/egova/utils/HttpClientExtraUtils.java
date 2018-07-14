package egova.utils;

import egova.base.OperateResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.conn.ConnectTimeoutException;

import java.net.SocketTimeoutException;
import java.util.Map;

public class HttpClientExtraUtils {
    private static Log LOG = LogFactory.getLog(HttpClientExtraUtils.class.getName());

    public static OperateResult<String> get(String url) {
        OperateResult<String> or = new OperateResult<String>();
        or.setHasError(false);
        try {
            or.setResult(HttpClientUtils.get(url, "utf-8"));
            return or;
        } catch (Exception e) {
            return handleException(or, e);
        }
    }

    public static OperateResult<String> post(String url, Map<String, String> headers, Map<String, String> data) {

        OperateResult<String> or = new OperateResult<String>();
        or.setHasError(false);

        try {
            or.setResult(HttpClientUtils.post(url, data, headers, null, null));
        } catch (Exception e) {
            handleException(or, e);
        }

        return or;
    }

    public static OperateResult<String> post(String url, Map<String, String> headers, String data) {

        OperateResult<String> or = new OperateResult<>();

        or.setHasError(false);

        try {
            or.setResult(HttpClientUtils.post(url, data, headers, null, null));
        } catch (Exception e) {
            handleException(or, e);
        }

        return or;
    }

    protected  static   OperateResult<String> handleException(OperateResult<String> or, Exception e) {
        if (e instanceof ConnectTimeoutException) {
            LOG.error("服务请求超时", e);
            or.setTag("408");
            or.error("服务请求超时");
        } else if (e instanceof SocketTimeoutException) {
            LOG.error("数据发送超时", e);
            or.setTag("413");
            or.error("数据发送超时");
        } else {
            LOG.error("未知异常", e);
            or.setTag("500");
            or.error("未知异常");
        }
        return or;
    }
}
