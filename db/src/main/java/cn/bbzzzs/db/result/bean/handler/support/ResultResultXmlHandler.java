package cn.bbzzzs.db.result.bean.handler.support;

import cn.bbzzzs.db.result.bean.Result;
import cn.bbzzzs.db.result.bean.handler.DefaultResultXmlHandler;
import org.dom4j.Element;

/**
 * 处理普通标签的处理器， 它负责解析 <result ...></result>
 */
public class ResultResultXmlHandler extends DefaultResultXmlHandler {

    @Override
    public Result parse(Element element) {
        if (element.getQName().getName().equals("result")) {
            Result result = new Result();
            result.setColumn(column(element));
            result.setProperty(property(element));

            if (result.getColumn() == null || result.getProperty() == null) {
                throw new IllegalArgumentException("result 标签的 column 属性和 property 属性不能为空!!");
            }

            return result;
        }

        return null;
    }
}
