package com.hms.dataexport.service;

import com.hms.dataexport.utils.ExecuteResult;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface ExportService {

    public List<LinkedHashMap<String, Object>> findRecords(String executeSql);
}
