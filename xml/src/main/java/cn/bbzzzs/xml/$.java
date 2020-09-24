package cn.bbzzzs.xml;

import cn.bbzzzs.common.util.LogUtils;
import cn.bbzzzs.common.util.StringUtils;
import com.google.common.collect.Lists;
import jdk.internal.org.xml.sax.InputSource;
import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class $ {
    private File file;
    private List<Element> node;
    private Document document;

    private static final SAXReader saxReader = new SAXReader();

    public $(File file) {
        this.file = file;
        try {
            document = saxReader.read(file);
            this.node = Lists.newArrayList(document.getRootElement());
        } catch (DocumentException e) {
            LogUtils.error("初始化 $ 失败..., 异常原因: %s", e.getMessage());
            e.printStackTrace();
        }
    }

    private $(List<Element> node, Document document, File file) {
        this.node = node;
        this.document = document;
        this.file = file;
    }

    /**
     * 此方法不会清除文本内容
     */
    public void clearHtml() {
        for (Element element : this.node) {
            for (Element e : element.elements()) {
                element.remove(e);
            }
        }
    }


    public $ parent() {
        return new $(this.node.stream().map(n -> n.getParent()).collect(Collectors.toList()), document, file);
    }

    /**
     * 底层查询方法, 接收一个节点, 过滤掉不符合filterFunc的元素
     *
     * @param node
     * @param filterFunc
     * @param elementList
     * @return
     */
    private void selector(Element node, Function<Element, Boolean> filterFunc, List<Element> elementList) {
        // 使用广度优先遍历
        for (Element element : node.elements()) {
            if (filterFunc.apply(element)) {
                elementList.add(element);
            }
        }
        for (Element element : node.elements()) {
            selector(element, filterFunc, elementList);
        }
    }

    public $ tag(String tagElementName) {
        List<Element> elementList = Lists.newLinkedList();
        Function<Element, Boolean> filterFunc = element -> element.getQName().getName().equals(tagElementName);
        node.forEach(e -> selector(e, filterFunc, elementList));
        return new $(elementList, document, file);
    }


    public $ id(String idName) {
        return byAttr("id", idName);
    }

    public $ className(String className) {
        return byAttr("class", className);
    }

    public $ byAttr(String attr, String val) {
        List<Element> elementList = Lists.newLinkedList();
        Function<Element, Boolean> filterFunc = element -> StringUtils.participle(element.attribute(attr).getValue()).contains(val);
        node.forEach(e -> selector(e, filterFunc, elementList));
        return new $(elementList, document, file);
    }

    public void forEach(Consumer<Element> consumer) {
        for (Element element : this.node) {
            consumer.accept(element);
        }
    }

    public List<String> html() {
        List<String> nodeHtmlList = Lists.newLinkedList();
        this.node.forEach(e -> nodeHtmlList.add(logCurrentNode(e, 0)));
        return nodeHtmlList;
    }

    public $ html(String html) {
        // 清除当前元素的内容
        clearHtml();

        String body = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><ResponseInfo>" + html + "</ResponseInfo>";

        // 重新设置当前元素的内容
        forEach(element -> {
            try {
                Document document = DocumentHelper.parseText(body);
                Element e = document.getRootElement();
                List<Element> elements = e.elements();

                // 先添加文本节点
                element.setText(e.getText());
                if (elements.size() > 0) {
                    // 如果存在子节点, 那么我们就循环添加它的子节点
                    for (Element childElement : elements) {
                        element.add((Element) childElement.clone());
                    }
                }
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        });

        // 将修改后的内容持久化到 xml 中
        persistence();

        return this;
    }

    private String logCurrentNode(Element element, int level) {
        StringBuilder sb = new StringBuilder();
        String prefix = getPrefix(level);
        String name = element.getQName().getName();
        sb.append(prefix).append("<").append(name);

        element.attributes().forEach(attr -> {
            sb.append(" ").append(attr.getQName().getName()).append("='").append(attr.getValue()).append("'");
        });

        sb.append(">");
        sb.append(element.getTextTrim());

        if (element.elements().size() > 0) {
            sb.append("\n");
        }

        ++level;
        for (Element e : element.elements()) {
            sb.append(logCurrentNode(e, level));
        }

        if (element.elements().size() > 0) {
            sb.append(prefix);
        }
        sb.append("</").append(name).append(">").append("\n");
        return sb.toString();
    }

    private String getPrefix(int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            sb.append("\t");
        }
        return sb.toString();
    }

    public List<Element> getNode() {
        return this.node;
    }

    public List<List<Attribute>> attr() {
        List attrList = Lists.newLinkedList();
        for (Element element : this.node) {
            attrList.add(element.attributes());
        }
        return attrList;
    }

    /**
     * 持久化xml操作, 不允许用户直接调用
     *
     * @return
     */
    private boolean persistence() {
        try {
            XMLWriter writer = new XMLWriter(new FileWriter(file));
            writer.write(document);
            writer.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.error("持久化 XML 失败, 异常原因: %s", e.getMessage());
            return false;
        }
    }

}
