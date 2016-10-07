package org.dbflute.erflute.core.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;

public class POIUtils {

    public static class CellLocation {
        public int r;
        public int c;

        private CellLocation(int r, short c) {
            this.r = r;
            this.c = c;
        }

        @Override
        public String toString() {
            final String str = "(" + this.r + ", " + this.c + ")";
            return str;
        }
    }

    public static CellLocation findCell(HSSFSheet sheet, String str) {
        return findCell(sheet, new String[] { str });
    }

    public static CellLocation findCell(HSSFSheet sheet, String[] strs) {
        for (int rowNum = sheet.getFirstRowNum(); rowNum < sheet.getLastRowNum() + 1; rowNum++) {
            final HSSFRow row = sheet.getRow(rowNum);
            if (row == null) {
                continue;
            }
            for (int i = 0; i < strs.length; i++) {
                final Integer colNum = findColumn(row, strs[i]);
                if (colNum != null) {
                    return new CellLocation(rowNum, colNum.shortValue());
                }
            }
        }
        return null;
    }

    public static Integer findColumn(HSSFRow row, String str) {
        for (int colNum = row.getFirstCellNum(); colNum <= row.getLastCellNum(); colNum++) {
            final HSSFCell cell = row.getCell(colNum);
            if (cell == null) {
                continue;
            }
            if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                final HSSFRichTextString cellValue = cell.getRichStringCellValue();
                if (str.equals(cellValue.getString())) {
                    return Integer.valueOf(colNum);
                }
            }
        }
        return null;
    }

    public static CellLocation findMatchCell(HSSFSheet sheet, String regexp) {
        for (int rowNum = sheet.getFirstRowNum(); rowNum < sheet.getLastRowNum() + 1; rowNum++) {
            final HSSFRow row = sheet.getRow(rowNum);
            if (row == null) {
                continue;
            }
            final Integer colNum = findMatchColumn(row, regexp);
            if (colNum != null) {
                return new CellLocation(rowNum, colNum.shortValue());
            }
        }
        return null;
    }

    public static Integer findMatchColumn(HSSFRow row, String str) {
        for (int colNum = row.getFirstCellNum(); colNum <= row.getLastCellNum(); colNum++) {
            final HSSFCell cell = row.getCell(colNum);
            if (cell == null) {
                continue;
            }
            if (cell.getCellType() != HSSFCell.CELL_TYPE_STRING) {
                continue;
            }
            final HSSFRichTextString cellValue = cell.getRichStringCellValue();
            if (cellValue.getString().matches(str)) {
                return Integer.valueOf(colNum);
            }
        }
        return null;
    }

    public static CellLocation findCell(HSSFSheet sheet, String str, int colNum) {
        for (int rowNum = sheet.getFirstRowNum(); rowNum < sheet.getLastRowNum() + 1; rowNum++) {
            final HSSFRow row = sheet.getRow(rowNum);
            if (row == null) {
                continue;
            }
            final HSSFCell cell = row.getCell(colNum);
            if (cell == null) {
                continue;
            }
            final HSSFRichTextString cellValue = cell.getRichStringCellValue();
            if (!Check.isEmpty(cellValue.getString())) {
                if (cellValue.getString().equals(str)) {
                    return new CellLocation(rowNum, (short) colNum);
                }
            }
        }

        return null;
    }

    public static void replace(HSSFSheet sheet, String keyword, String str) {
        final CellLocation location = findCell(sheet, keyword);

        if (location == null) {
            return;
        }

        setCellValue(sheet, location, str);
    }

    public static String getCellValue(HSSFSheet sheet, CellLocation location) {
        final HSSFRow row = sheet.getRow(location.r);
        final HSSFCell cell = row.getCell(location.c);

        final HSSFRichTextString cellValue = cell.getRichStringCellValue();

        return cellValue.toString();
    }

    public static String getCellValue(HSSFSheet sheet, int r, int c) {
        final HSSFRow row = sheet.getRow(r);

        if (row == null) {
            return null;
        }

        final HSSFCell cell = row.getCell(c);

        if (cell == null) {
            return null;
        }

        final HSSFRichTextString cellValue = cell.getRichStringCellValue();

        return cellValue.toString();
    }

    public static int getIntCellValue(HSSFSheet sheet, int r, int c) {
        final HSSFRow row = sheet.getRow(r);
        if (row == null) {
            return 0;
        }
        final HSSFCell cell = row.getCell(c);

        if (cell.getCellType() != HSSFCell.CELL_TYPE_NUMERIC) {
            return 0;
        }

        return (int) cell.getNumericCellValue();
    }

    public static boolean getBooleanCellValue(HSSFSheet sheet, int r, int c) {
        final HSSFRow row = sheet.getRow(r);

        if (row == null) {
            return false;
        }

        final HSSFCell cell = row.getCell(c);

        if (cell == null) {
            return false;
        }

        return cell.getBooleanCellValue();
    }

    public static short getCellColor(HSSFSheet sheet, int r, int c) {
        final HSSFRow row = sheet.getRow(r);
        if (row == null) {
            return -1;
        }
        final HSSFCell cell = row.getCell(c);

        return cell.getCellStyle().getFillForegroundColor();
    }

    public static void setCellValue(HSSFSheet sheet, CellLocation location, String value) {
        final HSSFRow row = sheet.getRow(location.r);
        final HSSFCell cell = row.getCell(location.c);

        final HSSFRichTextString text = new HSSFRichTextString(value);
        cell.setCellValue(text);
    }

    public static HSSFWorkbook readExcelBook(File excelFile) throws IOException {
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(excelFile);

            return readExcelBook(fis);

        } finally {
            if (fis != null) {
                fis.close();
            }
        }
    }

    public static HSSFWorkbook readExcelBook(InputStream stream) throws IOException {
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(stream);
            return new HSSFWorkbook(bis);

        } finally {
            if (bis != null) {
                bis.close();
            }
        }
    }

    public static void writeExcelFile(File excelFile, HSSFWorkbook workbook) throws IOException {
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;

        try {
            fos = new FileOutputStream(excelFile);
            bos = new BufferedOutputStream(fos);
            workbook.write(bos);

        } finally {
            if (bos != null) {
                bos.close();
            }
            if (fos != null) {
                fos.close();
            }
        }
    }

    public static CellRangeAddress getMergedRegion(HSSFSheet sheet, CellLocation location) {
        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
            final CellRangeAddress region = sheet.getMergedRegion(i);

            final int rowFrom = region.getFirstRow();
            final int rowTo = region.getLastRow();

            if (rowFrom == location.r && rowTo == location.r) {
                final int colFrom = region.getFirstColumn();

                if (colFrom == location.c) {
                    return region;
                }
            }
        }

        return null;
    }

    public static List<CellRangeAddress> getMergedRegionList(HSSFSheet sheet, int rowNum) {
        final List<CellRangeAddress> regionList = new ArrayList<CellRangeAddress>();
        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
            final CellRangeAddress region = sheet.getMergedRegion(i);
            final int rowFrom = region.getFirstRow();
            final int rowTo = region.getLastRow();
            if (rowFrom == rowNum && rowTo == rowNum) {
                regionList.add(region);
            }
        }
        return regionList;
    }

    public static void copyRow(HSSFSheet oldSheet, HSSFSheet newSheet, int oldStartRowNum, int oldEndRowNum, int newStartRowNum) {
        final HSSFRow oldAboveRow = oldSheet.getRow(oldStartRowNum - 1);

        int newRowNum = newStartRowNum;

        for (int oldRowNum = oldStartRowNum; oldRowNum <= oldEndRowNum; oldRowNum++) {
            POIUtils.copyRow(oldSheet, newSheet, oldRowNum, newRowNum++);
        }

        final HSSFRow newTopRow = newSheet.getRow(newStartRowNum);

        if (oldAboveRow != null) {
            for (int colNum = newTopRow.getFirstCellNum(); colNum <= newTopRow.getLastCellNum(); colNum++) {
                final HSSFCell oldAboveCell = oldAboveRow.getCell(colNum);
                if (oldAboveCell != null) {
                    final HSSFCell newTopCell = newTopRow.getCell(colNum);
                    newTopCell.getCellStyle().setBorderTop(oldAboveCell.getCellStyle().getBorderBottom());
                }
            }
        }
    }

    public static void copyRow(HSSFSheet oldSheet, HSSFSheet newSheet, int oldRowNum, int newRowNum) {
        final HSSFRow oldRow = oldSheet.getRow(oldRowNum);

        final HSSFRow newRow = newSheet.createRow(newRowNum);

        if (oldRow == null) {
            return;
        }

        newRow.setHeight(oldRow.getHeight());

        if (oldRow.getFirstCellNum() == -1) {
            return;
        }

        for (int colNum = oldRow.getFirstCellNum(); colNum <= oldRow.getLastCellNum(); colNum++) {
            final HSSFCell oldCell = oldRow.getCell(colNum);
            final HSSFCell newCell = newRow.createCell(colNum);

            if (oldCell != null) {
                final HSSFCellStyle style = oldCell.getCellStyle();
                newCell.setCellStyle(style);

                final int cellType = oldCell.getCellType();
                newCell.setCellType(cellType);

                if (cellType == HSSFCell.CELL_TYPE_BOOLEAN) {
                    newCell.setCellValue(oldCell.getBooleanCellValue());

                } else if (cellType == HSSFCell.CELL_TYPE_FORMULA) {
                    newCell.setCellFormula(oldCell.getCellFormula());

                } else if (cellType == HSSFCell.CELL_TYPE_NUMERIC) {
                    newCell.setCellValue(oldCell.getNumericCellValue());

                } else if (cellType == HSSFCell.CELL_TYPE_STRING) {
                    newCell.setCellValue(oldCell.getRichStringCellValue());
                }
            }
        }

        POIUtils.copyMergedRegion(newSheet, getMergedRegionList(oldSheet, oldRowNum), newRowNum);
    }

    public static void copyMergedRegion(HSSFSheet sheet, List<CellRangeAddress> regionList, int rowNum) {
        for (final CellRangeAddress region : regionList) {
            final CellRangeAddress address = new CellRangeAddress(rowNum, rowNum, region.getFirstColumn(), region.getLastColumn());
            sheet.addMergedRegion(address);
        }
    }

    public static List<HSSFCellStyle> copyCellStyle(HSSFWorkbook workbook, HSSFRow row) {
        final List<HSSFCellStyle> cellStyleList = new ArrayList<HSSFCellStyle>();

        for (int colNum = row.getFirstCellNum(); colNum <= row.getLastCellNum(); colNum++) {

            final HSSFCell cell = row.getCell(colNum);
            if (cell != null) {
                final HSSFCellStyle style = cell.getCellStyle();
                final HSSFCellStyle newCellStyle = copyCellStyle(workbook, style);
                cellStyleList.add(newCellStyle);
            } else {
                cellStyleList.add(null);
            }
        }

        return cellStyleList;
    }

    public static HSSFCellStyle copyCellStyle(HSSFWorkbook workbook, HSSFCellStyle style) {

        final HSSFCellStyle newCellStyle = workbook.createCellStyle();

        newCellStyle.setAlignment(style.getAlignment());
        newCellStyle.setBorderBottom(style.getBorderBottom());
        newCellStyle.setBorderLeft(style.getBorderLeft());
        newCellStyle.setBorderRight(style.getBorderRight());
        newCellStyle.setBorderTop(style.getBorderTop());
        newCellStyle.setBottomBorderColor(style.getBottomBorderColor());
        newCellStyle.setDataFormat(style.getDataFormat());
        newCellStyle.setFillBackgroundColor(style.getFillBackgroundColor());
        newCellStyle.setFillForegroundColor(style.getFillForegroundColor());
        newCellStyle.setFillPattern(style.getFillPattern());
        newCellStyle.setHidden(style.getHidden());
        newCellStyle.setIndention(style.getIndention());
        newCellStyle.setLeftBorderColor(style.getLeftBorderColor());
        newCellStyle.setLocked(style.getLocked());
        newCellStyle.setRightBorderColor(style.getRightBorderColor());
        newCellStyle.setRotation(style.getRotation());
        newCellStyle.setTopBorderColor(style.getTopBorderColor());
        newCellStyle.setVerticalAlignment(style.getVerticalAlignment());
        newCellStyle.setWrapText(style.getWrapText());

        final HSSFFont font = workbook.getFontAt(style.getFontIndex());
        newCellStyle.setFont(font);

        return newCellStyle;
    }

    public static HSSFFont copyFont(HSSFWorkbook workbook, HSSFFont font) {

        final HSSFFont newFont = workbook.createFont();

        // newFont.setBoldweight(font.getBoldweight());
        // newFont.setCharSet(font.getCharSet());
        // newFont.setColor(font.getColor());
        // newFont.setFontHeight(font.getFontHeight());
        // newFont.setFontHeightInPoints(font.getFontHeightInPoints());
        // newFont.setFontName(font.getFontName());
        // newFont.setItalic(font.getItalic());
        // newFont.setStrikeout(font.getStrikeout());
        // newFont.setTypeOffset(font.getTypeOffset());
        // newFont.setUnderline(font.getUnderline());

        return newFont;
    }

    public static HSSFRow insertRow(HSSFSheet sheet, int rowNum) {
        sheet.shiftRows(rowNum + 1, sheet.getLastRowNum(), 1);
        return sheet.getRow(rowNum);
    }
}
