//package egova.controller;
//
//import egova.base.OperateResult;
//import egova.config.Constants;
//import egova.model.GuidingModel;
//import egova.utils.HttpClientExtraUtils;
//import egova.utils.HttpClientUtils;
//import egova.utils.JsonUtils;
//import egova.utils.MD5Utils;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.util.StringUtils;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.servlet.http.HttpServletResponse;
//import java.io.*;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.HashMap;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/test/guiding")
//public class GuidingTestController {
//    private static final Log LOG = LogFactory.getLog(GuidingTestController.class);
//
//    @Value("${dispatch.guiding.url}")
//    private String guidingUrl;
//    @Value("${dispatch.guiding.userName}")
//    private String userName;
//    @Value("${dispatch.guiding.password}")
//    private String password;
//
//    @GetMapping(value = "/snapshot02")
//    public OperateResult<byte[]> getSnapshotTest02() {
//        OperateResult<byte[]> or = new OperateResult<>();
//        //返回为图片字节流
//        String snapUrl = guidingUrl + Constants.GUDING_SNAPSHOT;
////        String snapUrl = "http://localhost:8080/guiding/getPic";
//        try {
//            byte[] picData = HttpClientUtils.getByteArray(snapUrl);
//            LOG.info("快照接口get返回：" + picData.length);//解析获取的token
//            or.success(picData, "快照接口返回成功");
//        } catch (Exception e) {
//            or.exception("获取快照接口异常", e);
//        }
//        return or;
//    }
//
//    @GetMapping(value = "/snapshot03")
//    public OperateResult<String> getSnapshotTest03() {
//        OperateResult<String> or = new OperateResult<>();
//        //返回为图片字节流
//        String snapUrl = guidingUrl + Constants.GUDING_SNAPSHOT;
////        String snapUrl = "http://localhost:8080/guiding/getPic";
//        try {
//            String data = HttpClientExtraUtils.get(snapUrl).getResult();
//            LOG.info("快照接口get返回：" + data);//解析获取的token
//            or.success(data, "快照接口返回成功");
//        } catch (Exception e) {
//            or.exception("获取快照接口异常", e);
//        }
//        return or;
//    }
//    /**
//     * 根据用户名密码获取诱导屏token  成功
//     * @return
//     */
//    @GetMapping("/getAuth")
//    private String getAuth() {
//        Map<String,String> data = new HashMap<>();
//        data.put("userName", userName);
//        data.put("password", MD5Utils.md5(password));
//
//        Map<String,String> header = new HashMap<>();
//        header.put("Content-Type", "application/x-www-form-urlencoded");
//        // TODO: 2018/7/5 模拟post测试
////        String url = "http://localhost:8080/guiding/auth";
//        String jsonStr = HttpClientExtraUtils.post(guidingUrl + Constants.GUDING_TOKEN, header, data).getResult();
////        String jsonStr = HttpClientUtils.getPostString(guidingUrl + Constants.GUDING_TOKEN, data);
//        LOG.info("鉴权接口返回：" + jsonStr);//解析获取的token
//        if (StringUtils.isEmpty(jsonStr)) {
//            LOG.error("获取鉴权数据为空，请检查");
//            return null;
//        }
//        GuidingModel model = JsonUtils.deserialize(jsonStr, GuidingModel.class);
//        if (model.getCode().equals("0")) {
//            LOG.error("获取鉴权数据失败" + model.getMsg());
//            return null;
//        }
//        return model.getToken();
//    }
//
//    /**
//     * 开启屏幕, 没有打开，则调用鉴权接口拿token,然后打开屏幕
//     * @return
//     */
//    @GetMapping(value = "/openUp")
//    private boolean openUp() {
//        boolean isOpen = status();
//        //屏幕已经打开，不用多次打开
//        if (isOpen) {
//            return true;
//        }
//        String token = getAuth();
//        if (StringUtils.isEmpty(token)) {
//            return false;
//        }
//        Map<String, String> data = new HashMap<>();
//        data.put("token", token);
//
//        Map<String,String> header = new HashMap<>();
//        header.put("Content-Type", "application/x-www-form-urlencoded");
//        String jsonStr = HttpClientExtraUtils.post(guidingUrl + Constants.GUDING_OPEN, header, data).getResult();
////        String jsonStr = HttpClientUtils.getPostString(guidingUrl + Constants.GUDING_OPEN, data);
//        //解析获取的token
//        LOG.info("打开屏幕获取json" + jsonStr);
//        GuidingModel model = JsonUtils.deserialize(jsonStr, GuidingModel.class);
//        if (model != null && model.getCode().equals("1")) {
//            LOG.info("开启屏幕成功" + model.getMsg());
//            return true;
//        }
//        return false;
//    }
//
//    /**
//     * 开启屏幕
//     * @return
//     */
//    @GetMapping(value = "/status")
//    private boolean status(){
////        String url = "http://localhost:8080/guiding/getStatus";
//        String jsonStr = HttpClientExtraUtils.get(guidingUrl + Constants.GUDING_STATUS).getResult();
////        String jsonStr = HttpClientUtils.getString(guidingUrl + Constants.GUDING_STATUS);
//        //解析获取的token
//        LOG.info("jsonStr=" + jsonStr);
//        if (StringUtils.isEmpty(jsonStr)) {
//            LOG.error("获取状态接口失败");
//            return false;
//        }
//        GuidingModel model = JsonUtils.deserialize(jsonStr, GuidingModel.class);
//        if ((model != null) && (!model.getCode().equals("0"))) {
//            LOG.info("获取屏幕状态成功" + model.getMsg());
//            return true;
//        }
//        return false;
//    }
//
//    /**
//     * 从输入流中获取数据
//     * @param inStream
//     * @return
//     * @throws Exception
//     */
//    public static String readInputStream(InputStream inStream) throws Exception {
//        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//        byte[] buffer = new byte[10240];
//        int len = 0;
//        while ((len = inStream.read(buffer)) != -1) {
//            outStream.write(buffer, 0, len);
//        }
//        inStream.close();
//        return outStream.toString();
//    }
//}
