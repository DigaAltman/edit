package cn.bbzzzs.common.proxy;

import javassist.*;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 基于javassist实先的动态代理
 */
public abstract class Proxy implements Serializable {
    /**
     * 动态代理生成的类的后缀编号.避免生成的类冲突
     */
    private int classNumber = 1;

    /**
     * 动态代理生成的类的前缀,它表示我们所生成的动态类
     */
    private final String proxyClassName = "EditSpark$";

    /**
     * 动态代理类的缓存容器
     */
    protected final ClassPool classPool = ClassPool.getDefault();

    /**
     * 用户传入的具体增强的类的代码信息
     */
    protected InvocationHandler h;

    /**
     * 动态生成的类方法的业务核心逻辑
     */
    protected String voidBody = "this.handler.invoke(%s, %s, $args);";
    protected String baseBody = "return ($r) this.handler.invoke(%s, %s, $args);";


    public Proxy(InvocationHandler h) {
        Objects.requireNonNull(h);
        this.h = h;
    }

    private static boolean isNative(Method method) {
        return Modifier.isNative(method.getModifiers());
    }

    private static boolean isFinal(Method method) {
        return Modifier.isFinal(method.getModifiers());
    }

    /**
     * 创建 方法
     *
     * @param proxy
     * @param method
     * @return
     * @throws Exception
     */
    private CtMethod createMethod(CtClass proxy, Method method) throws Exception {
        String methodName = method.getName();
        CtClass returnType = classPool.get(method.getReturnType().getName());
        CtClass[] parameter = Arrays.stream(method.getParameterTypes()).map(c -> {
            try {
                return classPool.get(c.getName());
            } catch (NotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }).collect(Collectors.toList()).toArray(new CtClass[method.getParameterTypes().length]);

        CtClass[] errors = Arrays.stream(method.getExceptionTypes()).map(c -> {
            try {
                return classPool.get(c.getName());
            } catch (NotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }).collect(Collectors.toList()).toArray(new CtClass[method.getExceptionTypes().length]);
        CtMethod ctMethod = new CtMethod(returnType, methodName, parameter, proxy);
        ctMethod.setExceptionTypes(errors);
        ctMethod.setModifiers(method.getModifiers());
        return ctMethod;
    }

    /**
     * 创建字段
     *
     * @param proxy
     * @param fType
     * @param fName
     * @return
     */
    private CtField createField(CtClass proxy, Class fType, String fName) {
        CtField ctField = null;
        try {
            ctField = new CtField(classPool.get(fType.getName()), fName, proxy);
        } catch (CannotCompileException e) {
            e.printStackTrace();
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        return ctField;
    }

    /**
     * 获取唯一的方法名称标识
     *
     * @param method
     * @return
     */
    private String getMethodId(Method method) {
        StringBuilder methodName = new StringBuilder(method.getName());
        for (Class<?> parameterType : method.getParameterTypes()) {
            methodName.append(parameterType.getSimpleName());
        }
        return methodName.toString();
    }

    /**
     * 模式, true是 类 代理, false是 接口 代理
     *
     * @param model
     * @param <T>
     * @return
     * @throws Exception
     */
    public synchronized <T> T newInstance(boolean model) throws Exception {
        CtClass proxy = classPool.makeClass(proxyClassName + classNumber++);

        Class proxyClass = getProxyClass();
        if (Modifier.isFinal(proxyClass.getModifiers())) {
            throw new IllegalArgumentException("className:\t" + proxyClass.getName() + " not support Proxy!!");
        }

        if (!model) {
            // 用户选择的是接口类型的代理, 但是传入的不是接口类. 我们还是可以代理的.
            // 只不过这里需要做一个小小的判断
            if (proxyClass.isInterface()) {
                proxy.setInterfaces(new CtClass[]{classPool.get(proxyClass.getName())});
            } else {
                proxy.setSuperclass(classPool.get(proxyClass.getName()));
            }
        } else {
            proxy.setSuperclass(classPool.get(proxyClass.getName()));
            // Object Object;
            proxy.addField(createField(proxy, proxyClass, proxyClass.getSimpleName()));
        }

        // 添加 InvocationHandler handler 字段
        String handlerFieldName = "handler";
        proxy.addField(createField(proxy, InvocationHandler.class, handlerFieldName));

        List<Method> methods = Arrays.stream(proxyClass.getMethods()).filter(
                method -> !Modifier.isStatic(method.getModifiers()) && !isNative(method) && !isFinal(method)
        ).collect(Collectors.toList());


        // 3. 为每一个方法都创建一个代理方法实现
        for (int i = 0; i < methods.size(); i++) {
            Method method = methods.get(i);
            proxy.addField(createField(proxy, Method.class, getMethodId(method)));
            CtMethod ctMethod = createMethod(proxy, method);

            String src = getMethodBody(method.getReturnType(), getMethodId(method));

            ctMethod.setBody(src);
            proxy.addMethod(ctMethod);

        }

//        byte[] byteCode = proxy.toBytecode();
//        Files.write(byteCode, new File("D:\\java_project\\2018\\05\\edit\\mysql\\src\\main\\resources\\a.class"));

        // 3. 加载类并实例化这个对象
        Class<T> newClass = classPool.toClass(proxy);
        T instance = newClass.newInstance();
        Field field = newClass.getDeclaredField(handlerFieldName);
        field.setAccessible(true);
        field.set(instance, h);

        if (model) {
            Field proxyField = newClass.getDeclaredField(proxyClass.getSimpleName());
            proxyField.setAccessible(true);
            proxyField.set(instance, getInstance());
        }

        // 补充: 方法赋值操作
        for (int i = 0; i < methods.size(); i++) {
            Method method = methods.get(i);
            Field methodField = newClass.getDeclaredField(getMethodId(method));
            methodField.setAccessible(true);
            methodField.set(instance, method);
        }

        return instance;
    }

    /**
     * 获取类增强中的实例对象,这个方法不是给外界调用的
     *
     * @return
     */
    @SuppressWarnings("noCall")
    protected Object getInstance() {
        throw new IllegalArgumentException("需要子类实现");
    }

    /**
     * 获取代理类信息
     */
    protected abstract Class getProxyClass();

    /**
     * 获取代理类逻辑信息
     *
     * @param returnType
     * @param methodName
     * @return
     */
    protected abstract String getMethodBody(Class returnType, String methodName);

}
