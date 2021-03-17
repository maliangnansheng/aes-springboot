package com.example.demo.controller;

import com.example.demo.dto.ResponseResult;
import com.example.demo.dto.UserDto;
import com.example.demo.encrypt.annotation.Encrypt;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * @author 马亮
 * @date 2021/3/7 15:31
 */
@RestController
@RequestMapping("/test/ccc")
public class UserController {

    /**
     * get请求解密/响应加密
     *
     * @param dto
     * @param age
     * @param pass
     * @param size
     * @param no
     * @return
     * @throws IOException
     */
    @RequestMapping("get")
	@Encrypt
    public ResponseResult<UserDto> get(UserDto dto, String age, String pass, Integer size, String no) throws IOException {
        dto.setTime(LocalDateTime.now());
        System.err.println("dto:" + dto);
        System.err.println("age:" + age);
        System.err.println("pass:" + pass);
        System.err.println("size:" + size);
        System.err.println("no:" + no);

        return ResponseResult.success(dto);
    }

    /**
     * post请求解密/响应加密
     *
     * @param dto
     * @return
     */
    @PostMapping("/save")
    public ResponseResult<UserDto> save(@RequestBody UserDto dto) {
        System.err.println("dto:" + dto);
        return ResponseResult.success(dto);
    }

    /**
     * 仅仅响应加密
     *
     * @return
     */
    @RequestMapping("getEncrypt")
    @Encrypt
    public ResponseResult<UserDto> getEncrypt() {
        UserDto dto = new UserDto();
        dto.setId(1);
        dto.setName("hhh");

        return ResponseResult.success(dto);
    }
}
