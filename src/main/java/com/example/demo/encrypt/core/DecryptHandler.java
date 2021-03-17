package com.example.demo.encrypt.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.example.demo.dto.ResponseResult;
import com.example.demo.encrypt.constant.HeaderConstants;
import com.example.demo.encrypt.constant.PathConstants;
import com.example.demo.encrypt.util.AesEncryptUtils;
import com.example.demo.encrypt.util.ParamUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 马亮
 * @date 2021/3/12 10:40
 */
@Slf4j
public class DecryptHandler {
    /**
     * 请求解密处理
     *
     * @param requestWrapper 自定义的request
     * @param req            原始request
     * @param clazz          请求对应的controller类
     * @param method         请求对应的controller方法
     */
    public static void processDecryption(DecryptRequestWrapper requestWrapper, HttpServletRequest req, Class<?> clazz, Method method) {
        String requestData = requestWrapper.getRequestData();
        try {
            // 非GET请求
            if (!StringUtils.endsWithIgnoreCase(req.getMethod(), RequestMethod.GET.name())) {
                // 解密
                String decryptRequestData = AesEncryptUtils.aesDecrypt(requestData);
                requestWrapper.setRequestData(decryptRequestData);
                return;
            }

            // 加密的参数
            String encryptParams = req.getHeader(HeaderConstants.HAVE_ENCRYPT_PARAMS);
            // 未加密的参数
            String notEncryptParams = req.getHeader(HeaderConstants.NOT_ENCRYPT_PARAMS);
            // 获取类中方法的参数名（包括父类在内的所有字段）
            List<String> methodParams = ParamUtils.getMethodParams(clazz, method);
            // 有加密参数就以加密参数为准
            if (encryptParams != null) {
                methodParams = Arrays.asList(encryptParams.split(PathConstants.COMMA));
                // 否则以未加密的参数为准
            } else if (notEncryptParams != null) {
                methodParams = methodParams.stream().filter(temp -> !Arrays.asList(notEncryptParams.split(PathConstants.COMMA)).contains(temp)).collect(Collectors.toList());
            }

            // url参数解密
            Map<String, String> paramMap = new HashMap<>();
            Enumeration<String> parameterNames = req.getParameterNames();
            while (parameterNames.hasMoreElements()) {
                // 请求的各参数名（id）
                String paramName = parameterNames.nextElement();
                // 对请求中有参数值的数据进行解密
                if (methodParams.contains(paramName)) {
                    // 获取各参数值
                    String paramValue = req.getParameter(paramName);
                    // 解密
                    String decryptParamValue = AesEncryptUtils.aesDecrypt(paramValue);
                    paramMap.put(paramName, decryptParamValue);
                }
            }

            requestWrapper.setParamMap(paramMap);
        } catch (Exception e) {
            log.error("请求数据解密失败", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 响应加密处理
     *
     * @param data     需要加密的数据
     * @param response 响应
     * @return
     * @throws Exception
     */
    public static byte[] processEncryption(String data, HttpServletResponse response) throws Exception {
        ResponseResult responseResult = JSON.parseObject(data, ResponseResult.class);
        responseResult.setData(AesEncryptUtils.aesEncrypt(JSONObject.toJSONString(responseResult.getData(), SerializerFeature.WriteMapNullValue)));
        response.setHeader(HeaderConstants.ENCRYPT_HEADER, "1");

        return JSON.toJSONString(responseResult, SerializerFeature.WriteMapNullValue).getBytes(StandardCharsets.UTF_8);
    }

    /**
     * 判断list中是否包含uri
     *
     * @param list       需要对响应内容进行加密的接口URI
     * @param uri        请求URI
     * @param methodType 请求类型（get/post/...）
     * @return
     */
    public static boolean contains(List<String> list, String uri, String methodType) {
        if (list.contains(uri)) {
            return true;
        }
        String prefixUri = methodType.toLowerCase() + PathConstants.COLON + uri;
        return list.contains(prefixUri);
    }

}
