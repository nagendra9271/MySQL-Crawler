package com.mysqlcrawler.model;

import java.lang.String;

public class Addresses {
  private String id;

  private String student_id;

  private String street;

  private String city;

  private String zip_code;

  private Students student_id_Ref;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getStudent_id() {
    return student_id;
  }

  public void setStudent_id(String student_id) {
    this.student_id = student_id;
  }

  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getZip_code() {
    return zip_code;
  }

  public void setZip_code(String zip_code) {
    this.zip_code = zip_code;
  }

  public Students getStudent_id_Ref() {
    return student_id_Ref;
  }

  public void setStudent_id_Ref(Students student_id_Ref) {
    this.student_id_Ref = student_id_Ref;
  }
}
