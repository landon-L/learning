package egova.model;

import java.io.Serializable;

public class GuidingModel implements Serializable{
    private static final long serialVersionUID = -7026900768704885180L;
    private String code;
    private String msg;
    private String token;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
