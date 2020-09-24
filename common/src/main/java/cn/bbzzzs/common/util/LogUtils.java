package cn.bbzzzs.common.util;


public class LogUtils {

    public static LogType logType = LogType.DEBUG;

    public enum LogType {
        DEBUG, INFO, WARN, ERROR
    }

    public static void setLogType(LogType logType) {
        LogUtils.logType = logType;
    }

    /**
     * 打印等级日志
     *
     * @param level  层级等级
     * @param format format内容
     * @param args   format参数
     */
    private static void _native(String format, Integer level, LogType logType, Object... args) {
        StringBuilder sb = new StringBuilder();
        if (level != null) {
            for (int i = 0; i < level; i++) {
                sb.append("\t");
            }
        }

        switch (logType) {
            case DEBUG:
                sb.append("\033[36;0m").append(String.format(format, args)).append("\033[0m");
                break;
            case INFO:
                sb.append("\033[32;0m").append(String.format(format, args)).append("\033[0m");
                break;
            case WARN:
                sb.append("\033[33;1m").append(String.format(format, args)).append("\033[1m");
                break;
            case ERROR:
                sb.append("\033[31;1m").append(String.format(format, args)).append("\033[1m");
                break;
        }

        System.out.println(sb.toString());
    }

    public static void warn(Integer level, String format, Object... args) {
        _native(format, level, LogType.WARN, args);
    }

    public static void warn(String format, Object... args) {
        warn(null, format, args);
    }

    public static void error(Integer level, String format, Object... args) {
        _native(format, level, LogType.ERROR, args);
    }

    public static void error(String format, Object... args) {
        error(null, format, args);
    }

    public static void info(Integer level, String format, Object... args) {
        _native(format, level, LogType.INFO, args);
    }

    public static void info(String format, Object... args) {
        info(null, format, args);
    }

    public static void debug(Integer level, String format, Object... args) {
        _native(format, level, LogType.DEBUG, args);
    }

    public static void debug(String format, Object... args) {
        debug(null, format, args);
    }

}
