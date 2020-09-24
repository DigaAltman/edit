package cn.bbzzzs.common.proxy.support;

import cn.bbzzzs.common.proxy.Proxy;

import java.lang.reflect.InvocationHandler;


public class ClassProxy<T> extends Proxy {
    private Class<T> proxyClass;

    public ClassProxy(InvocationHandler h, Class proxyClass) {
        super(h);
        this.proxyClass = proxyClass;
    }

    @Override
    protected Class getProxyClass() {
        return proxyClass;
    }

    @Override
    protected String getMethodBody(Class returnType, String methodName) {
        if (returnType.equals(void.class)) {
            return String.format(voidBody, "this", methodName);
        } else {
            return String.format(baseBody, "this", methodName);
        }
    }
}
