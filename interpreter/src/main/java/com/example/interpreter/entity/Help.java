package com.example.interpreter.entity;


import cn.bbzzzs.common.util.ArrayUtil;
import cn.bbzzzs.common.util.FileUtils;
import cn.bbzzzs.common.util.LogUtils;
import cn.bbzzzs.common.util.StringUtils;
import com.example.interpreter.translation.CodeTranslationService;
import com.example.interpreter.translation.TransApi;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.File;
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * mysql优化
 */
public class Help {
    private CodeTranslationService codeTranslationService = new CodeTranslationService();

//    public static void main(String[] args) {
//
//        /**
//         * 存放注释的容器
//         */
//        List<LineBody> annoList = new LinkedList();
//
//        /**
//         * 存放代码的容器
//         */
//        List<LineBody> codeList = new LinkedList();
//
//        /**
//         * 常见的3种注释
//         */
//        String ann1 = "/*", ann2 = "//", ann3 = "*", ann4 = "/**", ann5 = "*/";
//
//
//        int count = FileUtils.readLine(new File("D:\\java_project\\2018\\05\\edit\\interpreter\\src\\main\\resources\\a.java"), (index, line) -> {
//            // 去除空格后的内容
//            String lineBody = line.trim();
//
//            // 当前行
//            int currentIndex = index;
//
//            // 如果是以 '/*', '//', '*' 开头的注释
//            if (lineBody.startsWith(ann1) || lineBody.startsWith(ann2) || lineBody.startsWith(ann3)) {
//                // 具体的注释内容
//                String annotationBody = "";
//
//                // 我们还需要判断一下内容是否以 '*/' 结尾
//                if (lineBody.endsWith(ann5)) {
//                    lineBody = lineBody.substring(0, lineBody.length() - 2);
//                }
//
//                // 如果注释是以 '/**' 开头的话, 我们就从第3个位置开始截取内容.
//                if (lineBody.startsWith(ann4)) {
//                    annotationBody = lineBody.substring(3);
//
//                    // 如果注释是以 '/*', '//' 开头的话, 我们就从第2个位置开始截取内容.
//                } else if (lineBody.startsWith(ann1) || lineBody.startsWith(ann2)) {
//                    annotationBody = lineBody.substring(2);
//
//                }
//                // 如果是以 * 开头的注释, 我们需要往上面进行递归判断. 如果上一行也是 *, 我们就继续让上面推. 直到遍历完所有行. 如果还没有检测道 /* 则,报错
//                else if (lineBody.startsWith(ann3)) {
//                    // 如果不是以 '*/' 开头的注释, 我们才进行处理. 否则直接跳过
//                    if (!lineBody.startsWith(ann5)) {
//
//                        // 递归条件: 当前行号 > 0
//                        while (currentIndex > 0) {
//
//                            // 取出上一行的 lineBody， 如果这个 lineBody 存在,那就说明上一行代码是注释开始的地方, 也就是 '/**' 或者是 '/*' 当然, '//' 也
//                            // 会算进去, 所以我们需要手动验证 代码注释 是否符合 alibaba开发手则 规范, 并且是否可以编译通过
//                            LineBody body = null;
//                            try {
//                                body = annoList.get(--currentIndex);
//                            } catch (IndexOutOfBoundsException e) {
//                                // 不做任何索引异常处理
//                            }
//                            if (body != null) {
//                                body.setOldLine(body.getOldLine() + "\n" + line).setTrimText(body.getTrimText() + "\n" + lineBody.substring(1).trim());
//                                return null;
//                            }
//
//                        }
//
//                        if (currentIndex < 0) {
//                            throw new IllegalArgumentException("* 注释没有父亲注释...");
//                        }
//
//                    } else {
//                        // 不在继续执行
//                        return null;
//                    }
//
//                }
//                // 这行内容是 */
//                else if (lineBody.equals("")) {
//                    return null;
//                }
//
//                // 添加注释内容
//                annoList.add(LineBody.buildInstance(index, line, annotationBody).setCode(false));
//            }
//            // 代码块
//            else {
//                // 这里我们不需要使用 去除空格 后的代码内容, 所以直接传入一个 null 进去
//                codeList.add(LineBody.buildInstance(index, line, null).setCode(true));
//            }
//            return null;
//        });


//        String text = "<p>Bean factory implementations should support the standard bean lifecycle interfaces\n" +
//                "as far as possible. The full set of initialization methods and their standard order is:\n" +
//                "<ol>\n" +
//                "<li>BeanNameAware's {@code setBeanName}\n" +
//                "<li>BeanClassLoaderAware's {@code setBeanClassLoader}\n" +
//                "<li>BeanFactoryAware's {@code setBeanFactory}\n" +
//                "<li>EnvironmentAware's {@code setEnvironment}\n" +
//                "<li>EmbeddedValueResolverAware's {@code setEmbeddedValueResolver}\n" +
//                "<li>ResourceLoaderAware's {@code setResourceLoader}\n" +
//                "(only applicable when running in an application context)\n" +
//                "<li>ApplicationEventPublisherAware's {@code setApplicationEventPublisher}\n" +
//                "(only applicable when running in an application context)\n" +
//                "<li>MessageSourceAware's {@code setMessageSource}\n" +
//                "(only applicable when running in an application context)\n" +
//                "<li>ApplicationContextAware's {@code setApplicationContext}\n" +
//                "(only applicable when running in an application context)\n" +
//                "<li>ServletContextAware's {@code setServletContext}\n" +
//                "(only applicable when running in a web application context)\n" +
//                "<li>{@code postProcessBeforeInitialization} methods of BeanPostProcessors\n" +
//                "<li>InitializingBean's {@code afterPropertiesSet}\n" +
//                "<li>a custom init-method definition\n" +
//                "<li>{@code postProcessAfterInitialization} methods of BeanPostProcessors\n" +
//                "</ol>\n" +
//                "\n" +
//                "<p>On shutdown of a bean factory, the following lifecycle methods apply:\n" +
//                "<ol>\n" +
//                "<li>{@code postProcessBeforeDestruction} methods of DestructionAwareBeanPostProcessors\n" +
//                "<li>DisposableBean's {@code destroy}\n" +
//                "<li>a custom destroy-method definition\n" +
//                "</ol>\n" +
//                "\n" +
//                "@author Rod Johnson\n" +
//                "@author Juergen Hoeller\n" +
//                "@author Chris Beams\n" +
//                "@since 13 April 2001\n" +
//                "@see BeanNameAware#setBeanName\n" +
//                "@see BeanClassLoaderAware#setBeanClassLoader\n" +
//                "@see BeanFactoryAware#setBeanFactory\n" +
//                "@see org.springframework.context.ResourceLoaderAware#setResourceLoader\n" +
//                "@see org.springframework.context.ApplicationEventPublisherAware#setApplicationEventPublisher\n" +
//                "@see org.springframework.context.MessageSourceAware#setMessageSource\n" +
//                "@see org.springframework.context.ApplicationContextAware#setApplicationContext\n" +
//                "@see org.springframework.web.context.ServletContextAware#setServletContext\n" +
//                "@see org.springframework.beans.factory.config.BeanPostProcessor#postProcessBeforeInitialization\n" +
//                "@see InitializingBean#afterPropertiesSet\n" +
//                "@see org.springframework.beans.factory.support.RootBeanDefinition#getInitMethodName\n" +
//                "@see org.springframework.beans.factory.config.BeanPostProcessor#postProcessAfterInitialization\n" +
//                "@see DisposableBean#destroy\n" +
//                "@see org.springframework.beans.factory.support.RootBeanDefinition#getDestroyMethodName";
//
//        List<String> responseList = depthLoop(text, 0);
//        responseList.forEach(System.out::println);
//


//    }

