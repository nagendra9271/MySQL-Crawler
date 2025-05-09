package com.mysqlcrawler.metadata;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ColumnMetadata {
    private String name;
    private String type;
    private int size;
    private boolean nullable;
    private String defaultValue;
    private boolean autoIncrement;
    private boolean isUnsigned;
}
