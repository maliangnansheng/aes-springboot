package com.example.demo.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 马亮
 * @date 2021/3/8 15:39
 */
@Data
public class UserDto extends Parent {
	private Integer id;
	private String name;
	private String content;
	private LocalDateTime time;
	private List<Teacher> teachers;
}
