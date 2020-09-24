package cn.bbzzzs.db.result.bean.handler;

import cn.bbzzzs.db.result.bean.Result;
import org.dom4j.Element;

/**
 * 解析 XML 标签的方法
 */
public interface ResultXmlHandler {

    /**
     * 传入一个 Element, 返回对应的 Result
     * @param element
     * @return
     */
    Result parse(Element element);
}
