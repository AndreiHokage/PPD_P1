package server.repository;

import java.util.Collection;

public interface Repository<T, TID> {
    void add(T elem);
    void delete(T elem);
    void update(T elem, TID id);
    T findByID(TID id);
    Iterable<T> findAll();
    Collection<T> getAll();
}
