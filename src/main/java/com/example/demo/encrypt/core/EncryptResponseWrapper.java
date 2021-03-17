package com.example.demo.encrypt.core;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 重写Response响应结果
 *
 * @author 马亮
 * @date 2021/3/11 20:37
 */
public class EncryptResponseWrapper extends HttpServletResponseWrapper {
    private ByteArrayOutputStream output = new ByteArrayOutputStream();

    /**
     * Constructs a response adaptor wrapping the given response.
     *
     * @param response The response to be wrapped
     * @throws IllegalArgumentException if the response is null
     */
    public EncryptResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    /**
     * 获取Writer
     *
     * @return
     * @throws IOException
     */
    @Override
    public PrintWriter getWriter() throws IOException {
        return new PrintWriter(output, false);
    }

    /**
     * 获取OutputStream
     *
     * @return
     * @throws IOException
     */
    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return new ServletOutputStream() {
            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setWriteListener(WriteListener listener) {
            }

            // 实际写入ByteArrayOutputStream
            @Override
            public void write(int b) throws IOException {
                output.write(b);
            }
        };
    }

    /**
     * 返回写入的byte[]
     *
     * @return
     */
    public byte[] getContent() {
        return output.toByteArray();
    }
}
