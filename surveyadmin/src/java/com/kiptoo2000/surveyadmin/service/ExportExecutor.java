package com.kiptoo2000.surveyadmin.service;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/** Application-owned, bounded workers; shut down when the application stops. */
@WebListener
public class ExportExecutor implements ServletContextListener {
    public static final String KEY = ExportExecutor.class.getName();

    @Override public void contextInitialized(ServletContextEvent event) {
        event.getServletContext().setAttribute(KEY, new ThreadPoolExecutor(2, 2,
                0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(8),
                new ThreadPoolExecutor.AbortPolicy()));
    }

    @Override public void contextDestroyed(ServletContextEvent event) {
        ExecutorService executor = (ExecutorService) event.getServletContext().getAttribute(KEY);
        if (executor != null) { executor.shutdownNow(); }
    }
}
