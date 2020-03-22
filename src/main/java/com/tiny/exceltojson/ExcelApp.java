package com.tiny.exceltojson;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.tiny.exceltojson.util.JsonUtil;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ExcelApp extends Application {
    private static Logger logger = LoggerFactory.getLogger(ExcelApp.class);

    private static ExcelApp instance;
    private static ExcelController controller;
    private static ExcelSave save;

    private static final ScheduledExecutorService scheduledPool =
            Executors.newSingleThreadScheduledExecutor();

    private static final Queue<String> logQueue = new ConcurrentLinkedQueue<String>();

    public static ExcelApp getInstance() {
        return instance;
    }

    public static ExcelSave getSave() {
        return save;
    }

    @Override
    public void start(Stage primaryStage) {
        instance = this;

        try {
            // 取得版面配置
            FXMLLoader fxmlLoader =
                    new FXMLLoader(getClass().getClassLoader().getResource("view.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            // 取得介面控制器
            controller = fxmlLoader.getController();
            controller.setPrimaryStage(primaryStage);
            controller.setScene(scene);

            primaryStage.setTitle("exceltojson");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
            primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    scheduledPool.shutdown();

                    save();
                }
            });

            load();

            reflashLogs();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static void log(String msg) {
        logQueue.add(msg + "\n");
    }

    public static void warning(String title, String content) {
        if (controller != null) {
            controller.warning(title, "", content);
        }
    }

    public static void warning(String title, String header, String content) {
        if (controller != null) {
            controller.warning(title, header, content);
        }
    }

    private void load() {
        String path = System.getProperty("app.logPath");
        String name = "save.config";

        File saveFile = new File(path + "\\" + name);
        if (saveFile.exists()) {
            try {
                save = new ExcelSave(new FileInputStream(path + "\\" + name));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            save = new ExcelSave();
            save.setLastExcelPath(path + "\\excel");
            save.setLastJsonPath(path + "\\json");
        }
        controller.setExcelText(save.getLastExcelPath());
        controller.setJsonText(save.getLastJsonPath());
    }

    private void save() {
        String path = System.getProperty("app.logPath");
        String name = "save.config";
        try {
            if (save == null) {
                save = new ExcelSave();
                save.setLastExcelPath(path + "\\excel");
                save.setLastJsonPath(path + "\\json");
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(path + "\\" + name));
            bw.write(JsonUtil.obj2JsonStr(save));
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reflashLogs() {
        scheduledPool.scheduleAtFixedRate(new ExcelReflashLog(controller, logQueue), 0, 200,
                TimeUnit.MILLISECONDS);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
