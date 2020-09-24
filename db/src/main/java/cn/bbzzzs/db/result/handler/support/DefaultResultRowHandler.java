package cn.bbzzzs.db.result.handler.support;

import cn.bbzzzs.db.result.bean.ResultMap;
import cn.bbzzzs.db.result.factory.ResultMapFactory;
import cn.bbzzzs.db.result.handler.ResultRowHandler;
import lombok.AllArgsConstructor;

import java.util.*;

@AllArgsConstructor
public class DefaultResultRowHandler implements ResultRowHandler {

    private ResultMapFactory factory;

    @Override
    public <T> T handle(LinkedHashMap map, Class<T> returnClass) {
        // 处理 Map 返回值类型
        if (Map.class.isAssignableFrom(returnClass)) {
            return (T) map;
        }

        // 处理 Set 返回值类型
        if (Set.class.isAssignableFrom(returnClass)) {
            Set set = new LinkedHashSet();
            map.forEach((k, v) -> set.add(v));
            return (T) set;
        }

        // 处理 Object[] 返回值类型
        if (Object[].class.isAssignableFrom(returnClass)) {
            Collection values = map.values();
            return (T) values.toArray(new Object[values.size()]);
        }

        // 处理 List 返回值类型
        if (List.class.isAssignableFrom(returnClass)) {
            List list = new LinkedList();
            map.forEach((k, v) -> list.add(v));
            return (T) list;
        }

        // 如果是基本类型,比如 Integer, int, float ... 之类的, 直接返回就好了
        if (ResultMap.basicTypeSet.contains(returnClass)) {
            return (T) map.values().toArray()[0];
        }

        // 其他返回值类型
        ResultMap resultMap = ResultMap.generate(factory, returnClass);
        return handle(map, resultMap);
    }

    @Override
    public <T> T handle(LinkedHashMap map, ResultMap resultMap) {
        // 1. 获取当前需要依赖的类
        Set<ResultMap> dependencies = resultMap.dependencies();

        // 2. 进行类的简单值填充
        Map<ResultMap, Object> beforeClassMap = new LinkedHashMap();
        dependencies.forEach(result -> beforeClassMap.put(result, result.mapToBean(map)));

        // 4. 进行真正的依赖填充
        Object bean = resultMap.fillBean(beforeClassMap);
        return (T) bean;
    }

}
