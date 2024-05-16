package service;

import java.util.List;

public interface interfaceUser  <T>{
    void addUser(T t);
    void updateUser(T t);
    void deleteUser(int id);
    List<T> getAll();
}
