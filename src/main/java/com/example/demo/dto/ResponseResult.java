package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author 80241664
 * @param <T>
 * @Description
 * code和msg一一对应，由枚举类型 @ResponseCode 提供。code分为三类：负数，0，正数。
 * 正数是提示性错误：比如用户不存在，项目名称重复
 * 0表示成功
 * 负数是程序运行异常：比如提交的参数类型不正确，数据库连接失败
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseResult<T> {

	private Integer code;
	private String desc;
	private T data;

	public static <T>ResponseResult<T> success(T data) {
		return ResponseResult.<T>builder()
				.code(200)
				.desc("描述")
				.data(data)
				.build();
	}

}
