package cn.bbzzzs.common.util;

import lombok.Data;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * created by TMT
 */
@Data
public class XmlQuery {
    public static final SAXReader saxReader = new SAXReader();

    /**
     * 节点树的节点
     */
    private XmlNode node;

    private XmlQuery(Element element, int level) {
        this.node = new XmlNode(UUID.randomUUID().toString(), element, level);
    }

    public XmlQuery(File file) {
        try {
            Document document = saxReader.read(file);

            // 初始化根节点
            node = new XmlNode(UUID.randomUUID().toString(), document.getRootElement(), 1);

            // 初始化 DOM 树
            scanDomXml();

        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    /**
     * 推荐使用操作入口
     *
     * @param file
     * @return
     */
    public static XmlQuery read(File file) {
        return new XmlQuery(file);
    }

    public static XmlQuery read(String file) {
        return new XmlQuery(new File(file));
    }

    /**
     * 解析当前的 DOM 树结构
     */
    private void scanDomXml() {
        List<Element> elementList = node.element.elements();

        for (Element element : elementList) {
            queryForSelector(node, element, node.level + 1);
        }
    }

    /**
     * 查询当前节点下的指定名称的标签
     *
     * @param tag 标签名称
     * @return
     */
    public List<XmlQuery> queryForSelector(String tag) {
        List<XmlNode> xmlNodeList = new ArrayList();

        doFor(node.child, node -> {
            if (node.element.getName().equals(tag)) {
                xmlNodeList.add(node);
            }
            return null;
        });

        return xmlNodeList.stream().map(node -> new XmlQuery(node.element, node.level)).collect(Collectors.toList());
    }

    /**
     * 循环遍历 DOM 树下的每一个节点, 然后做统一的处理
     *
     * @param child   多个子节点
     * @param handler 处理的方式
     */
    private void doFor(List<XmlNode> child, Handler handler) {
        for (XmlNode node : child) {
            handler.batch(node);
            doFor(node.child, handler);
        }
    }

    /**
     * @param node    所属节点
     * @param element 当前元素
     * @param level   节点等级
     */
    private void queryForSelector(XmlNode node, Element element, int level) {
        XmlNode xmlNode = new XmlNode(UUID.randomUUID().toString(), element, level);
        node.child.add(xmlNode);
        for (Element ele : element.elements()) {
            queryForSelector(xmlNode, ele, level + 1);
        }
    }

    /**
     * 获取当前节点的标签名称
     *
     * @return
     */
    public String tagName() {
        return node.element.getName();
    }

    /**
     * 获取当前节点中的内容
     *
     * @return
     */
    public String html() {
        return html(node.level);
    }

    /**
     * 获取当前节点的子节点
     */
    public List<XmlQuery> children() {
        return node.element.elements().stream().map(ele -> new XmlQuery(ele, node.level + 1)).collect(Collectors.toList());
    }

    /**
     * 获取当前节点的指定名称的子节点
     */
    public List<XmlQuery> children(String tag) {
        return children().stream().filter(query -> query.tagName().equals(tag)).collect(Collectors.toList());
    }


    /**
     * 获取指定key,value的子节点
     */
    public List<XmlQuery> selectList(String attr, String val) {
        List<XmlQuery> res = new ArrayList();
        doFor(node.child, node -> {
            if (node.element.attribute(attr).getValue().equals(val)) {
                res.add(new XmlQuery(node.element, node.level));
            }
            return null;
        });
        return res;
    }


    /**
     * 获取指定的 key,value的 子节点
     */
    public XmlQuery selectOne(String attr, String val) {
        return selectList(attr, val).get(0);
    }


    /**
     * 简化查询操作,$("#app"), $(".element")
     *
     * @param expr  表达式
     * @return
     */
    public List<XmlQuery> $(String expr) {
        if (expr.startsWith(".")) {
            // id处理
            return Arrays.asList(selectOne("id", expr));

        } else if (expr.startsWith("#")) {
            // class处理
            return selectList("class", expr);

        } else {
            // tag处理
            return queryForSelector(expr);
        }

    }


    /**
     * 获取当前节点中的内容
     *
     * @return
     */
    private String html(int level) {
        int levelCount = level - node.level;
        String prefix = "";
        for (int i = 0; i < levelCount; i++) {
            prefix += "\t";
        }

        StringBuilder res = new StringBuilder().append(prefix).append("<").append(tagName());

        List<String> names = attributeNames();
        List<String> values = attributeValues();

        for (int i = 0; i < names.size(); i++) {
            res.append(" ").append(names.get(i)).append(" = \"").append(values.get(i)).append("\"");
        }

        res.append(">");

        String html = node.element.getTextTrim();

        // 如果还存在子节点,那么我们就遍历子节点
        boolean noWrap;
        if (node.element.elements().size() > 0) {
            noWrap = false;
            for (Element element : node.element.elements()) {
                XmlNode xmlNode = new XmlNode(UUID.randomUUID().toString(), element, node.level + 1);
                String innerHtml = new XmlQuery(xmlNode.element, xmlNode.level).html(node.level + 2);
                res.append("\n").append(innerHtml);
            }
        } else {
            noWrap = true;
        }

        if (!noWrap) {
            res.append(prefix).append(html).append("\n");
        } else {
            res.append(html);
        }

        return res.append(prefix).append("</").append(tagName()).append(">").toString();
    }


    /**
     * 获取标签中的文本
     */
    public String text() {
        return node.element.getTextTrim();
    }

    /**
     * 获取标签的所有属性
     *
     * @return
     */
    public List<String> attributeNames() {
        return node.element.attributes().stream().map(attr -> attr.getQName().getName()).collect(Collectors.toList());
    }

    /**
     * 获取标签中的所有属性对应的值
     *
     * @return
     */
    public List<String> attributeValues() {
        return node.element.attributes().stream().map(attr -> attr.getValue()).collect(Collectors.toList());
    }

    /**
     * 记录DOM树的结构的节点
     */
    private static class XmlNode {
        /**
         * 节点id
         */
        private String id;

        /**
         * 节点中的元素
         */
        private Element element;

        /**
         * 节点等级
         */
        private int level;

        /**
         * 第一个孩子节点
         */
        private List<XmlNode> child = new ArrayList();

        public XmlNode(String id, Element element, int level) {
            this.id = id;
            this.element = element;
            this.level = level;
        }

    }


    @FunctionalInterface
    public interface Handler<T> {

        /**
         * 传入一个 XmlNode 信息, 做自定义处理
         *
         * @param xmlNode
         * @return
         */
        T batch(XmlNode xmlNode);
    }
}
