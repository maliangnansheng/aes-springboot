package com.example.demo.encrypt.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.example.demo.dto.ResponseResult;
import com.example.demo.encrypt.constant.HeaderConstants;
import com.example.demo.encrypt.constant.PathConstants;
import com.example.demo.encrypt.util.AesEncryptUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;

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
     */
    public static void processDecryption(DecryptRequestWrapper requestWrapper, HttpServletRequest req) {
        String requestData = requestWrapper.getRequestData();
        try {
            // GET请求
            String methodType = req.getMethod();
            if (RequestMethod.GET.name().equals(methodType)) {
                // 加密的参数
                List<String> encryptParams = Collections.emptyList();
                String encryptHeader = req.getHeader(HeaderConstants.HAVE_ENCRYPT_PARAMS);
                if (encryptHeader != null) {
                    encryptParams = Arrays.asList(encryptHeader.split(PathConstants.COMMA));
                }
                // 未加密的参数
                List<String> notEncryptParams = Collections.emptyList();
                String notEncryptHeader = req.getHeader(HeaderConstants.NOT_ENCRYPT_PARAMS);
                if (notEncryptHeader != null) {
                    notEncryptParams = Arrays.asList(notEncryptHeader.split(PathConstants.COMMA));
                }

                // url参数解密
                Map<String, String> paramMap = new HashMap<>(10);
                Enumeration<String> parameterNames = req.getParameterNames();
                while (parameterNames.hasMoreElements()) {
                    // 请求的各参数名（id）
                    String paramName = parameterNames.nextElement();
                    // 获取各参数值
                    String paramValue = req.getParameter(paramName);

                    // “指定加密参数头”和“指定未加密参数头”最多只能有一个
                    if (CollectionUtils.isNotEmpty(encryptParams) && CollectionUtils.isNotEmpty(notEncryptParams)) {
                        // TODO 抛出异常（自定义）
                        throw new Exception();
                    }
                    // 指定了已经加密参数
                    if (CollectionUtils.isNotEmpty(encryptParams) && CollectionUtils.isEmpty(notEncryptParams)) {
                        if (encryptParams.contains(paramName)) {
                            paramValue = AesEncryptUtils.aesDecrypt(paramValue);
                        }
                    }
                    // 全字段加密
                    if (CollectionUtils.isEmpty(encryptParams) && CollectionUtils.isEmpty(notEncryptParams)) {
                        paramValue = AesEncryptUtils.aesDecrypt(paramValue);
                    }

                    paramMap.put(paramName, paramValue);
                }
                requestWrapper.setParamMap(paramMap);

                // POST请求
            } else if (RequestMethod.POST.name().equals(methodType)) {
                // 解密
                String decryptRequestData = AesEncryptUtils.aesDecrypt(requestData);
                requestWrapper.setRequestData(decryptRequestData);
            } else {
                // TODO 抛出异常
            }
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
