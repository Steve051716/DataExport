package com.hms.dataexport.controller;

import com.alibaba.fastjson.JSON;
import com.hms.dataexport.service.ExportService;
import com.hms.dataexport.utils.ExecuteResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 根据sql导出excel
 * @author gaoyuhang
 */
@Controller
@RequestMapping(value = "/data")
public class ExportController {

    private static final Logger LOG = LoggerFactory.getLogger(ExportController.class);
    private static final String MESSAGE_FIELDS_NULL = "文件名或执行SQL不能为空";
    private static final String MESSAGE_NO_RECORD = "查询无结果集。";
    private static final String MESSAGE_ILLEGAL_KEYWORDS = "此SQL并不是查询语句。";
    private static final String FILE_SUFFIX = ".xls";
    private static final String SQL_PREFIX = "SELECT";
    private static final String DATE_FORMATE_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    @Inject
    private ExportService exportService;

    @RequestMapping
    public ModelAndView index() {
        return new ModelAndView("export");
    }

    /**
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/export", method = RequestMethod.POST)
    @ResponseBody
    public String export(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ExecuteResult result = new ExecuteResult();
        String fileName = request.getParameter("fileName");
        String executeSql = request.getParameter("executeSql");
        if (StringUtils.isBlank(fileName) || StringUtils.isBlank(executeSql)) {
            result.setStatus(false);
            result.setResult(MESSAGE_FIELDS_NULL);
            return JSON.toJSONString(result);
        }
        String checkSql = executeSql.trim().toUpperCase();
        if (!checkSql.startsWith(SQL_PREFIX)) {
            result.setStatus(false);
            result.setResult(MESSAGE_ILLEGAL_KEYWORDS);
            return JSON.toJSONString(result);
        }
        LOG.info(executeSql);
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMATE_YYYYMMDDHHMMSS);
        fileName += sdf.format(new Date()) + FILE_SUFFIX;
        List<LinkedHashMap<String, Object>> resultMapList = exportService.findRecords(executeSql);
        if (resultMapList == null || resultMapList.size() == 0) {
            result.setStatus(false);
            result.setResult(MESSAGE_NO_RECORD);
            return JSON.toJSONString(result);
        }
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet resultSheet = workbook.createSheet("result");
        createTitle(workbook, resultSheet, resultMapList.get(0));
        //新增数据行，并且设置单元格数据
        int rowNum=1;
        for(Map<String, Object> resultMap : resultMapList){
            HSSFRow row = resultSheet.createRow(rowNum);
            int cellIndex = 0;
            //遍历map中的值
            for (Object value : resultMap.values()) {
                row.createCell(cellIndex).setCellValue(value != null ? value.toString() : null);
                cellIndex++;
            }
            rowNum++;
        }
        HSSFSheet sqlSheet = workbook.createSheet("SQL");
        HSSFRow sqlRow = sqlSheet.createRow(0);
        sqlRow.createCell(0).setCellValue(executeSql);
        //生成excel文件
        // buildExcelFile(fileName, workbook);
        //浏览器下载excel
        buildExcelDocument(fileName,workbook,response);
        return null;
    }

    /**
     * 创建表头
     * @param workbook
     * @param sheet
     * @param tilteMap
     */
    private void createTitle(HSSFWorkbook workbook, HSSFSheet sheet, LinkedHashMap tilteMap){
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell;
        //遍历map中的键
        int cellIndex = 0;
        for (Object key : tilteMap.keySet()) {
            cell = row.createCell(cellIndex);
            cell.setCellValue(key != null ? key.toString() : null);
            cellIndex++;
        }
    }

    /**
     * 生成excel文件
     * @param filename
     * @param workbook
     * @throws Exception
     */
    protected void buildExcelFile(String filename,HSSFWorkbook workbook) throws Exception{
        FileOutputStream fos = new FileOutputStream(filename);
        workbook.write(fos);
        fos.flush();
        fos.close();
    }

    /**
     * 下载excel
     * @param filename
     * @param workbook
     * @param response
     * @throws Exception
     */
    protected void buildExcelDocument(String filename,HSSFWorkbook workbook,HttpServletResponse response) throws Exception{
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "utf-8"));
        OutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        outputStream.flush();
        outputStream.close();
    }
}
