package com.college.interfaces;

import java.util.List;

// Interface demonstrating ABSTRACTION — generic CRUD contract
public interface Manageable<T> {
    void    add(T item);
    T       getById(String id);
    List<T> getAll();
    boolean remove(String id);
    boolean update(T item);
}
