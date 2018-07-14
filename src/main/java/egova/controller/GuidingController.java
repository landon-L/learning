package egova.controller;

import egova.base.OperateResult;
import egova.config.Constants;
import egova.model.GuidingModel;
import egova.utils.HttpClientUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import egova.utils.HttpClientExtraUtils;
import egova.utils.JsonUtils;
import egova.utils.MD5Utils;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/guiding")
public class GuidingController {
    private static final Log LOG = LogFactory.getLog(GuidingController.class);

    @Value("${dispatch.guiding.url}")
    private String guidingUrl;

    @Value("${dispatch.guiding.userName}")
    private String userName;
    @Value("${dispatch.guiding.password}")
    private String password;

    /**
     * 快照接口
     * @return
     */
    @GetMapping(value = "/snapshot")
    public OperateResult<String> getSnapshot() {
        OperateResult<String> or = new OperateResult<>();
        try {
            return or.success(snapshot(guidingUrl + Constants.GUDING_SNAPSHOT), "获取诱导屏接口图片成功");
        } catch (Exception e) {
            or.error("获取快照接口异常");
            LOG.error(e);
        }
        return or;
    }

    /**
     * 获取快照
     * @param url
     * @return
     */
    private String snapshot(String url) {
        try {
            String data = HttpClientUtils.streamToBase64(url);
            LOG.info("快照接口get返回：" + url + data);//解析获取的token
            return data;
        } catch (Exception e) {
            LOG.error("获取快照异常", e);
        }
        return null;
    }

    /**
     * 根据用户名密码获取诱导屏token
     * @return
     */
    private String token(String url) {
        Map<String,String> data = new HashMap<>();
        data.put("userName", userName);
        data.put("password", MD5Utils.md5(password));
        String jsonStr = HttpClientExtraUtils.post(url + Constants.GUDING_TOKEN,null, data).getResult();
        //解析获取的token
        if (StringUtils.isEmpty(jsonStr)) {
            LOG.error("获取鉴权数据为空，请检查");
            return null;
        }
        GuidingModel model = JsonUtils.deserialize(jsonStr, GuidingModel.class);
        if (model.getCode().equals("0")) {
            LOG.error("获取鉴权数据失败" + model.getMsg());
            return null;
        }
        return model.getToken();
    }

    /**
     * 开启屏幕
     */
    private boolean open(String url) {
        try {
            String token = token(url);
            if (StringUtils.isEmpty(token)) {
                return false;
            }
            Map<String, String> data = new HashMap<>();
            data.put("token", token);
            String jsonStr = HttpClientExtraUtils.post(url + Constants.GUDING_OPEN,null, data).getResult();
            //解析获取的token
            GuidingModel model = JsonUtils.deserialize(jsonStr, GuidingModel.class);
            if (model != null && model.getCode().equals("1")) {
                LOG.info("开启屏幕成功" + model.getMsg());
                return true;
            }
        } catch (Exception e) {
            LOG.error("开启屏幕异常", e);
        }
        return false;
    }

    /**
     * 获取状态
     * @return
     */
    private boolean status(String url) {
        try {
            String jsonStr = HttpClientExtraUtils.get(url + Constants.GUDING_STATUS).getResult();
            //解析获取的token
            GuidingModel model = JsonUtils.deserialize(jsonStr, GuidingModel.class);
            if (model != null && model.getCode().equals("1")) {
                LOG.error("获取屏幕状态成功" + model.getMsg());
                return true;
            }
        } catch (Exception e) {
            LOG.info("获取状态异常");
        }
        return false;
    }
}
