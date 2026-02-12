package coreParkingSystem;

import java.util.List;

public interface GenericDAO<T, ID> {
    void create(T t);
    T read(ID id);
    void update(T t);
    void delete(ID id);
    List<T> getAll();
}