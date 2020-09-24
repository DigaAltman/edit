package cn.bbzzzs.common.proxy.support;

import cn.bbzzzs.common.proxy.Proxy;

import java.lang.reflect.InvocationHandler;

/**
 * created by TMT
 */
public class InstanceProxy<T> extends Proxy {
    private T proxyInstance;

    public InstanceProxy(InvocationHandler h, T proxyInstance) {
        super(h);
        this.proxyInstance = proxyInstance;
    }

    @Override
    protected Class getProxyClass() {
        return proxyInstance.getClass();
    }

    @Override
    protected String getMethodBody(Class returnType, String methodName) {
        if (returnType.equals(void.class)) {
            return String.format(voidBody, getProxyClass().getSimpleName(), methodName);
        } else {
            return String.format(baseBody, getProxyClass().getSimpleName(), methodName);
        }
    }

    @Override
    public Object getInstance() {
        return proxyInstance;
    }
}
