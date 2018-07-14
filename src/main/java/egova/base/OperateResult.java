package egova.base;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 返回结果包装类
 * @author yanhenyiduo
 * @date 16:54 2018/7/3
 */
public class OperateResult<T> extends ExtendObject{
    private static Log LOG = LogFactory.getLog(OperateResult.class);

    private boolean hasError;
    private T result;
    private String message;
    private String tag;

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public OperateResult<T> error(String message) {
        this.message = message;
        this.hasError = true;
        return this;
    }

    public OperateResult<T> exception(String message, Exception e) {
        this.message = message;
        this.hasError = true;
        LOG.error(message, e);
        return this;
    }

    public OperateResult<T> success(T result, String message) {
        this.result = result;
        this.hasError = false;
        this.message = message;
        return this;
    }

    public boolean isHasError() {
        return hasError;
    }

    public void setHasError(boolean hasError) {
        this.hasError = hasError;
    }
}
