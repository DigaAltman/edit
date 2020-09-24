package cn.bbzzzs.db.result.factory.support;

import cn.bbzzzs.db.result.bean.ResultMap;
import cn.bbzzzs.db.result.factory.ResultMapFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 管理 ResultMap 对象的工厂, 所有的 ResultMap 都会通过这个ResultMap 进行
 */
public class DefaultResultMapFactory implements ResultMapFactory {

    /**
     * 基于 id 的 ResultMap 的缓存对象, 缓存彻底初始化的 ResultMap, 用户自定义的 ResultMap 解析后的结果也应该要放在这里
     */
    private final Map<String, ResultMap> idResultMapCache = new HashMap<> (64);

    /**
     * 基于 id 的 ResultMap 的缓存对象,  缓存已经简单初始化的 ResultMap， 里面关于引用其他 ResultMap 的字段引用一直是 null
     */
    private final Map<String, ResultMap> easyIdResultMapCache = new HashMap<>  (64);

    /**
     * 基于 class 的 ResultMap 的缓存对象, 缓存彻底初始化的 ResultMap, 用户自定义的 ResultMap 解析后的结果也应该要放在这里
     */
    private final Map<Class, ResultMap> classResultMapCache = new HashMap<> (64);

    /**
     * 基于 class 的 ResultMap 的缓存对象,  缓存已经简单初始化的 ResultMap， 里面关于引用其他 ResultMap 的字段引用一直是 null
     */
    private final Map<Class, ResultMap> easyClassResultMapCache = new HashMap<> (64);


    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    @Override
    public ResultMap getMap(String id) {
        readWriteLock.readLock().lock();
        try {
            return idResultMapCache.get(id);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public ResultMap getMap(Class clazz) {
        readWriteLock.readLock().lock();
        try {
            return classResultMapCache.get(clazz);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public void putMap(String id, ResultMap resultMap) {
        readWriteLock.writeLock().lock();
        try {
            idResultMapCache.put(id, resultMap);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public void putMap(Class clazz, ResultMap resultMap) {
        readWriteLock.writeLock().lock();
        try {
            classResultMapCache.put(clazz, resultMap);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public ResultMap getEasyMap(String id) {
        readWriteLock.readLock().lock();
        try {
            return easyIdResultMapCache.get(id);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public ResultMap getEasyMap(Class clazz) {
        readWriteLock.readLock().lock();
        try {
            return easyClassResultMapCache.get(clazz);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public void putEasyMap(String id, ResultMap resultMap) {
        readWriteLock.writeLock().lock();
        try {
            easyIdResultMapCache.put(id, resultMap);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public void putEasyMap(Class clazz, ResultMap resultMap) {
        readWriteLock.writeLock().lock();
        try {
            easyClassResultMapCache.put(clazz, resultMap);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public void removeEasyMap(Class clazz) {
        readWriteLock.writeLock().lock();
        try {
            easyIdResultMapCache.remove(clazz);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public void removeEasyMap(String id) {
        readWriteLock.writeLock().lock();
        try {
            easyIdResultMapCache.remove(id);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }
}
