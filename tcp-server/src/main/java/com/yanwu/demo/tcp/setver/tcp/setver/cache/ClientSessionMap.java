package com.yanwu.demo.tcp.setver.tcp.setver.cache;

import com.yanwu.demo.tcp.setver.tcp.setver.swing.SwingUtil;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-08-09 11:25.
 * <p>
 * description:
 */
@Slf4j
public final class ClientSessionMap {

    private static final String CACHE_NAME = "ctxId_ctx_local_cache";
    private static final Cache SESSION_MAP = CacheManager.create().getCache(CACHE_NAME);

    public static void sessionSync() {
        try {
            int size = 0;
            List keys = SESSION_MAP.getKeys();
            if (!CollectionUtils.isEmpty(keys)) {
                size = keys.size();
            }
            String message = "当前检测到长连接数目: " + size;
            SwingUtil.printLog(message, null);
            SwingUtil.setConnectionNum();
        } catch (Exception e) {
            log.error("[local cache] monitor has occured error, cause: " + e);
        }
    }

    public static void put(String ctxId, ChannelHandlerContext channel) {
        Element ctxElement = new Element(ctxId, channel);
        SESSION_MAP.put(ctxElement);
    }

    public static ChannelHandlerContext get(String ctxId) {
        Element element = SESSION_MAP.get(ctxId);
        return element == null ? null : (ChannelHandlerContext) element.getObjectValue();
    }

    public static void remove(String ctxId) {
        SESSION_MAP.remove(ctxId);
    }
}
