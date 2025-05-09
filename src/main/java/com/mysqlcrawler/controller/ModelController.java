package com.mysqlcrawler.controller;


import com.mysqlcrawler.metadata.DatabaseMetadata;
import com.mysqlcrawler.service.SchemaCrawlerService;
import com.mysqlcrawler.service.ModelGenerator;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.sql.SQLException;

@RestController
@RequestMapping("/api/models")
public class ModelController {
    private final SchemaCrawlerService schemaCrawler;
    private final ModelGenerator modelGenerator;
    
    public ModelController() throws Exception {
        this.schemaCrawler = new SchemaCrawlerService();
        this.modelGenerator = new ModelGenerator();
    }
    
    @GetMapping("/metadata")
    public ResponseEntity<DatabaseMetadata> getMetadata() throws SQLException {
        DatabaseMetadata metadata = schemaCrawler.extractDatabaseMetadata();
        return ResponseEntity.ok(metadata);
    }
    
    @PostMapping("/generate")
    public ResponseEntity<String> generateModels() throws Exception {
        DatabaseMetadata metadata = schemaCrawler.extractDatabaseMetadata();
        modelGenerator.generateModels(metadata);
        return ResponseEntity.ok("Models generated successfully");
    }
}













// package com.mysqlcrawler.controller;

// import com.mysqlcrawler.service.SchemaCrawlerService;
// import com.mysqlcrawler.config.DBConfig;
// import com.mysqlcrawler.config.DBConfigLoader;
// import com.mysqlcrawler.metadata.DatabaseMetadata;
// import com.mysqlcrawler.service.ModelGenerator;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.util.HashMap;
// import java.util.*;

// @RestController
// @RequestMapping("/api/generator")
// public class ModelController {

//     private final ModelGenerator modelGenerator;
//     private final SchemaCrawlerService schemaCrawlerService;
//     private final DBConfig config;
//     public ModelController() throws Exception {
//         this.modelGenerator = new ModelGenerator();
//         this.schemaCrawlerService = new SchemaCrawlerService();
//         this.config = DBConfigLoader.loadConfig();
//     }
    
//     @GetMapping("/metadata")
//     public ResponseEntity<DatabaseMetadata> getDatabaseMetadata() {
//         try {
//             DatabaseMetadata metadata = schemaCrawlerService.extractDatabaseMetadata();
//             return ResponseEntity.ok(metadata);
//         } catch (Exception e) {
//             e.printStackTrace();
//             return ResponseEntity.internalServerError().build();
//         }
//     }
    
    
    
//     @PostMapping("/generate-models")
//     public List<String> generateModels() throws Exception {
//         String packageName = config.getPackageName();
//         String outputDir = config.getOutputDir();
        
//         // Ensure the package name for generated models is com.mysqlcrawler.model
//         if (packageName == null || !packageName.equals("com.mysqlcrawler.model")) {
//             packageName = "com.mysqlcrawler.model";
//         }
        
//         if (outputDir == null) {
//             outputDir = "src/main/java/com/mysqlcrawler/model";
//         }
        
//         DatabaseMetadata metadata = schemaCrawlerService.extractDatabaseMetadata();
//         modelGenerator.generateModels(metadata, packageName, outputDir);
//         return modelGenerator.getGeneratedClasses();
//     }
// }














// // package com.mysqlcrawler.controller;
// // import com.mysqlcrawler.model.dto.DatabaseMetadata;
// // import com.mysqlcrawler.model.dto.TableMetadata;
// // import com.mysqlcrawler.service.*;
// // import org.springframework.beans.factory.annotation.Autowired;

// // import org.springframework.http.HttpStatus;
// // import org.springframework.http.ResponseEntity;
// // import org.springframework.web.bind.annotation.*;
// // import java.sql.SQLException;
// // import java.util.*;
// // // ModelGeneratorController.java
// // @RestController
// // @RequestMapping("/api/models")
// // public class ModelController {

// //     @Autowired
// //     private SchemaCrawlerService metadataService;
    
// //     @Autowired
// //     private ModelGenerationService modelGenerationService;

// //     @GetMapping("/generate")
// //     public ResponseEntity<Map<String, String>> generateModels(
// //             @RequestParam(defaultValue = "") String schemaName) {
// //         try {
// //             DatabaseMetadata metadata = metadataService.extractDatabaseMetadata();
// //             Map<String, String> models = new LinkedHashMap<>();
            
// //             for (TableMetadata table : metadata.getTables()) {
// //                 String className = modelGenerationService.toCamelCase(table.getName(), true);
// //                 String classContent = modelGenerationService.generateModelClass(table, metadata);
// //                 models.put(className, classContent);
// //             }
            
// //             return ResponseEntity.ok(models);
// //         } catch (SQLException e) {
// //             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
// //         }
// //     }

// //     @GetMapping("/generate/{tableName}")
// //     public ResponseEntity<String> generateModel(
// //             @RequestParam(defaultValue = "") String schemaName,
// //             @PathVariable String tableName) {
// //         try {
// //             DatabaseMetadata metadata = metadataService.extractDatabaseMetadata();
// //             Optional<TableMetadata> table = metadata.getTables().stream()
// //                     .filter(t -> t.getName().equalsIgnoreCase(tableName))
// //                     .findFirst();
            
// //             if (table.isPresent()) {
// //                 String classContent = modelGenerationService.generateModelClass(table.get(), metadata);
// //                 return ResponseEntity.ok(classContent);
// //             } else {
// //                 return ResponseEntity.notFound().build();
// //             }
// //         } catch (SQLException e) {
// //             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
// //         }
// //     }
// // }