package egova.base;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

/**
 * 实体类扩展
 * @author yanhenyiduo
 * @date 16:53 2018/7/3
 */
public class ExtendObject implements Serializable{
    private static final long serialVersionUID = 256048600743981280L;

    private transient Map<String, Object> extras = Collections.EMPTY_MAP;
    public void set(String name, Object value) {
        extras.put(name, value);
    }
    public Object get(String name) {
        return extras.get(name);
    }
}
