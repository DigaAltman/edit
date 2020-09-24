package cn.bbzzzs.db.result.bean.handler.support;

import cn.bbzzzs.common.util.StringUtils;
import cn.bbzzzs.db.result.bean.Result;
import cn.bbzzzs.db.result.bean.ResultMap;
import cn.bbzzzs.db.result.bean.handler.DefaultResultXmlHandler;
import cn.bbzzzs.db.result.bean.handler.ResultXmlHandler;
import lombok.AllArgsConstructor;
import org.dom4j.Element;

/**
 * 处理特殊标签的处理器， 它负责解析 <association ...></association> 和 <collection ...></collection>
 */
@AllArgsConstructor
public class SpecialResultXmlHandler extends DefaultResultXmlHandler {
    /**
     * 处理 <id ...></id> 和 <result ...></result> 这种基本标签的处理器
     */
    private ResultXmlHandler xmlHandler;

    @Override
    public Result parse(Element element) {
        String elementName = element.getQName().getName();
        if (elementName.equals("collection") || elementName.equals("association")) {

            // 定义一个 [一对一] 或 [一对多] 返回结果映射字段
            Result special = new Result();
            special.setProperty(property(element));

            // 定义当前标签 对应的 ResultMap
            ResultMap specialMap = new ResultMap();
            if (elementName.equals("collection")) {
                special.setCollectionMap(specialMap);
            } else {
                special.setAssociationMap(specialMap);
            }

            // 如果使用 ref 的话, 我们就不需要解析 ofType 以及它的子属性了
            String ref = ref(element);
            if (!StringUtils.isEmpty(ref)) {
                // 先构建一个简单的 ResultMap， 里面只需要存在一个 id 属性就可以了
                specialMap.setId(ref);
                return special;
            }

            // 设置 collectionMap 中的id为 property 字段值
            specialMap.setId(special.getProperty());
            String type = ofType(element);
            if (StringUtils.isEmpty(type)) {
                throw new IllegalArgumentException("[ERROR] " + elementName + " 标签的 ofType 属性的值不能为 空字符");
            }

            // 设置对应的返回实体类
            specialMap.setType(type);

            for (Element childResultElement : element.elements()) {
                specialMap.getResultList().add(parse(childResultElement));
            }

            return special;
        }

        return xmlHandler.parse(element);
    }
}
