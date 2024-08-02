package com.kkbpro.terminal.controller;

import com.kkbpro.terminal.config.AppConfig;
import com.kkbpro.terminal.pojo.vo.OSInfo;
import com.kkbpro.terminal.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 系统信息接口类
 **/

@RestController
@RequestMapping("/api")
public class SystemController {

    public static String serverOS = "Linux";

    private static volatile Thread monitor;

    private static ConcurrentHashMap<String, Long> windowActiveMap = new ConcurrentHashMap<>();


    @Autowired
    private AppConfig appConfig;


    /**
     * 新建窗口初始化
     */
    @GetMapping("/init")
    public Result init(@RequestHeader("User-Agent") String userAgent) {
        String clientOS = getOSFromUA(userAgent);
        String windowId = UUID.randomUUID().toString();
        OSInfo osInfo = new OSInfo(serverOS, clientOS, windowId);
        // PC端，Windows和Mac
        if(!"Linux".equals(serverOS) && appConfig.getPcWindowTag()) {
            windowActiveMap.put(windowId, new Date().getTime());
            startMonitor();
        }
        return Result.setSuccess("初始化成功", osInfo);
    }

    private String getOSFromUA(String userAgent) {
        userAgent = userAgent.toLowerCase();
        if (userAgent.contains("windows")) {
            return "Windows";
        } else if (userAgent.contains("mac")) {
            return "Mac";
        } else if (userAgent.contains("linux")) {
            return "Linux";
        } else if (userAgent.contains("android")) {
            return "Android";
        } else if (userAgent.contains("iphone") || userAgent.contains("ipad")) {
            return "iOS";
        } else {
            return "Other";
        }
    }


    /**
     * 开启监视线程
     */
    private void startMonitor() {
        if(monitor == null) {
            synchronized (this) {
                if(monitor == null) {
                    monitor = new Thread(() -> {
                        try {
                            while (!windowActiveMap.isEmpty()) {
                                Iterator<Map.Entry<String, Long>> iterator = windowActiveMap.entrySet().iterator();
                                while (iterator.hasNext()) {
                                    Map.Entry<String, Long> entry = iterator.next();
                                    // 超过33s则移除（判定为窗口已关闭）
                                    if (new Date().getTime() - entry.getValue() > 1000L * 33) {
                                        iterator.remove();
                                    }
                                }
                                Thread.sleep(1000); // 每隔1秒处理一次
                            }
                            System.exit(0); // 结束进程
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
                    monitor.start();
                }
            }
        }
    }


    /**
     * 窗口心跳续约
     */
    @PostMapping ("/beat")
    public Result beat(String windowId) {
        if(!"Linux".equals(serverOS) && appConfig.getPcWindowTag() && windowId != null)
            windowActiveMap.replace(windowId, new Date().getTime());
        return Result.setSuccess("窗口心跳续约成功", null);
    }

    /**
     * 是否开启监视线程
     */
    public static Boolean isEnableMonitor() {
        return monitor != null;
    }
}
