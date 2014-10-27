package com.pocketdigi.plib.util;

import java.lang.reflect.Field;

/**
 * 对象Util类
 * Created by fhp on 14-9-20.
 */
public class ObjectUtils {
    /**
     * 将父类对像转成子类的对象,不能有带参数构造方法
     * 仅仅是将同名的字段值复制
     * @param parent
     * @param childClass
     * @return
     */
    public static Object castToChild(Object parent, Class childClass) {
//        if(parent instanceof type)
//        {
//            return parent;
//        }

        Class parentClass = parent.getClass();
        if(parentClass==childClass)
            return parent;
        if (parentClass.isAssignableFrom(childClass)) {
            Field[] parentFields = parentClass.getDeclaredFields();
            try {
                Object child = childClass.newInstance();
                for (Field field : parentFields) {
                    field.setAccessible(true);
                    Field childField = childClass.getSuperclass().getDeclaredField(field.getName());
                    childField.setAccessible(true);
                    childField.set(child, field.get(parent));
                }
                return child;
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }

        }
        return null;

    }
}