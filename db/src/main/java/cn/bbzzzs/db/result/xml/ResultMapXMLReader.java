package cn.bbzzzs.db.result.xml;

import cn.bbzzzs.common.util.StringUtils;
import cn.bbzzzs.db.result.bean.Result;
import cn.bbzzzs.db.result.bean.ResultMap;
import cn.bbzzzs.db.result.bean.handler.ResultXmlHandler;
import cn.bbzzzs.db.result.bean.handler.support.IdResultXmlHandler;
import cn.bbzzzs.db.result.bean.handler.support.ResultResultXmlHandler;
import cn.bbzzzs.db.result.bean.handler.support.SpecialResultXmlHandler;
import cn.bbzzzs.db.result.factory.ResultMapFactory;
import cn.bbzzzs.db.result.factory.support.DefaultResultMapFactory;
import cn.bbzzzs.xml.$;
import org.dom4j.Element;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 解析 ResultMap 的 XML 配置
 */
public class ResultMapXMLReader {
    private ResultMapFactory factory;
    private $ jQuery;
    private ResultXmlHandler xmlHandler;

    public ResultMapXMLReader(ResultMapFactory factory, File xmlFile) {
        this.factory = factory;
        this.jQuery = new $(xmlFile);
        xmlHandler = new SpecialResultXmlHandler(
                new IdResultXmlHandler(
                        new ResultResultXmlHandler()
                )
        );
    }

    public void scan() {
        $ resultMapList = jQuery.tag("resultMap");

        List<ResultMap> initResultMapList = new ArrayList();
        List<Element> elementList = new ArrayList();

        resultMapList.forEach(element -> {
            if (element.attribute("id") == null) {
                throw new IllegalArgumentException("resultMap 必须存在 id 标签");
            }

            if (element.attribute("type") == null) {
                throw new IllegalArgumentException("resultMap 必须存在 type 标签");
            }

            String id = element.attribute("id").getValue();
            String type = element.attribute("type").getValue();

            if (StringUtils.isEmpty(id) || StringUtils.isEmpty(type)) {
                throw new IllegalArgumentException("[ERROR] resultMap 标签的 id 属性和 type 属性的值不能为 null");
            }

            ResultMap resultMap = new ResultMap();
            resultMap.setId(id);
            resultMap.setType(type);

            initResultMapList.add(resultMap);
            elementList.add(element);
        });

        generate(initResultMapList, elementList);
    }

    private void generate(List<ResultMap> initResultMapList, List<Element> elementList) {
        // 简单初始化完毕!!
        for (int i = 0; i < initResultMapList.size(); i++) {
            ResultMap resultMap = initResultMapList.get(i);
            Element element = elementList.get(i);

            List<Result> resultList = resultMap.getResultList();

            element.elements().forEach(e -> resultList.add(xmlHandler.parse(e)));
            factory.putEasyMap(resultMap.getId(), resultMap);
        }

        // 填充依赖
        for (int i = 0; i < initResultMapList.size(); i++) {
            ResultMap resultMap = initResultMapList.get(i);
            fillGenerate(resultMap.childMapResult());
            factory.putMap(resultMap.getId(), resultMap);
            factory.removeEasyMap(resultMap.getId());
        }
    }

    private void fillGenerate(List<Result> resultList) {
        for (Result result : resultList) {
            // 获取它的 子ResultMap
            ResultMap map = result.getCollectionMap();
            if (map == null) {
                map = result.getAssociationMap();
            }

            // 此时,已经遍历到了没有依赖的 Result 了, 可以直接返回了
            if (map == null) {
                return;
            }

            // 如果 result 的 type 是 null, 也就表示它是一个 ref
            if (map.getType() == null) {
                String ref = map.getId();
                map = factory.getMap(ref);
                if (map == null) {
                    map = factory.getEasyMap(ref);
                }

                if (map == null) {
                    throw new IllegalArgumentException("[ERROR] 找不到 ResultMap[id='" + ref + "'] 的声明");
                }

                fillGenerate(map.getResultList());
                if (result.getCollectionMap() == null) {
                    result.setAssociationMap(map);
                } else {
                    result.setCollectionMap(map);
                }
            }
        }
    }

    public static void main(String[] args) {
        DefaultResultMapFactory defaultResultMapFactory = new DefaultResultMapFactory();

        ResultMapXMLReader xmlReader = new ResultMapXMLReader(defaultResultMapFactory, new File("D:\\java_project\\2018\\05\\edit\\db\\src\\main\\resources\\result.xml"));
        xmlReader.scan();

    }
}
