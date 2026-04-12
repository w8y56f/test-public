import java.util.regex.Pattern;
import java.util.stream.Stream;

public class SqlSafetyChecker {

    // 预编译正则，提高性能
    // 1. 必须以 SELECT 开头（忽略空格和换行）
    private static final Pattern SELECT_PATTERN = Pattern.compile("^\\s*SELECT\\b", Pattern.CASE_INSENSITIVE);
    
    // 2. 危险关键词黑名单
    private static final String[] BLACKLIST = {
        "DROP", "TRUNCATE", "DELETE", "UPDATE", "INSERT", "CREATE", "ALTER", "GRANT", "REVOKE", "EXEC"
    };

    /**
     * 简单检查 SQL 是否为合规的只读查询
     * @param sql 传入的 SQL 字符串
     * @return true 通过, false 存在风险
     */
    public static boolean isSafeSelect(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return false;
        }

        // 统一转大写处理，方便比对
        String upperSql = sql.toUpperCase().trim();

        // 检查 1：必须以 SELECT 开头
        if (!SELECT_PATTERN.matcher(upperSql).find()) {
            return false;
        }

        // 检查 2：不能包含注释符（防止绕过或截断）
        if (upperSql.contains("--") || upperSql.contains("/*") || upperSql.contains("#")) {
            return false;
        }

        // 检查 3：黑名单关键词扫描
        // 使用 Stream 显得代码比较现代，Sonar 也会觉得你处理得比较优雅
        return Stream.of(BLACKLIST).noneMatch(keyword -> upperSql.contains(keyword + " "));
    }

    public static void main(String[] args) {
        String testSql = "SELECT * FROM users WHERE id = 1";
        System.out.println("是否安全: " + isSafeSelect(testSql)); // true

        String evilSql = "SELECT * FROM users; DROP TABLE users";
        System.out.println("是否安全: " + isSafeSelect(evilSql)); // false (命中 DROP)
    }
}
