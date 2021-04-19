# aes-springboot 基于SpringBoot框架API的加解密操作

[![](https://img.shields.io/badge/Author-马亮南生-orange.svg)](http://blog.nanshengbbs.top/)
[![GitHub stars](https://img.shields.io/github/stars/maliangnansheng/aes-springboot.svg?style=social&label=Stars)](https://github.com/maliangnansheng/bbs-ssm)
[![GitHub forks](https://img.shields.io/github/forks/maliangnansheng/aes-springboot.svg?style=social&label=Fork)](https://github.com/maliangnansheng/bbs-ssm)

## 前言

前后端分离的开发方式，我们以接口为标准来进行推动，定义好接口，各自开发自己的功能，最后进行联调整合。无论是开发原生的APP还是webapp还是PC端的软件,只要是前后端分离的模式，就避免不了调用后端提供的接口来进行业务交互

网页或者app，只要抓下包就可以清楚的知道这个请求获取到的数据，这样的接口对爬虫工程师来说是一种福音，要抓你的数据简直轻而易举

数据的安全性非常重要，特别是用户相关的信息，稍有不慎就会被不法分子盗用，所以我们对这块要非常重视，不容马虎

aes-springboot 基于SpringBoot框架API的加解密操作

## 功能点

- 支持所有基于Servlet的Web框架（Spring Boot、SSM等）
- 前后端独立开发
- 内置AES加密算法
- 支持用户自定义加密算法
- 使用简单，有操作示列

## 跑起来

该项目基于SpringBoot开发，开箱即用

1. 启动DemoApplication
2. 本机访问[http://localhost:8888/](http://localhost:8888/)即可测试

## 独立开发

通常情况下对每一个需要加密或者解密的rest api接口，前后端需要沟通一致，否则前端加密了后端不知道没有进行解密，程序是会抛出异常，反之亦然。这很明显不是我们想要的，前后端其实也很难保证一致性。

故 为了有效解决以上问题我们约定3个请求头和1个响应头，分别是：

- `X-REQ-ENCRYPTED（敏感信息解密头 -请求头）`
- `X-RES-ENCRYPTED（敏感信息加密头 -响应头）`
- `X-HAVE-ENCRYPT（指定加密参数头 -请求头）`
- `X-NOT-ENCRYPT（指定未加密参数头 -请求头）`

> 后端根据前端传过来的请求头`X-REQ-ENCRYPTED、X-HAVE-ENCRYPT、X-NOT-ENCRYPT`进行分析解密
>
> 前端根据后端响应头里面的`X-RES-ENCRYPTED`进行分析解密

这样就实现了前后端对于加密/解密与否的独立开发，不需要沟通每一个rest api的加密/解密情况，只需要和上面一样约定好3个请求头和1个响应头即可

## 情景模拟

### 请求需要加密

- **GET请求**

  - 全字段加密

    只需要前端传参时在请求头加上`X-REQ-ENCRYPTED`即可

    ```js
    $.ajax({
        type: "get",
        url: "test/ccc/get",
        headers: {
            "X-REQ-ENCRYPTED": 1
        },
        data: {
            "id": "a3445d13b33a9fd310a23ae3921b969b",
            "name": "7476a246be2ed0a9bc66188f0222a834",
            "age": "5b96bb2a998d7b95f17ae80cef13e968",
            "pass": "326e82a3774b2f5aa337167476116449"
        },
        contentType: "application/json",
        success: function (resData) {
    
        }
    });
    ```

  - 部分字段加密`（加密数<未加密数）`

    **注：`X-HAVE-ENCRYPT（指定加密参数头）`和`X-NOT-ENCRYPT（指定未加密参数头）`最多只能有一个**

    只需要前端传参时在请求头加上`X-REQ-ENCRYPTED`和`X-HAVE-ENCRYPT`即可

    ```js
    $.ajax({
        type: "get",
        url: "test/ccc/get",
        headers: {
            "X-REQ-ENCRYPTED": 1,
            "X-HAVE-ENCRYPT": "id, age"
        },
        data: {
            "id": "a3445d13b33a9fd310a23ae3921b969b",
            "name": "张三",
            "age": "5b96bb2a998d7b95f17ae80cef13e968",
            "pass": "123456"
        },
        contentType: "application/json",
        success: function (resData) {
    
        }
    });
    ```

  - 部分字段不加密`（加密数>未加密数）`

    **注：`X-HAVE-ENCRYPT（指定加密参数头）`和`X-NOT-ENCRYPT（指定未加密参数头）`最多只能有一个**

    只需要前端传参时在请求头加上`X-REQ-ENCRYPTED`和`X-NOT-ENCRYPT`即可

    ```js
    $.ajax({
        type: "get",
        url: "test/ccc/get",
        headers: {
            "X-REQ-ENCRYPTED": 1,
            "X-NOT-ENCRYPT": "name, pass"
        },
        data: {
            "id": "a3445d13b33a9fd310a23ae3921b969b",
            "name": "张三",
            "age": "5b96bb2a998d7b95f17ae80cef13e968",
            "pass": "123456"
        },
        contentType: "application/json",
        success: function (resData) {
    
        }
    });
    ```

  - 不加密

    不添加请求头即可

    ```js
    $.ajax({
        type: "get",
        url: "test/ccc/get",
        data: {
            "id": "500",
            "name": "张三",
            "age": "26",
            "pass": "123456"
        },
        contentType: "application/json",
        success: function (resData) {
    
        }
    });
    ```

- **POST请求**

  - 加密

    `直接对整个请求体加密`，只需要前端传参时在请求头加上`X-REQ-ENCRYPTED`即可

    ```js
    $.ajax({
        type: "post",
        url: "test/ccc/save",
        headers: {
            "X-REQ-ENCRYPTED": 1
        },
        data: "1a66fe909c8aced9ed1369823fc50811dfc0cf97c5ad8d6a5aeddb6976ce3262de96dd24f1b379898cddf5890b49f660aa1498947124874d040fc5e47f9f8b3a46cfc8593b652fdd533838101a999fbbe9eca63088f62baaa35b5fbcd6699041",
        contentType: "application/json",
        success: function (resData) {
            
        }
    });
    ```

  - 不加密

    不添加请求头即可

    ```js
    $.ajax({
        type: "post",
        url: "test/ccc/save",
        data: {
            "id": "500",
            "name": "张三",
            "age": "26",
            "pass": "123456"
        },
        contentType: "application/json",
        success: function (resData) {
    
        }
    });
    ```

### 响应需要加密

对响应结果整体进行加密，返回给前端，响应头加上`X-RES-ENCRYPTED`即可

具体实现直接去看源码的`ApiDecryptDataInit类`、 `DecryptFilter类`、`EncryptResponseWrapper类`、`DecryptHandler类的processEncryption方法`

> **实现流程：**
>
> 对需要加密的响应接口添加注解@Encrypt > 对需要加密的response进行重写 > 调用加密方法给结果进行加密 > 给响应体添加响应头`X-RES-ENCRYPTED`

## 附

[在线AES加密解密](http://tool.chacuo.net/cryptaes)

## 其它

需要了解更多请参看源码！