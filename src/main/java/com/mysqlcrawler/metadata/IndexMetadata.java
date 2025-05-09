package com.mysqlcrawler.metadata;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndexMetadata {
    private String indexName;
    private boolean unique;
    private List<String> columnNames;
}