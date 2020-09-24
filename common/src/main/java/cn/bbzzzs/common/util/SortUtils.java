package cn.bbzzzs.common.util;

import java.util.Arrays;
import java.util.Random;

import static cn.bbzzzs.common.util.LogUtils.*;


/**
 * 排序工具类
 */
public class SortUtils {

    /**
     * 快速排序
     *
     * @param arr
     */
    public static void quickSort(int[] arr) {
        if (arr == null || arr.length < 2)
            return;

        quickSort(arr, 0, arr.length - 1, 0);
    }

    /**
     * 快速排序的核心方法
     *
     * @param arr   具体数组
     * @param l     左区间
     * @param r     右区间
     * @param level 递归深度,debug情况下是为了显示算法的具体逻辑步骤
     */
    private static void quickSort(int[] arr, int l, int r, int level) {
        if (logType == LogUtils.LogType.DEBUG) {
            warn(level, "quickSort %s -- [left:%d, right:%d]", Arrays.toString(arr), l, r);
        }

        if (l < r) {
            // 把数组中随机的一个元素与最后一个元素交换,
            // 这样以最后一个元素作为基准值实际上就是以数组中随机的一个元素作为基准值
            swap(arr, new Random().nextInt(r - l + 1) + l, r, level + 1);

            // 获取一个分区
            int[] p = partition(arr, l, r, level + 1);

            // 基于分区来进行交换
            quickSort(arr, l, p[0] - 1, level + 1);
            quickSort(arr, p[1], r, level + 1);
        }
    }

    /**
     * 分区, 整数数组 arr 的[L, R]部分上，使得：
     * 大于 arr[R] 的元素位于[L, R]部分的右边, 但这部分数据不一定有序
     * 小于 arr[R] 的元素位于[L, R]部分的左边，但这部分数据不一定有序
     * 等于 arr[R] 的元素位于[L, R]部分的中间
     *
     * @param arr 具体数组
     * @param l   下标 - i
     * @param r   下标 - j
     * @return 返回等于部分的第一个元素的下标和最后一个下标组成的整数数组
     */
    private static int[] partition(int[] arr, int l, int r, int level) {
        int basic = arr[r];
        int less = l - 1;
        int more = r + 1;

        if (logType == LogType.DEBUG) {
            info(level, "partition %s -- [left:%d, right:%d]", Arrays.toString(arr), l, r);
        }

        while (l < more) {
            if (arr[l] < basic) {
                swap(arr, ++less, l++, level);
            } else if (arr[l] > basic) {
                swap(arr, --more, l, level);
            } else {
                l++;
            }
        }

        return new int[]{less + 1, more - 1};
    }


    /**
     * 交换数组 arr 中下标为 i 和下标为 j 位置的元素
     *
     * @param arr 具体数组
     * @param i   下标 - i
     * @param j   下标 - j
     */
    private static void swap(int[] arr, int i, int j, int level) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
        if (logType == LogType.DEBUG) {
            error(level, "swap %s -- [i:%d, j:%d]", Arrays.toString(arr), i, j);
        }
    }


    /**
     * 归并排序
     *
     * @param arr
     */
    public static void mergeSort(int[] arr) {
        sort(arr, 0, arr.length - 1);
    }

    /**
     * @param arr   排序数组
     * @param left  左边界
     * @param mid   中间索引
     * @param right 右边界
     */
    private static void merge(int[] arr, int left, int mid, int right) {
        int[] aux = Arrays.copyOfRange(arr, left, right + 1);
        warn("数组: %s, 左边界: %d, 右边界: %d", Arrays.toString(aux), left, right);

        // 初始化, i 指向左半部分的起始索引位置; j 指向右半部分起始索引位置 mid +1
        int i = left, j = mid + 1;
        for (int k = left; k <= right; k++) {

            // 如果左半部分元素已经全部处理完毕
            if (i > mid) {
                arr[k] = aux[j - left];
                j++;
            }

            // 如果右半部分元素已经全部处理完毕
            else if (j > right) {
                arr[k] = aux[i - left];
                i++;
            }

            // 左半部分所指元素 < 右半部分所指元素
            else if (aux[i - left] > aux[j - left]) {
                arr[k] = aux[i - left];
                i++;
            }

            // 左半部分所指元素 >= 右半部分所指元素
            else {
                arr[k] = aux[j - left];
                j++;
            }

        }
    }


    private static void sort(int[] arr, int l, int r) {
        if (l >= r) {
            return;
        }

        int mid = (l + r) / 2;
        LogUtils.error("数组: %s, 左边界: %d, 右边界: %d", Arrays.toString(arr), l, r);
        sort(arr, l, mid);
        sort(arr, mid + 1, r);
        merge(arr, l, mid, r);
    }

    
}
