package com.mysqlcrawler.service;

import com.mysqlcrawler.config.Config;
import com.mysqlcrawler.config.ConfigLoader;
import com.mysqlcrawler.metadata.ColumnMetadata;
import com.mysqlcrawler.metadata.DatabaseMetadata;
import com.mysqlcrawler.metadata.ForeignKeyMetadata;
import com.mysqlcrawler.metadata.TableMetadata;

import java.sql.*;
import java.util.*;


public class SchemaCrawlerService {
    private final Connection connection;
    private final Config config;

    public SchemaCrawlerService() throws Exception {
        this.config = ConfigLoader.loadConfig();
        Class.forName(config.getDriverClassName());
        this.connection = DriverManager.getConnection(
                config.getUrl(),
                config.getUsername(),
                config.getPassword()
        );
    }

    public DatabaseMetadata extractDatabaseMetadata() throws SQLException {
        DatabaseMetadata metadata = new DatabaseMetadata();
        metadata.setSchemaName(config.getCatalog());
        metadata.setTables(extractTables());
        return metadata;
    }

    private List<TableMetadata> extractTables() throws SQLException {
        List<TableMetadata> tables = new ArrayList<>();
        DatabaseMetaData metaData = connection.getMetaData();
        
        try (ResultSet rs = metaData.getTables(config.getCatalog(), null, "%", new String[]{"TABLE"})) {
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                TableMetadata table = new TableMetadata(tableName);
                
                // Extract columns
                extractColumns(metaData, table);
                
                // Extract primary keys
                extractPrimaryKeys(metaData, table);
                
                // Extract foreign keys
                extractForeignKeys(metaData, table);
                
                // Extract indexes
                extractIndexes(metaData, table);
                
                tables.add(table);
            }
        }
        return tables;
    }

    private void extractColumns(DatabaseMetaData metaData, TableMetadata table) throws SQLException {
    try (ResultSet rs = metaData.getColumns(config.getCatalog(), null, table.getName(), "%")) {
        while (rs.next()) {
            ColumnMetadata column = new ColumnMetadata();
            column.setName(rs.getString("COLUMN_NAME"));

            String rawType = rs.getString("TYPE_NAME"); // e.g., "INT UNSIGNED"
            column.setType(rawType);
            column.setUnsigned(rawType.toUpperCase().contains("UNSIGNED")); // Detect unsigned

            column.setSize(rs.getInt("COLUMN_SIZE"));
            column.setNullable(rs.getInt("NULLABLE") == DatabaseMetaData.columnNullable);
            column.setDefaultValue(rs.getString("COLUMN_DEF"));
            column.setAutoIncrement("YES".equals(rs.getString("IS_AUTOINCREMENT")));

            table.addColumn(column);
        }
    }
}


    private void extractPrimaryKeys(DatabaseMetaData metaData, TableMetadata table) throws SQLException {
        try (ResultSet rs = metaData.getPrimaryKeys(config.getCatalog(), null, table.getName())) {
            while (rs.next()) {
                String columnName = rs.getString("COLUMN_NAME");
                table.addPrimaryKey(columnName);
            }
        }
    }

    private void extractForeignKeys(DatabaseMetaData metaData, TableMetadata table) throws SQLException {
        try (ResultSet rs = metaData.getImportedKeys(config.getCatalog(), null, table.getName())) {
            while (rs.next()) {
                ForeignKeyMetadata fk = new ForeignKeyMetadata();
                fk.setName(rs.getString("FK_NAME"));
                fk.setColumnName(rs.getString("FKCOLUMN_NAME"));
                fk.setReferencedTableName(rs.getString("PKTABLE_NAME"));
                fk.setReferencedColumnName(rs.getString("PKCOLUMN_NAME"));
                fk.setUpdateRule(parseForeignKeyRule(rs.getShort("UPDATE_RULE")));
                fk.setDeleteRule(parseForeignKeyRule(rs.getShort("DELETE_RULE")));
                
                table.addForeignKey(fk);
            }
        }
    }

    private void extractIndexes(DatabaseMetaData metaData, TableMetadata table) throws SQLException {
        try (ResultSet rs = metaData.getIndexInfo(config.getCatalog(), null, table.getName(), false, false)) {
            
            while (rs.next()) {
                String indexName = rs.getString("INDEX_NAME");
                String columnName = rs.getString("COLUMN_NAME");
                boolean nonUnique = rs.getBoolean("NON_UNIQUE");
                 
                table.addIndexInfo(indexName, !nonUnique, columnName);
            }
        }
    }

    private String parseForeignKeyRule(short rule) {
        switch (rule) {
            case DatabaseMetaData.importedKeyCascade: return "CASCADE";
            case DatabaseMetaData.importedKeyRestrict: return "RESTRICT";
            case DatabaseMetaData.importedKeySetNull: return "SET NULL";
            case DatabaseMetaData.importedKeyNoAction: return "NO ACTION";
            case DatabaseMetaData.importedKeySetDefault: return "SET DEFAULT";
            default: return "UNKNOWN";
        }
    }

    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public List<String> getTableNames() throws SQLException {
        List<String> tables = new ArrayList<>();
        DatabaseMetaData metaData = connection.getMetaData();
        
        try (ResultSet rs = metaData.getTables(config.getCatalog(), null, "%", new String[]{"TABLE"})) {
            while (rs.next()) {
                tables.add(rs.getString("TABLE_NAME"));
            }
        }
        return tables;
    }

    public TableMetadata getTableMetadata(String tableName) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        TableMetadata table = new TableMetadata(tableName);
        
        // Extract all table details
        extractColumns(metaData, table);
        extractPrimaryKeys(metaData, table);
        extractForeignKeys(metaData, table);
        extractIndexes(metaData, table);
        
        return table;
    }
}