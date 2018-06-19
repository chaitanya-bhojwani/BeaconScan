package com.example.affine.beaconscan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Lists {
    private static final List<?> s_emptyList = Collections.unmodifiableList(newArrayList());

    public static <V> List<V> newArrayList(Collection<V> collection) {
        return new ArrayList<V>(collection);
    }

    public static <V> List<V> newArrayList(V... list) {
        return newArrayList(Arrays.asList(list));
    }

    public static <V> List<V> newArrayList() {
        return new ArrayList<V>();
    }

    public static <V> List<V> concatenate(List<V> list1, List<V> list2) {
        ArrayList<V> arrayList = new ArrayList<V>();
        arrayList.addAll(list1);
        arrayList.addAll(list2);
        return arrayList;
    }

    public static <V> List<V> emptyList() {
        return (List<V>) s_emptyList;
    }
}
