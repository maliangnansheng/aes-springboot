package com.example.demo.encrypt.core;

import com.example.demo.encrypt.constant.HeaderConstants;
import com.example.demo.encrypt.constant.PathConstants;
import com.example.demo.encrypt.init.ApiDecryptDataInit;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * 数据解密过滤器
 *
 * @author 马亮
 * @date 2021/3/8 15:33
 */
@Slf4j
public class DecryptFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @SneakyThrows
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // 判断请求是否需要解密
        boolean decryptionStatus = "1".equals(req.getHeader(HeaderConstants.DECRYPT_HEADER));
        // 判断请求是否需要加密
        boolean encryptionStatus = DecryptHandler.contains(ApiDecryptDataInit.responseEncryptUriList, req.getRequestURI(), req.getMethod());
        // 例：get:/test/ccc/getEncode
        String uri = req.getMethod().toLowerCase() + PathConstants.COLON + req.getRequestURI();
        // 需要解密
        if (decryptionStatus) {
            log.info("需要解密的请求 {}", uri);
            Class<?> clazz = ApiDecryptDataInit.requestUriClassMap.get(uri);
            Method method = ApiDecryptDataInit.requestUriMethodMap.get(uri);
            if (clazz != null && method != null) {
                // 以下是需要解密的操作
                DecryptRequestWrapper requestWrapper = new DecryptRequestWrapper(req);
                // 请求解密处理
                DecryptHandler.processDecryption(requestWrapper, req, clazz, method);
                req = requestWrapper;
            }
        }
        // 需要加密
        if (encryptionStatus) {
            log.info("需要加密的请求 {}", uri);
            EncryptResponseWrapper wrapper = new EncryptResponseWrapper(resp);
            // 只需要请求解密
            chain.doFilter(req, wrapper);

            // 从伪造的Response中读取写入的内容并放入缓存
            byte[] data = wrapper.getContent();
            data = DecryptHandler.processEncryption(new String(data), resp);

            // 写入到原始的Response
            ServletOutputStream output = resp.getOutputStream();
            output.write(data);
            output.close();
        } else {
            chain.doFilter(req, resp);
        }

    }

    @Override
    public void destroy() {

    }

}
