public class LapCounter {
    private long last = System.nanoTime();

    public long tick() {
        long now = System.nanoTime();
        long delta = now - last;
        last = now; // 每次调用自动更新锚点
        return TimeUnit.NANOSECONDS.toMillis(delta);
    }
}

// 使用时：
LapCounter timer = new LapCounter();
step1();
log.info("Step1: {}ms", timer.tick());
step2();
log.info("Step2: {}ms", timer.tick());
