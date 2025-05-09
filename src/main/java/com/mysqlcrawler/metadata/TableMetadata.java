package com.mysqlcrawler.metadata;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@NoArgsConstructor

public class TableMetadata {
    private String name;
    private List<ColumnMetadata> columns = new ArrayList<>();
    private List<String> primaryKeys = new ArrayList<>();
    private List<ForeignKeyMetadata> foreignKeys = new ArrayList<>();
    Map<String, IndexMetadata> indexes = new LinkedHashMap<>();
    public TableMetadata(String name) {
        this.name = name;
    }

    public void addColumn(ColumnMetadata column) {
        columns.add(column);
    }

    public void addPrimaryKey(String columnName) {
        primaryKeys.add(columnName);
    }

    public void addForeignKey(ForeignKeyMetadata fk) {
        foreignKeys.add(fk);
    }

    public void addIndexInfo(String indexName, boolean unique,  String columnName) {
        if (indexName == null || columnName == null) return;

        IndexMetadata metadata = indexes.get(indexName);
        if (metadata == null) {
            metadata = new IndexMetadata(indexName, unique,new ArrayList<>());
            indexes.put(indexName, metadata);
        }
        metadata.getColumnNames().add(columnName);
    }
}
