import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;
import java.util.Map;

public class MdcTaskDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {
        // 1. 此时还在主线程，抓取当前的 MDC 上下文
        Map<String, String> contextMap = MDC.getCopyOfContextMap();
        
        return () -> {
            try {
                // 2. 此时已在线程池线程，将上下文设置进去
                if (contextMap != null) {
                    MDC.setContextMap(contextMap);
                }
                runnable.run();
            } finally {
                // 3. 必须清除，防止线程池复用导致的 traceId 污染
                MDC.clear();
            }
        };
    }
}



@Configuration
public class ThreadPoolConfig {

    @Bean("metadataPool")
    public ThreadPoolTaskExecutor metadataPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // 基础参数配置（对应你第一张图的参数）
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(100);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("data-puring-");
        
        // 拒绝策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        
        // 注入关键的装饰器
        executor.setTaskDecorator(new MdcTaskDecorator());
        
        executor.initialize();
        return executor;
    }
}
