package com.example.demo.encrypt.init;

import com.example.demo.encrypt.annotation.Encrypt;
import com.example.demo.encrypt.constant.HttpMethodTypePrefixConstants;
import com.example.demo.encrypt.constant.PathConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Api解密数据初始化
 *
 * @author 马亮
 * @date 2021/3/8 15:13
 */
@Slf4j
public class ApiDecryptDataInit implements ApplicationContextAware {
	/**
	 * 需要对响应内容进行加密的接口URI
	 * 比如：/user/list
	 */
	public static List<String> responseEncryptUriList = new ArrayList<>();

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		Map<String, Object> beanMap = ctx.getBeansWithAnnotation(Controller.class);
        initData(beanMap);
    }

	/**
	 * 初始化需要解密的接口和请求参数
	 *
	 * @param beanMap
	 */
	private void initData(Map<String, Object> beanMap) {
		if (beanMap != null) {
            for (Object bean : beanMap.values()) {
                Class<?> clz = bean.getClass();
                Method[] methods = clz.getMethods();
                for (Method method : methods) {
					// 加密初始化
					Encrypt encrypt = AnnotationUtils.findAnnotation(method, Encrypt.class);
                    if (encrypt != null) {
                    	String uri = encrypt.value();
                    	if (!StringUtils.hasText(uri)) {
                    		uri = getApiUri(clz, method);
						}
                        responseEncryptUriList.add(uri);
                    }
                }
            }
        }
		log.info("【responseEncryptUriList】 {}", responseEncryptUriList);
	}

	/**
	 * 构造请求类型和请求地址并返回
	 *
	 * @param clz
	 * @param method
	 * @return
	 */
    private String getApiUri(Class<?> clz, Method method) {
    	String methodType = "";
        StringBuilder uri = new StringBuilder();

        // Controller类有@RequestMapping的情况
        RequestMapping reqMapping = AnnotationUtils.findAnnotation(clz, RequestMapping.class);
        if (reqMapping != null) {
        	uri.append(formatUri(reqMapping.value()[0]));
		}

        GetMapping getMapping = AnnotationUtils.findAnnotation(method, GetMapping.class);
		if (getMapping != null) {
			methodType = HttpMethodTypePrefixConstants.GET;
			if (uri.toString().endsWith(PathConstants.PATH_SEPARATOR)) {
				uri.delete(uri.length()-1, uri.length());
			}
			uri.append(formatUri(getMapping.value()[0]));
		}
        PostMapping postMapping = AnnotationUtils.findAnnotation(method, PostMapping.class);
		if (postMapping != null) {
			methodType = HttpMethodTypePrefixConstants.POST;
			if (uri.toString().endsWith(PathConstants.PATH_SEPARATOR)) {
				uri.delete(uri.length()-1, uri.length());
			}
			uri.append(formatUri(postMapping.value()[0]));
		}
        PutMapping putMapping = AnnotationUtils.findAnnotation(method, PutMapping.class);
		if (putMapping != null) {
			methodType = HttpMethodTypePrefixConstants.PUT;
			if (uri.toString().endsWith(PathConstants.PATH_SEPARATOR)) {
				uri.delete(uri.length()-1, uri.length());
			}
			uri.append(formatUri(putMapping.value()[0]));
		}
        DeleteMapping deleteMapping = AnnotationUtils.findAnnotation(method, DeleteMapping.class);
		if (deleteMapping != null) {
			methodType = HttpMethodTypePrefixConstants.DELETE;
			if (uri.toString().endsWith(PathConstants.PATH_SEPARATOR)) {
				uri.delete(uri.length()-1, uri.length());
			}
			uri.append(formatUri(deleteMapping.value()[0]));
		}
		RequestMapping requestMapping = AnnotationUtils.findAnnotation(method, RequestMapping.class);
		if (requestMapping != null && !StringUtils.hasText(methodType)) {
        	// 没有注明method时默认是GET请求
			RequestMethod requestMethod = RequestMethod.GET;
        	if (requestMapping.method().length > 0) {
        		// 实际请求类型
				requestMethod = requestMapping.method()[0];
			}
        	methodType = requestMethod.name().toLowerCase() + PathConstants.COLON;
			if (uri.toString().endsWith(PathConstants.PATH_SEPARATOR)) {
				uri.delete(uri.length()-1, uri.length());
			}
            uri.append(formatUri(requestMapping.value()[0]));
        }

        return methodType + uri.toString();
}

	/**
	 * 构造uri
	 *
	 * @param uri
	 * @return
	 */
    private String formatUri(String uri) {
    	if (uri.startsWith(PathConstants.PATH_SEPARATOR)) {
			return uri;
		}
    	return PathConstants.PATH_SEPARATOR + uri;
    }

}
