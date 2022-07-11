/**
 * Copyright © 2019 by Hilti Corporation – all rights reserved
 */
package utils;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class DataMapperForPOJO {

    private static Map<Class<?>, Object> clsObj = new HashMap<>();

    public static void createMapping(Map<String, String> data, Object obj) {
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isSynthetic()) {
                continue;
            }
            String val = "";
            Object fldVal = null;
            try {
                boolean access = field.isAccessible();
                field.setAccessible(true);
                fldVal = field.get(obj);
                field.setAccessible(access);
            } catch (Exception ignored) {
            }

            String typeName = field.getType().getTypeName();
            boolean typeFlag =
                    typeName.contains("String")
                            || typeName.contains("int")
                            || typeName.contains("Integer")
                            || typeName.contains("float")
                            || typeName.contains("Float")
                            || typeName.contains("double")
                            || typeName.contains("Double")
                            || typeName.contains("long")
                            || typeName.contains("Long")
                            || typeName.contains("boolean")
                            || typeName.contains("Boolean");
            if (Objects.nonNull(fldVal) && typeFlag) {
                val = fldVal.toString();
            }

            if (data.containsKey(field.getName())) {
                val = Objects.nonNull(data.get(field.getName())) ? data.get(field.getName()) : null;
            }
            try {
                boolean accessible = field.isAccessible();
                field.setAccessible(true);
                Type genericFieldType = field.getGenericType();
                if (genericFieldType instanceof ParameterizedType) {
                    ParameterizedType aType = (ParameterizedType) genericFieldType;
                    Type[] fieldArgTypes = aType.getActualTypeArguments();
                    for (Type fieldArgType : fieldArgTypes) {
                        Class<?> fieldArgClass = (Class<?>) fieldArgType;
                        if (aType.getRawType().getTypeName().contains("List")
                                || aType.getRawType().getTypeName().contains("ArrayList")) {
                            internalMappingForList(data, fieldArgClass, val, field, obj);
                        }
                    }
                } else {
                    internalMapping(data, field, obj, val);
                }
                field.setAccessible(accessible);
            } catch (Exception e) {
                String err =
                        "Failed to write value for FIELD: "
                                + field.getName()
                                + " in CLASS: "
                                + field.getDeclaringClass().getSimpleName();
            }
        }
    }

    private static void internalMapping(Map<String, String> data, Field field, Object obj, String val)
            throws IllegalArgumentException, IllegalAccessException, ClassNotFoundException,
            InstantiationException, InvocationTargetException, NoSuchMethodException,
            SecurityException {
        Type type = field.getType();
        String typeName = type.getTypeName();
        Object fldVal = field.get(obj);

        if (Objects.nonNull(val)) {
            if (val.contentEquals("BLANK")) {
                val = "";
            } else if (val.contentEquals("NULL")
                    || val.isEmpty()
                    || val.contentEquals("<" + field.getName() + ">")) {
                val = null;
            }
        }

        if (typeName.contains("String")) {
            field.set(obj, StringUtils.isNotEmpty(val) ? val : null);
        } else if (typeName.contains("int") || typeName.contains("Integer")) {
            field.set(obj, StringUtils.isNotEmpty(val) ? Integer.parseInt(val) : null);
        } else if (typeName.contains("float")) {
            field.set(obj, StringUtils.isNotEmpty(val) ? Float.parseFloat(val) : null);
        } else if (typeName.contains("double") || typeName.contains("Double")) {
            field.set(obj, StringUtils.isNotEmpty(val) ? Double.parseDouble(val) : null);
        } else if (typeName.contains("long") || typeName.contains("Long")) {
            field.set(obj, StringUtils.isNotEmpty(val) ? Long.parseLong(val) : null);
        } else if (typeName.contains("boolean") || typeName.contains("Boolean")) {
            if (Objects.nonNull(val)) {
                if (StringUtils.isNotEmpty(val) && Boolean.parseBoolean(val)) {
                    field.set(obj, true);
                } else {
                    field.set(obj, false);
                }
            }
        } else if (((Class<?>) type).isEnum()) {
            if (Objects.nonNull(val)) {
                field.set(obj, Enum.valueOf((Class<Enum>) type, val));
            }
        } else {
            Class<?> cls = Class.forName(type.getTypeName());
            if (!cls.getName().contains("Date") && !cls.getName().contains("Time")) {
                Object childObj;
                if (clsObj.containsKey(cls)) {
                    childObj = clsObj.get(cls);
                }
                childObj =
                        Objects.nonNull(fldVal)
                                ? (cls.cast(fldVal))
                                : cls.getDeclaredConstructor().newInstance();
                createMapping(data, childObj);
                boolean flag = false;
                for (Field f : childObj.getClass().getDeclaredFields()) {
                    f.setAccessible(true);
                    if (f.get(childObj) != null) {
                        flag = true;
                        break;
                    }
                }
                if (flag) {
                    field.set(obj, childObj);
                } else {
                    field.set(obj, null);
                }
            } else {
                field.set(obj, null);
            }
        }
    }

    private static void internalMappingForList(
            Map<String, String> data, Class<?> fieldArgClass, String val, Field field, Object obj)
            throws IllegalArgumentException, IllegalAccessException, ClassNotFoundException,
            InstantiationException, InvocationTargetException, NoSuchMethodException,
            SecurityException {

        List<String> list;
        if (Objects.isNull(val) || val.isEmpty()) {
            list = null;
        } else {
            if (val.contains("[")) {
                val = val.substring(1);
            }
            if (val.contains("]")) {
                val = val.substring(0, val.length() - 1);
            }
            list = Arrays.asList(val.split(","));
            list = list.stream().map(String::trim).collect(Collectors.toList());
        }
        Object fldVal = field.get(obj);

        if (fieldArgClass.equals(String.class)
                || fieldArgClass.equals(int.class)
                || fieldArgClass.equals(Integer.class)
                || fieldArgClass.equals(float.class)
                || fieldArgClass.equals(Float.class)
                || fieldArgClass.equals(double.class)
                || fieldArgClass.equals(Double.class)
                || fieldArgClass.equals(long.class)
                || fieldArgClass.equals(Long.class)
                || fieldArgClass.equals(boolean.class)
                || fieldArgClass.equals(Boolean.class)) {
            field.set(obj, list);
        } else {
            Class<?> cls = Class.forName(fieldArgClass.getTypeName());
            Object object;
            if (clsObj.containsKey(cls)) {
                object = clsObj.get(cls);
            } else {
                object =
                        Objects.nonNull(fldVal)
                                ? (cls.cast(fldVal))
                                : cls.getDeclaredConstructor().newInstance();
                clsObj.put(cls, object);
            }
            createMapping(data, object);
            boolean flag = false;
            for (Field f : object.getClass().getDeclaredFields()) {
                f.setAccessible(true);
                if (f.get(object) != null) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                object = null;
            }
            field.set(obj, Objects.isNull(object) ? null : List.of(object));
        }
    }
}
