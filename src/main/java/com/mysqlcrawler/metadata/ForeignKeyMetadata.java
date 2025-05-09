package com.mysqlcrawler.metadata;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ForeignKeyMetadata {
    private String name;
    private String columnName;
    private String referencedTableName;
    private String referencedColumnName;
    private String updateRule;
    private String deleteRule;
}
