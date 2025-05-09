package com.mysqlcrawler.metadata;

import lombok.Data;
import java.util.List;

@Data
public class DatabaseMetadata {
    private String schemaName;
    private List<TableMetadata> tables;
}
