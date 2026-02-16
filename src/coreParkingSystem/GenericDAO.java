package coreParkingSystem;

import java.util.List;

// Base interface used by all DAO classes to standardize database operations
public interface GenericDAO<T, ID> {

    void create(T t);      // insert new record
    T read(ID id);         // retrieve by primary key
    void update(T t);      // modify existing record
    void delete(ID id);    // remove record
    List<T> getAll();      // fetch all records
}
