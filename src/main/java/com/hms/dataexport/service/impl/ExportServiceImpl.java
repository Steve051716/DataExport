package com.hms.dataexport.service.impl;

import com.hms.dataexport.dao.ExportDAO;
import com.hms.dataexport.service.ExportService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.List;

@Service
public class ExportServiceImpl implements ExportService {

    @Inject
    private ExportDAO exportDAO;

    @Override
    public List<LinkedHashMap<String, Object>> findRecords(String executeSql) {
        return exportDAO.findRecords(executeSql);
    }
}
