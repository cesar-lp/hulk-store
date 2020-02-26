package com.herostore.products.utils;

import com.herostore.products.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ClassUtils {

    static Logger logger = LoggerFactory.getLogger(ClassUtils.class);

    private ClassUtils() {
    }

    public static <T> Method extractMethod(Class<T> clazz, String name) {
        try {
            return clazz.getMethod("get" + StringUtils.capitalize(name));
        } catch (NoSuchMethodException exc) {
            logger.error("Couldn't extract method for field named {}", name, exc);
            throw new ServiceException("Couldn't extract method for field named " + name, exc);
        }
    }

    public static <T> Object invokeMethod(Method method, T item) {
        try {
            return method.invoke(item, (Object[]) null);
        } catch (IllegalAccessException | InvocationTargetException exception) {
            logger.error("Couldn't invoke method {}: ", method.getName(), exception.getCause());
            throw new ServiceException("Couldn't invoke method " + method.getName(), exception.getCause());
        }
    }
}
