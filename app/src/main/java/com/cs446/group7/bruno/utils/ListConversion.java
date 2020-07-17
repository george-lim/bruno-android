package com.cs446.group7.bruno.utils;

import java.util.LinkedList;
import java.util.List;

public class ListConversion {
    public static <T> LinkedList<T> listToLinkedList(List<T> list) {
        LinkedList<T> newList = new LinkedList<>();
        for (T o : list) {
            newList.add(o);
        }
        return newList;
    }
}
