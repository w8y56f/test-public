import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlParser {
    public static void main(String[] args) {
        String sql = "SELECT id, name\n" +
                     "FROM users\n" +
                     "WHERE status = 'active'";

        // 解释正则：
        // (?i)         -> 开启大小写不敏感
        // FROM         -> 匹配 FROM 关键字
        // \\s+         -> 匹配一个或多个空白符（空格、换行、制表符）
        // (.*)         -> 捕获组：匹配后面所有的内容
        // Pattern.DOTALL -> 确保 . 能够匹配换行符
        Pattern pattern = Pattern.compile("(?i)FROM\\s+(.*)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(sql);

        if (matcher.find()) {
            String result = matcher.group(1);
            System.out.println("截取内容如下：");
            System.out.println(result);
        } else {
            System.out.println("未找到 FROM 关键字");
        }
    }
}
