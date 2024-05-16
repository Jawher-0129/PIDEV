package service;

import java.sql.SQLException;
import java.util.List;

public interface IService<T> {
    void add(T var1);

    void delete(int var1) throws SQLException;

    void update(T var1, int var2);

    List<T> getAll();
    T getById(int var1);
}
