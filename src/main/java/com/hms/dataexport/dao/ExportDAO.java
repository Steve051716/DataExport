package com.hms.dataexport.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface ExportDAO {

    public List<LinkedHashMap<String, Object>> findRecords(@Param("executeSql")String executeSql);
}
