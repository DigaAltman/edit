package com.example.dao;

import lombok.Getter;

import java.io.Serializable;
import java.util.List;

/**
 * 分页对象
 *
 * @param <E> 分页中的数据集合
 */
@Getter
public class Page<E> implements Serializable {

    // 当前页
    private int current;

    // 每页数据量
    private int size;

    // 总页数
    private int total;

    // 数据总数
    private int totalSize;

    // 是否存在下一页
    private boolean next;

    // 是否存在上一页
    private boolean prev;

    // 当前页包含的数据
    private List<E> dataList;

    /**
     *
     * @param current   当前页
     * @param size      每页数据量
     * @param totalSize 总数据量
     * @param dataList  数据集合
     */
    public Page(int current, int size, int totalSize, List<E> dataList) {
        this.current = current;
        this.size = size;
        this.totalSize = totalSize;
        this.total = totalSize % size == 0 ? totalSize / size : (totalSize / size) + 1;
        this.next = this.current < this.total;
        this.prev = this.current > 1;
        this.dataList = dataList;
    }

}
