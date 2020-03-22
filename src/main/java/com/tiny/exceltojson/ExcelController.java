package com.tiny.exceltojson;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.util.ResourceBundle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class ExcelController implements Initializable {

    private Stage stage;
    private Scene scene;

    private int logCount = 0;

    @FXML
    private TextArea textLog;
    @FXML
    private TextField excelText;
    @FXML
    private TextField jsonText;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        excelText.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                    String newValue) {
                ExcelApp.getSave().setLastExcelPath(newValue);
            }
        });

        jsonText.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                    String newValue) {
                ExcelApp.getSave().setLastJsonPath(newValue);
            }
        });
    }

    public void setPrimaryStage(Stage stage) {
        this.stage = stage;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public void setExcelText(String text) {
        if (excelText != null) {
            excelText.setText(text);
        }
    }

    public void setJsonText(String text) {
        if (jsonText != null) {
            jsonText.setText(text);
        }
    }

    public void log(String msg, int count) {
        // System.out.println(msg);

        Platform.runLater(() -> {
            // 检查日志行数
            if (logCount > 100) {
                logCount = 0;
                textLog.clear();
            }
            // 增加日志行数
            logCount = logCount + count;
            // 加入日志内容
            textLog.appendText(msg);
        });
    }

    public void warning(String title, String content) {
        warning(title, "", content);
    }

    public void warning(String title, String header, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    public void onConverter() {
        File excelFolder = new File(ExcelApp.getSave().getLastExcelPath());
        if (excelFolder.isDirectory()) {
            String[] files = excelFolder.list(new ExcelFilter());
            for (String file : files) {
                try {
                    String path = ExcelApp.getSave().getLastExcelPath() + "\\" + file;
                    ExcelConverter converter = new ExcelConverter();
                    HSSFSheet sheet = converter.loadExcel(path);
                    String jsons = converter.excelToJson(sheet, "S");
                    String jsonsPath = ExcelApp.getSave().getLastJsonPath() + "\\"
                            + file.replaceFirst(".xls", "_S.json");
                    converter.saveJson(jsonsPath, jsons);
                    String jsonc = converter.excelToJson(sheet, "C");
                    String jsoncPath = ExcelApp.getSave().getLastJsonPath() + "\\"
                            + file.replaceFirst(".xls", "_C.json");
                    converter.saveJson(jsoncPath, jsonc);
                    String note = String.format("convert to %s success.",
                            ExcelApp.getSave().getLastExcelPath() + "\\" + file);
                    ExcelApp.getInstance().log(note);
                } catch (Exception e) {

                }
            }
        }
    }

    public void onExcelPath() {
        DirectoryChooser folderChooser = new DirectoryChooser();
        folderChooser.setTitle("選擇目錄");
        File folder = folderChooser.showDialog(stage);
        if (folder != null && folder.isDirectory()) {
            setExcelText(folder.getAbsolutePath());
            ExcelApp.getSave().setLastExcelPath(folder.getAbsolutePath());
        }
    }

    public void onJsonPath() {
        DirectoryChooser folderChooser = new DirectoryChooser();
        folderChooser.setTitle("選擇目錄");
        File folder = folderChooser.showDialog(stage);
        if (folder != null && folder.isDirectory()) {
            setExcelText(folder.getAbsolutePath());
            ExcelApp.getSave().setLastJsonPath(folder.getAbsolutePath());
        }
    }

    private class ExcelFilter implements FilenameFilter {
        @Override
        public boolean accept(File dir, String name) {
            return name.toLowerCase().endsWith(".xls");
        }
    }

    private class JsonFilter implements FilenameFilter {
        @Override
        public boolean accept(File dir, String name) {
            return name.toLowerCase().endsWith(".json");
        }
    }
}
