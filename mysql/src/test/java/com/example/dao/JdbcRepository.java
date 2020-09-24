package com.example.dao;

import java.util.Iterator;
import java.util.List;

/**
 * @param <E> 具体实体类
 * @param <T> 实体类中的Id字段类型
 */
public interface JdbcRepository<E, T> {

    /**
     * 根据id查询数据
     *
     * @param id
     * @return
     */
    E findById(T id);

    /**
     * 查询所有数据
     *
     * @return
     */
    List<E> findAll();

    /**
     * 查询多条数据
     *
     * @param ids
     * @return
     */
    List<E> findAll(Iterator<T> ids);

    /**
     * 分页查询
     *
     */
    Page<E> findByAll(Iterator<T> ids, Pageable pageable);

    /**
     * 根据id修改数据
     *
     * @param e
     * @return
     */
    int update(E e);

    /**
     * 根据id删除数据
     *
     * @param id
     * @return 返回影响条数
     */
    int removeById(T id);

    /**
     * 删除多条数据
     *
     * @param ids
     * @return 返回影响条数
     */
    int removeById(Iterator<T> ids);

    /**
     * 删除所有数据
     *
     * @return 返回影响条数
     */
    int removeAll();

    /**
     * 新增数据
     */
    int insert(E e);

    /**
     * 新增多条数据
     *
     * @param eList
     * @return
     */
    int insertBatch(List<E> eList);

}
