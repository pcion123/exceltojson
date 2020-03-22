package com.tiny.exceltojson;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.CellType;

public class ExcelConverter {
    private static final int FIELD_READNAME_ROW = 0;
    private static final int FIELD_FIELDNAME_ROW = 1;
    private static final int FIELD_FIELDTYPE_ROW = 2;
    private static final int FIELD_PLATFORM_ROW = 3;
    private static final int FIELD_DATA_ROW = 4;
    private static final int FIELD_DATA_COL = 1;

    private static final String FORMAT_STRING = "\"%s\":\"%s\"";
    private static final String FORMAT_VALUE = "\"%s\":%s";

    private String getFormat(String type) {
        if ("STRING".equals(type)) {
            return FORMAT_STRING;
        } else if ("NUMERIC".equals(type)) {
            return FORMAT_VALUE;
        } else if ("BOOLEAN".equals(type)) {
            return FORMAT_VALUE;
        }
        return FORMAT_STRING;
    }

    private String getCellAsString(HSSFCell cell) {
        String value = null;
        if (cell != null) {
            if (cell.getCellType() == CellType.NUMERIC) {
                cell.setCellType(CellType.STRING);
            }
            value = cell.getStringCellValue();
        }
        return value;
    }

    private int getMaxRow(HSSFSheet sheet) {
        int maxRow = 0;
        if (sheet != null) {
            maxRow = sheet.getLastRowNum();
            for (int i = 0; i <= maxRow; i++) {
                HSSFRow row = sheet.getRow(i);
                if (row != null) {
                    String value = getCellAsString(row.getCell(0));
                    if ("EndOfFile".equals(value)) {
                        maxRow = i;
                        break;
                    }
                }
            }
        }
        return maxRow;
    }

    private int getMaxCol(HSSFSheet sheet) {
        int maxCol = 0;
        if (sheet != null) {
            HSSFRow row = sheet.getRow(0);
            if (row != null) {
                maxCol = row.getLastCellNum();
                for (int i = 0; i <= maxCol; i++) {
                    String value = getCellAsString(row.getCell(i));
                    if ("EndOfFile".equals(value)) {
                        maxCol = i;
                        break;
                    }
                }
            }
        }
        return maxCol;
    }

    private boolean checkRow(HSSFCell cell) {
        return cell != null ? "1".equals(getCellAsString(cell)) : false;
    }

    private boolean checkCell(String platform, HSSFCell cell) {
        return cell != null
                ? "CS".equals(getCellAsString(cell)) || platform.equals(getCellAsString(cell))
                : false;
    }

    private String parse(String fieldType, String fieldName, String fieldValue) {
        String format = getFormat(fieldType);
        if (format != null) {
            return String.format(format, fieldName, fieldValue);
        } else {
            return null;
        }
    }

    private String analyzer(HSSFSheet sheet, String platform, int maxRow, int maxCol) {
        if (sheet != null) {
            List<String> jsons = new ArrayList<String>();
            HSSFRow fieldNameRow = sheet.getRow(FIELD_FIELDNAME_ROW);
            for (int i = FIELD_DATA_ROW; i < maxRow; i++) {
                HSSFRow dataRow = sheet.getRow(i);
                if (checkRow(dataRow.getCell(0))) {
                    List<String> fields = new ArrayList<String>();
                    for (int j = FIELD_DATA_COL; j < maxCol; j++) {
                        HSSFRow fieldTypeRow = sheet.getRow(FIELD_FIELDTYPE_ROW);
                        HSSFRow platformRow = sheet.getRow(FIELD_PLATFORM_ROW);
                        if (checkCell(platform, platformRow.getCell(j))) {
                            String fieldType = getCellAsString(fieldTypeRow.getCell(j));
                            String fieldName = getCellAsString(fieldNameRow.getCell(j));
                            String fieldValue = getCellAsString(dataRow.getCell(j));
                            fields.add(parse(fieldType, fieldName, fieldValue));
                        }
                    }
                    if (fields.size() > 0) {
                        jsons.add(String.format("{%s}", fields.stream().map(b -> b.toString())
                                .collect(Collectors.joining(","))));
                    }
                }
            }
            if (jsons.size() > 0) {
                return String.format("[%s]",
                        jsons.stream().map(b -> b.toString()).collect(Collectors.joining(",")));
            }
        }
        return null;
    }

    public String excelToJson(HSSFSheet sheet, String platform) {
        int maxRow = getMaxRow(sheet);
        int maxCol = getMaxCol(sheet);
        return analyzer(sheet, platform, maxRow, maxCol);
    }

    public HSSFSheet loadExcel(String path) throws IOException {
        POIFSFileSystem fileSystem = new POIFSFileSystem(new FileInputStream(path));
        HSSFWorkbook workbook = new HSSFWorkbook(fileSystem);
        return workbook.getSheet("Sheet1");
    }

    public HSSFSheet loadExcel(File file) throws IOException {
        POIFSFileSystem fileSystem = new POIFSFileSystem(new FileInputStream(file));
        HSSFWorkbook workbook = new HSSFWorkbook(fileSystem);
        return workbook.getSheet("Sheet1");
    }

    public void saveJson(String path, String json) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(path));
        bw.write(json);
        bw.close();
    }

    public static void main(String[] args) {
        try {
            ExcelConverter converter = new ExcelConverter();
            HSSFSheet sheet = converter.loadExcel("D:\\work_space\\exceltojson\\ItemData.xls");
            String json = converter.excelToJson(sheet, "S");
            System.out.println(json);
            converter.saveJson("D:\\work_space\\exceltojson\\ItemData.json", json);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
