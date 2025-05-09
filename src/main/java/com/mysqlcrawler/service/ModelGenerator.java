package com.mysqlcrawler.service;

import com.mysqlcrawler.config.Config;
import com.mysqlcrawler.config.ConfigLoader;
import com.mysqlcrawler.metadata.ColumnMetadata;
import com.mysqlcrawler.metadata.DatabaseMetadata;
import com.mysqlcrawler.metadata.ForeignKeyMetadata;
import com.mysqlcrawler.metadata.TableMetadata;
import com.squareup.javapoet.*;
import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ModelGenerator {
    private final Config config;

    public ModelGenerator() throws Exception {
       
        this.config = ConfigLoader.loadConfig();
    }

    public void generateModels(DatabaseMetadata metadata ) throws IOException {
        for (TableMetadata table : metadata.getTables()) {
            generateModelClass(table);
        }
    }

    private void generateModelClass(TableMetadata table) throws IOException {
        String className = capitalize(sanitizeJavaIdentifier(table.getName()));
        
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC);

        // Add fields for columns
        for (ColumnMetadata column : table.getColumns()) {
            String fieldName = sanitizeJavaIdentifier(column.getName());
            FieldSpec field = FieldSpec.builder(
                    getJavaType(column.getType(), column.getSize(), column.isUnsigned()),
                    fieldName,
                    Modifier.PRIVATE
            ).build();
            classBuilder.addField(field);

            // Add getter and setter methods for each column
            classBuilder.addMethod(createGetter(fieldName, getJavaType(column.getType(), column.getSize(), column.isUnsigned())));
            classBuilder.addMethod(createSetter(fieldName, getJavaType(column.getType(), column.getSize(), column.isUnsigned())));
        }

        // Handle relationships (foreign keys)
        addRelationships(classBuilder, table);

        // Generate the Java file
        JavaFile javaFile = JavaFile.builder(config.getPackageName(), classBuilder.build())
                .build();

        javaFile.writeTo(new File(config.getOutputDir()));
    }

    private void addRelationships(TypeSpec.Builder classBuilder, TableMetadata table) {
    // Handle one-to-many and many-to-one relationships
    for (ForeignKeyMetadata fk : table.getForeignKeys()) {

        String refClass = capitalize(fk.getReferencedTableName());
        TypeName refType = ClassName.get(config.getPackageName(), refClass);


        String fieldName = sanitizeJavaIdentifier(fk.getColumnName());

    
        fieldName = fieldName + "_Ref";

        // Add field and methods
        FieldSpec field = FieldSpec.builder(refType, fieldName, Modifier.PRIVATE).build();
        classBuilder.addField(field);
        classBuilder.addMethod(createGetter(fieldName, refType));
        classBuilder.addMethod(createSetter(fieldName, refType));

    }

    // Handle many-to-many relationships (for any referenced tables)
    detectAndAddManyToMany(classBuilder, table);
}

    private void detectAndAddManyToMany(TypeSpec.Builder classBuilder, TableMetadata table) {
    // Check if the table is a valid join table
    if (!isJoinTable(table)) {
        return;
    }


    List<ForeignKeyMetadata> fks = table.getForeignKeys();
    if (fks.size() != 2) {
        return; 
    }

    Set<String> referencedTables = fks.stream()
            .map(ForeignKeyMetadata::getReferencedTableName)
            .map(this::capitalize)
            .collect(Collectors.toSet());

    if (referencedTables.size() != 2) {
        return; 
    }

    List<String> tableNames = new ArrayList<>(referencedTables);
    // Add List fields and methods for each related table
    for (String tableName : tableNames) {
        ParameterizedTypeName listType = ParameterizedTypeName.get(
                ClassName.get("java.util", "List"),
                ClassName.get(config.getPackageName(), tableName)
        );

        String fieldName = tableName + "List";
        classBuilder.addField(FieldSpec.builder(listType, fieldName, Modifier.PRIVATE)
                .initializer("new $T<>()", ClassName.get("java.util", "ArrayList"))
                .build());

        // Add getter and setter methods
        classBuilder.addMethod(createListGetter(fieldName, listType));
        classBuilder.addMethod(createListSetter(fieldName, listType));
    }
}
    
    private boolean isJoinTable(TableMetadata table) {
        // A join table typically has exactly two foreign keys and minimal other columns
        return table.getForeignKeys().size() == 2 &&
               table.getColumns().size() <= 3 &&
               table.getPrimaryKeys().size() >= 1;
    }

    private MethodSpec createGetter(String fieldName, TypeName type) {
        return MethodSpec.methodBuilder("get" + capitalize(fieldName))
                .addModifiers(Modifier.PUBLIC)
                .returns(type)
                .addStatement("return " + fieldName)
                .build();
    }

    private MethodSpec createSetter(String fieldName, TypeName type) {
        return MethodSpec.methodBuilder("set" + capitalize(fieldName))
                .addModifiers(Modifier.PUBLIC)
                .addParameter(type, fieldName)
                .addStatement("this." + fieldName + " = " + fieldName)
                .build();
    }

    private MethodSpec createListGetter(String fieldName, TypeName type) {
        return MethodSpec.methodBuilder("get" + capitalize(fieldName))
                .addModifiers(Modifier.PUBLIC)
                .returns(type)
                .addStatement("return " + fieldName)
                .build();
    }

    private MethodSpec createListSetter(String fieldName, TypeName type) {
        return MethodSpec.methodBuilder("set" + capitalize(fieldName))
                .addModifiers(Modifier.PUBLIC)
                .addParameter(type, fieldName)
                .addStatement("this." + fieldName + " = " + fieldName)
                .build();
    }

    private TypeName getJavaType(String sqlType, int size, boolean isUnsigned) {
        sqlType =sqlType.split(sqlType.contains(" ") ? " " : "")[0];
        switch (sqlType.toUpperCase()) {
            case "TINYINT":
                return isUnsigned ? ClassName.get(Short.class) : ClassName.get(Byte.class);
            case "SMALLINT":
                return isUnsigned ? ClassName.get(Integer.class) : ClassName.get(Short.class);
            case "MEDIUMINT":
            case "INT":
            case "INTEGER":
                return isUnsigned ? ClassName.get(Long.class) : ClassName.get(Integer.class);
            case "BIGINT":
                return isUnsigned ? ClassName.get("java.math", "BigInteger") : ClassName.get(Long.class);

            case "DECIMAL":
            case "NUMERIC":
                return ClassName.get("java.math", "BigDecimal");

            case "FLOAT":
                return ClassName.get(Float.class);
            case "DOUBLE":
            case "REAL":
                return ClassName.get(Double.class);

            case "CHAR":
            case "VARCHAR":
            case "TEXT":
            case "TINYTEXT":
            case "MEDIUMTEXT":
            case "LONGTEXT":
            case "ENUM":
            case "SET":
                return ClassName.get(String.class);

            case "BIT":
            case "BOOLEAN":
                return ClassName.get(Boolean.class);

            case "DATE":
                return ClassName.get("java.time", "LocalDate");
            case "TIME":
                return ClassName.get("java.time", "LocalTime");
            case "DATETIME":
            case "TIMESTAMP":
                return ClassName.get("java.time", "LocalDateTime");

            case "BINARY":
            case "VARBINARY":
            case "BLOB":
            case "TINYBLOB":
            case "MEDIUMBLOB":
            case "LONGBLOB":
                return ArrayTypeName.of(TypeName.BYTE); // byte[]

            default:
                return ClassName.get(String.class);
        }
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private String sanitizeJavaIdentifier(String name) {
        return name.replaceAll("[^a-zA-Z0-9_$]", "").replaceAll("^\\d+", "");
    }
}