    // 在平台申请的APP_ID 详见 http://api.fanyi.baidu.com/api/trans/product/desktop?req=developer
    public static final String APP_ID = "20200819000546641";
    public static final String SECURITY_KEY = "tIAOmRTZnIlfKCKUpc7y";

    public static void main(String[] args) {
        TransApi api = new TransApi(APP_ID, SECURITY_KEY);

        String query = "The root interface for accessing a Spring bean container.";
        System.out.println(api.getTransResult(query, "auto", "en"));
    }


    private static final String[] divisionSymbol = {"\n\n", ".\n", ",", "\n"};

    /**
     * 深度遍历
     *
     * @param text  遍历文本
     * @param depth 遍历深度
     * @return
     */
    private static List<String> depthLoop(String text, int depth) {
        // 如果当前遍历的文本超过 400 个字,那么我们就进行分割
        if (text.length() > 400) {
            // 如果已经到达了最大深度, 那么就直接抛出内容过长的异常
            if (depth == divisionSymbol.length) {
                throw new IllegalArgumentException("内容过长, 无法直接翻译. 请尽量缩减注释。 对需要翻译的内容进行 逗号或段落分割 处理");
            }
            List<String> response = new LinkedList();
            for (String s : handle(text, divisionSymbol[depth])) {
                System.out.println(s);
                response.add(ArrayUtil.toString(depthLoop(s, depth + 1), divisionSymbol[depth]));
            }
            return response;
        }
        return CodeTranslationService.translation(text).getTranslation();
    }

