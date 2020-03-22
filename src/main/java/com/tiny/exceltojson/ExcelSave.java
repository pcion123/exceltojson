package com.tiny.exceltojson;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import com.tiny.exceltojson.util.JsonUtil;

public class ExcelSave {
    private String lastExcelPath;
    private String lastJsonPath;

    public ExcelSave() {

    }

    public ExcelSave(InputStream config) throws IOException {
        this(IOUtils.toString(config, "UTF-8"));
    }

    public ExcelSave(String config) {
        Map<String, Object> map = JsonUtil.toMap(config);
        lastExcelPath = String.valueOf(map.get("lastExcelPath"));
        lastJsonPath = String.valueOf(map.get("lastJsonPath"));
    }

    public String getLastExcelPath() {
        return lastExcelPath;
    }

    public void setLastExcelPath(String lastExcelPath) {
        this.lastExcelPath = lastExcelPath;
    }

    public String getLastJsonPath() {
        return lastJsonPath;
    }

    public void setLastJsonPath(String lastJsonPath) {
        this.lastJsonPath = lastJsonPath;
    }
}
