package com.dyx.simpledb.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class UserManager {
    private static final int MAX_USERS = 20;
    private static final int MAX_SESSION_DURATION = 2 * 60 * 60 * 1000; // 2 hours in milliseconds

    private ConcurrentHashMap<String, UserSession> activeUsers = new ConcurrentHashMap<>();
    private AtomicInteger userCount = new AtomicInteger(0);

    @Value("${custom.db.path}")
    private String dbPath;

    public boolean canInit(String userId) {
        if (userCount.get() >= MAX_USERS) {
            return false;
        }
        activeUsers.put(userId, new UserSession(userId, System.currentTimeMillis()));
        userCount.incrementAndGet();
        return true;
    }

    public UserSession getUserSession(String userId) {
        return activeUsers.get(userId);
    }

    @Scheduled(fixedRate = 60000) // 每分钟执行一次
    public void checkSessions() {
        long currentTime = System.currentTimeMillis();
        activeUsers.forEach((userId, session) -> {
            if (currentTime - session.getStartTime() >= MAX_SESSION_DURATION) {
                destroyDatabase(userId);
                activeUsers.remove(userId);
                userCount.decrementAndGet();
            }
        });
    }

    private void destroyDatabase(String userId) {
        // 先关闭数据库文件
        UserSession session = activeUsers.get(userId);
        if (session != null) {
            session.close();
        }
        // 删除数据库目录和文件的逻辑
        String directoryPath = dbPath + File.separator + userId;
        File directory = new File(directoryPath);
        boolean deleted = deleteDirectory(directory);
        if (!deleted) {
            System.err.println("Failed to delete directory: " + directoryPath);
        }
    }

    private boolean deleteDirectory(File directory) {
        if (directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    deleteFileWithRetry(file);
                }
            }
        }
        return directory.delete();
    }

    private boolean deleteFileWithRetry(File file) {
        int retryCount = 3;
        int retryInterval = 1000; // 1 second
        boolean deleted = false;

        for (int i = 0; i < retryCount; i++) {
            if (file.delete()) {
                deleted = true;
                break;
            }
            try {
                Thread.sleep(retryInterval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        if (!deleted) {
            System.err.println("Failed to delete file: " + file.getAbsolutePath());
        }

        return deleted;
    }
}
