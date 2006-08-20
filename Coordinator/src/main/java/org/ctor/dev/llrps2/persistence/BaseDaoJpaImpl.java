package org.ctor.dev.llrps2.persistence;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.orm.jpa.JpaCallback;
import org.springframework.orm.jpa.JpaTemplate;
import org.springframework.orm.jpa.support.JpaDaoSupport;

public abstract class BaseDaoJpaImpl<T, PK extends Serializable> extends
        JpaDaoSupport implements BaseDao<T, PK> {
    protected Class<T> entityClass;

    public BaseDaoJpaImpl(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public List<T> findAll() {
        return (List<T>) getJpaTemplate().execute(new JpaCallback() {
            public Object doInJpa(EntityManager em) {
                return em.createQuery("from " + entityClass.getName())
                        .getResultList();
            }
        });
    }

    public T find(PK id) {
        return getJpaTemplate().find(entityClass, id);
    }

    public void save(T entity) {
        getJpaTemplate().persist(entity);
    }

    public void saveAll(List<T> entities) {
        final JpaTemplate template = getJpaTemplate();
        for (T entity : entities) {
            template.persist(entity);
        }
    }

    public T merge(T entity) {
        return getJpaTemplate().merge(entity);
    }

    public void remove(T entity) {
        getJpaTemplate().remove(entity);
    }

    public void flush() {
        getJpaTemplate().flush();
    }

    protected T singleObject(List<? extends T> found) {
        if (found.size() > 1) {
            throw new IllegalStateException(String.format(
                    "more than one object returned: %d", found.size()));
        }
        return found.isEmpty() ? null : found.get(0);
    }

}