    /**
     * 处理语句的方法
     *
     * @param text 文本
     * @param flag 符号
     * @return
     */
    private static List<String> handle(String text, String flag) {
        // 基于特殊符号分割文本。 \n\n .\n
        if (text.contains(flag)) {
            return Arrays.asList(text.split(flag));
        }
        return Arrays.asList(text);
    }
}

@Data
@Accessors(chain = true)
class LineBody implements Serializable {
    private int line;
    private String oldLine;
    private String trimText;
    // 是否属于代码内容
    private boolean isCode;

    /**
     * 前缀文本
     */
    private int prefix;

    private static final String SPACE = " ";

    /**
     * 获取文本开头的空格的数量, 然后称其为前缀
     *
     * @param text
     * @return
     */
    public static int getSpacePrefixNumber(String text) {
        int i = 0;
        while (true) {
            // 如果整行文本都是 空格
            if (i == text.length()) {
                return text.length();
            }

            int j = i + 1;

            // 如果两个字符之间的内容不是空格了,就退出
            if (!text.substring(i, j).equals(SPACE)) {
                return i;
            }

            i++;
        }
    }

    /**
     * 将每行内容编译一下
     *
     * @param index    每行代码或者注释的索引号
     * @param oldLine  每行代码或者注释原来的文本
     * @param trimText 每行代码或者注释去除空格和注释前后缀的文本
     * @return
     */
    public static LineBody buildInstance(int index, String oldLine, String trimText) {
        LineBody instance = new LineBody();
        instance.line = index;
        instance.oldLine = oldLine;
        instance.trimText = trimText;
        instance.prefix = getSpacePrefixNumber(oldLine);
        return instance;
    }

    /**
     * 代码翻译
     */
    public String translate() {
        if (!isCode) {
            // 首先对需要翻译的代码进行一个分散组合
            if (this.trimText.length() > 400) {

                if (this.trimText.contains("\\.\n")) {

                }
            }


            if (this.trimText.length() > 400) {
                List<String> res = null;

                // 循环翻译处理函数
                Function<String, String> mapFunc = text -> {
                    List<String> response = CodeTranslationService.translation(text).getTranslation();
                    if (response == null) {
                        System.out.println(text);
                        return "";
                    }
                    if (response.size() > 0) {
                        String[] strings = response.toArray(new String[response.size()]);
                        return StringUtils.concat("", strings);
                    } else {
                        return "";
                    }
                };

                StringUtils.SBuilder sb = StringUtils.builder();
                // 如果翻译文本超过400个字, 那么我们就通过 . 符号进行分割
                if (this.trimText.contains("\n")) {
                    res = Arrays.asList(this.trimText.split("\n\n")).stream().map(mapFunc).collect(Collectors.toList());

                    for (int i = 0; i < res.size(); i++) {
                        sb.build(res.get(i));

                        // 如果是最后一行
                        if (i != res.size() - 1) {
                            sb.build(".");
                        }
                    }

                    return sb.toString();

                } else if (this.trimText.contains(";")) {
                    res = Arrays.asList(this.trimText.split(";")).stream().map(mapFunc).collect(Collectors.toList());

                    for (int i = 0; i < res.size(); i++) {
                        sb.build(res.get(i));

                        // 如果是最后一行
                        if (i != res.size() - 1) {
                            sb.build(";");
                        }
                    }

                    return sb.toString();
                }

                return "";
            }
            return CodeTranslationService.translation(this.trimText).getTranslation().get(0);
        }
        return this.oldLine;
    }
}