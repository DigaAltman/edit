package cn.bbzzzs.orm.repositoy;

import cn.bbzzzs.common.util.ArrayUtil;
import cn.bbzzzs.db.factory.DB;
import cn.bbzzzs.orm.pojo.TableSize;
import cn.bbzzzs.orm.pojo.ThreadSize;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Repository("cn.bbzzzs.mysql.repositoy.DataBaseDao")
public class DataBaseDao {

    /**
     * 获取数据库的字符集
     *
     * @param db
     * @return
     */
    public String getCharset(DB db) {
        List<Map> mapList = db.selectList("show variables like '%character%';", Map.class);
        for (Map<String,String> map : mapList) {
            if (map.get("Variable_name").equals("character_set_database")) {
                return map.get("Value");
            }
        }
        return null;
    }

    /**
     * 获取数据库的存储引擎
     *
     * @param db
     * @return
     */
    public String getEngine(DB db) {
        for (Map<String,String> map : db.selectList("show variables like '%storage_engine%'", Map.class)) {
            if(map.get("Variable_name").equals("default_storage_engine")) {
                return map.get("Value");
            }
        }
        return null;
    }

    /**
     * 获取数据库的版本号
     */
    public String getVersion(DB db) {
        Map versionMap = db.selectOne("select version() AS version", Map.class);
        return (String) versionMap.get("version");
    }

    /**
     * 获取数据库的大小
     */
    public TableSize getDataBaseSize(DB db) {
        TableSize dbSize = db.selectOne("select  sum(data_length) as data_size,  sum(index_length) as index_size from  information_schema.tables;", TableSize.class);
        return dbSize;
    }

    /**
     * 获取数据库的缓存区大小
     */
    public Long getCacheSize(DB db) {
        Map map = db.selectOne("select @@global.query_cache_size AS cache_size;", Map.class);
        return Long.parseLong(map.get("cache_size").toString());
    }

    /**
     * 获取数据库连接数大小
     */
    public Integer getConnectionNumber(DB db) {
        Map map = db.selectOne("show variables like '%max_connections%' ", Map.class);
        return Integer.parseInt(map.get("Value").toString());
    }

    /**
     * 获取数据库线程相关信息
     */
    public ThreadSize getThreadSize(DB db) {
        ThreadSize threadSize = new ThreadSize();
        List<Map> mapList = db.selectList("show status like  'Threads%';", Map.class);
        for (Map map : mapList) {
            String val = map.get("Value").toString();
            String key = map.get("Variable_name").toString();
            if(key.equals("Threads_cached")) {
                threadSize.setCacheSize(Integer.parseInt(val));
            }
            else if(key.equals("Threads_connected")) {
                threadSize.setConnectSize(Integer.parseInt(val));
            }
            else if(key.equals("Threads_created")) {
                threadSize.setCreatedSize(Integer.parseInt(val));
            }
            else if(key.equals("Threads_running")) {
                threadSize.setRunSize(Integer.parseInt(val));
            }
        }
        return threadSize;
    }

    /**
     * 获取数据库数据存放目录
     */
    public String getDir(DB db) {
        Map map = db.selectOne("show variables like '%datadir%';", Map.class);
        return map.get("Value").toString();
    }

    /**
     * 获取当前登录的用户
     */
    public String getCurrentUser(DB db) {
        Map map = db.selectOne("SELECT CURRENT_USER() AS user", Map.class);
        return map.get("user").toString();
    }

    /**
     * 获取当前登录用户的权限
     */
    public String getAuthority(DB db) {
        List<Map> mapList = db.selectList("show grants for current_user()", Map.class);
        List<String> authorityList = Lists.newArrayList();
        mapList.forEach(map -> {
            for (Object o : map.keySet()) {
                String user = (String) o;
                String authorityExpr = map.get(user).toString();
                authorityList.add(parseAuthority(authorityExpr));
            }
        });
        return ArrayUtil.toString(authorityList);
    }

    /**
     * 解析权限表达式
     * @param authorityExpr
     * @return
     */
    private static String parseAuthority(String authorityExpr) {
        if(Pattern.matches("GRANT .* ON .*", authorityExpr)) {
            // "GRANT USAGE ON *.* TO 'db1'@'%'"
            return authorityExpr.substring(6, authorityExpr.indexOf("ON") - 1);
        }
        return "";
    }
}
