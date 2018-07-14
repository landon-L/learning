package egova.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 这里配置常量
 * @author yanhenyiduo
 * @date 17:03 2018/7/3
 */
@Configuration
@ComponentScan(basePackages = { "egova.*"})
public class Constants {
    public static final String GUDING_TOKEN = "/v1/auth.do";
    //快照接口
    public static final String GUDING_SNAPSHOT = "/snapshot.do";
    //获取诱导屏状态接口
    public static final String GUDING_STATUS = "/querystatus.do";
    //开启屏幕接口
    public static final String GUDING_OPEN = "/v1/openup.do";
}
