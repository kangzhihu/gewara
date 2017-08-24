package com.gewara.support;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import com.gewara.util.GewaLogger;
import com.gewara.util.HttpUtils;
import com.gewara.util.LoggerUtils;

public abstract class ExecutorManager {
	private static final transient GewaLogger dbLogger = LoggerUtils.getLogger(HttpUtils.class);
	/**
	 * 注册的线程池，当系统重新启动时，先等待线程池中的任务完成，最大30s
	 * @param executor
	 * @param maxWaitSecondsBeforeDomainRestart
	 */
	private static Map<String, ThreadPoolExecutor> executorMap = new HashMap<String, ThreadPoolExecutor>();
	public static void registerExecutor(String name, ThreadPoolExecutor executor){
		executorMap.put(name, executor);
	}
	/**
	 * 在系统重启前，等待一段时间，便于线程池执行完任务
	 * @param totalWait
	 */
	public static void waitComplete(int totalWait){
		for(Map.Entry<String, ThreadPoolExecutor> entry: executorMap.entrySet()){
			while(totalWait > 0 && entry.getValue().getActiveCount()>0){
				try {
					Thread.sleep(1000L);
					dbLogger.warn("wait:" + entry.getKey() + " to complete!" + totalWait);
				} catch (InterruptedException e) {
				}
				totalWait --;
			}
		}
	}
}
