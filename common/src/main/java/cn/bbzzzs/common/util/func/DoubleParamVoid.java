package cn.bbzzzs.common.util.func;

/**
 * 传入2个参数,没有返回值
 * @param <P1>
 * @param <P2>
 */
@FunctionalInterface
public interface DoubleParamVoid<P1, P2> {

    void m(P1 p1, P2 p2);
}
