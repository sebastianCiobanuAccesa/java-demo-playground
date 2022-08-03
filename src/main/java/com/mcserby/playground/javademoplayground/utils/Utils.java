package com.mcserby.playground.javademoplayground.utils;

import org.springframework.util.SerializationUtils;

import java.io.Serializable;

public class Utils {
    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T clone(T object) {
        return (T) SerializationUtils.deserialize(SerializationUtils.serialize(object));
    }
}
