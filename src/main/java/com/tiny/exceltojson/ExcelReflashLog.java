package com.tiny.exceltojson;

import java.util.Queue;

public class ExcelReflashLog implements Runnable {
    private ExcelController controller;
    private Queue<String> queue;

    public ExcelReflashLog(ExcelController controller, Queue<String> queue) {
        this.controller = controller;
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            if (controller != null && queue != null) {
                synchronized (queue) {
                    StringBuilder sb = new StringBuilder();
                    int count = 0;
                    while (!queue.isEmpty()) {
                        String s = queue.poll();
                        if (s != null && s.length() > 0) {
                            sb.append(s);
                            count++;
                        }
                    }
                    if (sb.length() > 0) {
                        controller.log(sb.toString(), count);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
