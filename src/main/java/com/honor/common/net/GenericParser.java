package com.honor.common.net;


import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class GenericParser {

    private GenericParser(){}

    public static Class<?> getClassGeneric(Class clazz) {

        return getClassGeneric(clazz, 0);
    }

    public static Class<?> getClassGeneric(Class clazz, int index) {

        Type type = clazz.getGenericSuperclass();

        return (Class<?>) ((ParameterizedType) type).getActualTypeArguments()[index];
    }

    public static Class<?> getFieldGeneric(Field field) {

        return getFieldGeneric(field,0);
    }
    public static Class<?> getFieldGeneric(Field field,int index) {

        return (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[index];
    }


}
