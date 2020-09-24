package cn.bbzzzs.db.result.bean.handler.support;

import cn.bbzzzs.db.result.bean.Result;
import cn.bbzzzs.db.result.bean.handler.DefaultResultXmlHandler;
import cn.bbzzzs.db.result.bean.handler.ResultXmlHandler;
import lombok.AllArgsConstructor;
import org.dom4j.Element;

/**
 * 处理普通标签的处理器， 它负责解析 <id ...></id> 标签
 */
@AllArgsConstructor
public class IdResultXmlHandler extends DefaultResultXmlHandler {
    private ResultXmlHandler xmlHandler;

    @Override
    public Result parse(Element element) {
        if (element.getQName().getName().equals("id")) {
            Result result = new Result();
            result.setColumn(column(element));
            result.setProperty(property(element));
            result.setPrimary(true);

            if(result.getColumn() == null || result.getProperty() == null) {
                throw new IllegalArgumentException("ID 标签的 column 属性和 property 属性不能为空!!");
            }

            return result;
        }

        // 交给其他实现处理
        return xmlHandler.parse(element);

    }
}
