package com.sanghelic.facturacion.repository;

import java.util.ArrayList;
import java.util.List;

public class Repository<T> {

    private final List<T> storage = new ArrayList<>();

    public void save(T item) {
        storage.add(item);
    }

    public List<T> findAll() {
        return List.copyOf(storage);
    }

    public void saveAll(List<? extends T> source) {
        storage.addAll(source);
    }

    public void exportTo(List<? super T> destination) {
        destination.addAll(storage);
    }
}