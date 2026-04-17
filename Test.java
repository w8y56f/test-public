import org.slf4j.MDC;
import java.util.Map;
import java.util.concurrent.Executor;

public class MdcTaskDecorator {
    public static Runnable wrap(Runnable runnable) {
        // 1. 从当前线程获取 MDC 内容
        Map<String, String> contextMap = MDC.getCopyOfContextMap();
        return () -> {
            try {
                // 2. 在线程池线程中设置 MDC
                if (contextMap != null) {
                    MDC.setContextMap(contextMap);
                }
                runnable.run();
            } finally {
                // 3. 执行完后必须清除，防止线程复用导致的污染
                MDC.clear();
            }
        };
    }
}




// 在 ThreadPoolConfig 类里，建议定义一个专门用于 MDC 传递的 Executor 包装
private static final ExecutorService innerPool = new ThreadPoolExecutor(
    10, 20, 60L, TimeUnit.SECONDS,
    new LinkedBlockingQueue<>(100),
    new ThreadFactoryBuilder().setNameFormat("data-puring-%d").build(),
    new ThreadPoolExecutor.CallerRunsPolicy()
);

// 最终暴露给外面使用的 metadataPool
public static final Executor metadataPool = runnable -> innerPool.execute(MdcTaskDecorator.wrap(runnable));
