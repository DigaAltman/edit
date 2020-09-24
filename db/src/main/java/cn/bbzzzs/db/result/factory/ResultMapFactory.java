package cn.bbzzzs.db.result.factory;

import cn.bbzzzs.db.result.bean.ResultMap;

public interface ResultMapFactory {

    ResultMap getMap(String id);

    ResultMap getMap(Class clazz);

    void putMap(String id, ResultMap resultMap);

    void putMap(Class clazz, ResultMap resultMap);

    ResultMap getEasyMap(String id);

    ResultMap getEasyMap(Class clazz);

    void putEasyMap(String id, ResultMap resultMap);

    void putEasyMap(Class clazz, ResultMap resultMap);

    void removeEasyMap(Class clazz);

    void removeEasyMap(String id);
}
