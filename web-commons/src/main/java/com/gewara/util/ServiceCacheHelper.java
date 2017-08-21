package com.gewara.util;

import java.util.Map;

/**
 * Service方法缓存，每个Service一个
 * @author gebiao(ge.biao@gewara.com)
 * @since Nov 5, 2013 10:25:21 PM
 */
public interface ServiceCacheHelper {
	public Map getStats();
	public Map getAndResetStats();
	public boolean isLocal();
	
	/**
	 * 不需要同步
	 * @param key
	 * @param call
	 * @param cacheSeconds:缓存时间
	 * @return
	 */
	public <T> T cacheCall(Integer cacheSeconds, CachableCall<T> call, String ukey, Object...params);
	public <T> T cacheCallRefresh(Integer cacheSeconds, CachableCall<T> call, String ukey, boolean forceRefresh, Object...params);
	public void updateCache(Object value, Integer cacheSeconds, String ukey, Object...params);
}
