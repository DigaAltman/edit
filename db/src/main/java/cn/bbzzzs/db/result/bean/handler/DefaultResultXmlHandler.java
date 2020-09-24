package cn.bbzzzs.db.result.bean.handler;

import org.dom4j.Attribute;
import org.dom4j.Element;

public abstract class DefaultResultXmlHandler implements ResultXmlHandler {

    /**
     * 解析 element 中的 attributeName 属性的值
     *
     * @param element
     * @param attributeName
     * @return
     */
    private String attr(Element element, String attributeName) {
        Attribute column = element.attribute(attributeName);
        if (column != null) {
            return column.getValue();
        }
        return null;
    }

    /**
     * 解析 column 属性
     */
    public String column(Element element) {
        return attr(element, "column");
    }

    /**
     * 解析 property 属性
     *
     * @param element
     * @return
     */
    public String property(Element element) {
        return attr(element, "property");
    }


    /**
     * 解析 javaType 属性
     *
     * @param element
     * @return
     */
    public String javaType(Element element) {
        return attr(element, "javaType");
    }

    /**
     * 解析 ref 属性
     *
     * @param element
     * @return
     */
    public String ref(Element element) {
        return attr(element, "ref");
    }

    /**
     * 解析 ofType 属性
     *
     * @param element
     * @return
     */
    public String ofType(Element element) {
        return attr(element, "ofType");
    }

}
