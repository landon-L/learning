package egova.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import java.io.IOException;
import java.util.ArrayList;

public class JsonUtils {


    private static Log LOG = LogFactory.getLog(JsonUtils.class);

    private static ObjectMapper mapper = new ObjectMapper();
    
    public JsonUtils(){
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static <T> T deserializeByType(String json, TypeReference type) {
        try {
            @SuppressWarnings("unchecked")
            T d = (T) mapper.readValue(json,type);
            return d;
        } catch (Exception e) {
            LOG.error("类型对象解析出错",e);
            throw new RuntimeException( "类型对象解析出错");
        }
    }

    public static <T> T deserialize(String json, Class<?> clazz) {
        try {
            @SuppressWarnings("unchecked")
            T d = (T) mapper.readValue(json, clazz);
            return d;
        } catch (IOException e) {
            LOG.error(clazz + "类型对象解析出错",e);
            throw new RuntimeException(clazz + "类型对象解析出错");
        }
    }

    public static <T> java.util.List<T> deserializeList(String json,
            Class<?> clazz) {
        try {
            if(StringUtils.isBlank(json)){
                return new ArrayList<T>();
            }
            JavaType type = getCollectionType(mapper, ArrayList.class, clazz);
            @SuppressWarnings("unchecked")
            java.util.List<T> d = mapper.readValue(json,type);
            return d;
        } catch (IOException e) {
            LOG.error(clazz + "类型对象集合解析出错",e);
            throw new RuntimeException(clazz + "类型对象集合解析出错");
        }
    }

    public static String serialize(Object value) {
        try {
            if (value == null){
                return null;
            }
            //解析对象为文件时则系列化该对象，否则出现解析失败 edit by ldy
            if (value instanceof StandardMultipartHttpServletRequest) {
                return null;
            }
            String json = mapper.writeValueAsString(value);
            return json;
        } catch (JsonProcessingException e) {
            LOG.error(value.getClass() + "类型对象解析出错",e);
            throw new RuntimeException(value.getClass() + "类型对象解析出错");
        }
    }

    public static JavaType getCollectionType(ObjectMapper mapper,
                                             Class<?> collectionClass, Class<?> elementClasses) {
        return mapper.getTypeFactory().constructParametrizedType(
                collectionClass, collectionClass, elementClasses);
    }
}
