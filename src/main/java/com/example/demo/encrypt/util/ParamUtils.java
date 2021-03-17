package com.example.demo.encrypt.util;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @author 马亮
 * @date 2021/3/16 10:46
 */
public class ParamUtils {

    /**
     * 获取类中方法的参数名（包括父类在内的所有字段）
     *
     * @param clz
     * @param method
     * @return
     */
    public static List<String> getMethodParams(Class<?> clz, Method method) {
        List<String> methodParams = new ArrayList<>();
        Map<String, Object> params = getParams(clz, method);
        for (String in : params.keySet()) {
            //参数类型
            Class clazz = (Class) params.get(in);
            // 类不是Java类型是用户定义类型
            if (clazz.getClassLoader() != null) {
                List<Field> fieldList = new ArrayList<>() ;
                //当父类为null的时候说明到达了最上层的父类(Object类)
                while (clazz != null) {
                    fieldList.addAll(Arrays.asList(clazz.getDeclaredFields()));
                    //得到父类,然后赋给自己
                    clazz = clazz.getSuperclass();
                }
                for (Field f : fieldList) {
                    methodParams.add(f.getName());
                }
            } else {
                methodParams.add(in);
            }
        }

        return methodParams;
    }

    /**
     * 使用javaassist的反射方法获取方法的参数名和对应参数类型的映射
     *
     * @param clazz
     * @param method
     * @return
     */
    private static Map<String, Object> getParams(Class<?> clazz, Method method) {
        Map<String, Object> params = new LinkedHashMap<>();
        try {
            ClassPool pool = ClassPool.getDefault();
            ClassClassPath classPath = new ClassClassPath(ParamUtils.class);
            pool.insertClassPath(classPath);
            CtClass cc = pool.get(clazz.getName());
            CtMethod cm = cc.getDeclaredMethod(method.getName());
            MethodInfo methodInfo = cm.getMethodInfo();
            CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
            LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
            if (attr == null) {
                throw new IllegalArgumentException("获取参数错误");
            }
            int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;
            String[] paramNames = new String[cm.getParameterTypes().length];
            // 参数类型
            Class<?>[] parameterTypes = method.getParameterTypes();
            for (int i = 0; i < paramNames.length; i++) {
                // 参数名称
                paramNames[i] = attr.variableName(i + pos);
                params.put(paramNames[i], parameterTypes[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }

}
