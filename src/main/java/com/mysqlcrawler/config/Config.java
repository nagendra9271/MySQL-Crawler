package com.mysqlcrawler.config;

import lombok.Data;




@Data
public class Config {
    private String driverClassName;
    private String url;
    private String username;
    private String password;
    private String catalog;
    private String packageName;
    private String outputDir;
    // Getters and Setters
    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

}


