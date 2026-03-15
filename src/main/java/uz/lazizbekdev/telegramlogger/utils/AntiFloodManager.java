package uz.lazizbekdev.telegramlogger.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Rate limiter that prevents message flooding to Telegram and Minecraft.
 * Uses a sliding window approach per message category.
 */
public class AntiFloodManager {

    private final Map<String, SlidingWindow> windows = new ConcurrentHashMap<>();

    private int maxMessagesPerWindow;
    private long windowMillis;
    private boolean enabled;

    public AntiFloodManager(boolean enabled, int maxPerWindow, long windowSeconds) {
        this.enabled = enabled;
        this.maxMessagesPerWindow = maxPerWindow;
        this.windowMillis = windowSeconds * 1000L;
    }

    /**
     * Check whether a message of the given type is allowed to send right now.
     * Returns true if allowed, false if rate-limited.
     */
    public boolean tryAcquire(String type) {
        if (!enabled) return true;
        SlidingWindow window = windows.computeIfAbsent(type, k -> new SlidingWindow());
        return window.tryAcquire();
    }

    /**
     * Check global rate limit across all types combined.
     */
    public boolean tryAcquireGlobal() {
        return tryAcquire("__global__");
    }

    public void updateSettings(boolean enabled, int maxPerWindow, long windowSeconds) {
        this.enabled = enabled;
        this.maxMessagesPerWindow = maxPerWindow;
        this.windowMillis = windowSeconds * 1000L;
        windows.clear();
    }

    public void reset() {
        windows.clear();
    }

    private class SlidingWindow {
        private long windowStart;
        private final AtomicInteger count = new AtomicInteger(0);

        SlidingWindow() {
            this.windowStart = System.currentTimeMillis();
        }

        synchronized boolean tryAcquire() {
            long now = System.currentTimeMillis();
            if (now - windowStart >= windowMillis) {
                windowStart = now;
                count.set(1);
                return true;
            }
            return count.incrementAndGet() <= maxMessagesPerWindow;
        }
    }
}
