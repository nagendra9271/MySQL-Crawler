package com.mysqlcrawler.controller;
import com.mysqlcrawler.metadata.DatabaseMetadata;
import com.mysqlcrawler.metadata.TableMetadata;
import com.mysqlcrawler.service.SchemaCrawlerService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.sql.SQLException;
import java.util.*;
@RestController
@RequestMapping("/api/schema")
public class SchemaController {

    private final SchemaCrawlerService metadataService;

    public SchemaController() throws Exception {
        // Initialize the SchemaCrawlerService to interact with DB
        this.metadataService = new SchemaCrawlerService();
    }
    @GetMapping("/metadata")
    public ResponseEntity<DatabaseMetadata> getDatabaseMetadata() {
        try {
            DatabaseMetadata metadata = metadataService.extractDatabaseMetadata();
            return ResponseEntity.ok(metadata);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/tables")
    public ResponseEntity<List<String>> getTables(
            @RequestParam(defaultValue = "") String schemaName) {
        try {
            List<String> tables = metadataService.getTableNames();
            return ResponseEntity.ok(tables);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/tables/{tableName}")
    public ResponseEntity<TableMetadata> getTableDetails(
            @RequestParam(defaultValue = "") String schemaName,
            @PathVariable String tableName) {
        try {
            DatabaseMetadata metadata = metadataService.extractDatabaseMetadata();
            Optional<TableMetadata> table = metadata.getTables().stream()
                    .filter(t -> t.getName().equalsIgnoreCase(tableName))
                    .findFirst();
            return table.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}