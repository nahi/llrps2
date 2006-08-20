package org.ctor.dev.llrps2.persistence;

import java.io.Serializable;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface BaseDao<T, ID extends Serializable> {

    List<T> findAll();

    T find(ID id);

    @Transactional(readOnly = false)
    void save(T entity);

    @Transactional(readOnly = false)
    void saveAll(List<T> entities);

    @Transactional(readOnly = false)
    T merge(T entity);

    @Transactional(readOnly = false)
    void remove(T entity);
    
    void flush();
}
