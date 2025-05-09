package com.mysqlcrawler.model;

import java.lang.String;
import java.util.ArrayList;
import java.util.List;

public class Enrollments {
  private String student_id;

  private String course_id;

  private String enrolled_on;

  private Courses course_id_Ref;

  private Students student_id_Ref;

  private List<Students> StudentsList = new ArrayList<>();

  private List<Courses> CoursesList = new ArrayList<>();

  public String getStudent_id() {
    return student_id;
  }

  public void setStudent_id(String student_id) {
    this.student_id = student_id;
  }

  public String getCourse_id() {
    return course_id;
  }

  public void setCourse_id(String course_id) {
    this.course_id = course_id;
  }

  public String getEnrolled_on() {
    return enrolled_on;
  }

  public void setEnrolled_on(String enrolled_on) {
    this.enrolled_on = enrolled_on;
  }

  public Courses getCourse_id_Ref() {
    return course_id_Ref;
  }

  public void setCourse_id_Ref(Courses course_id_Ref) {
    this.course_id_Ref = course_id_Ref;
  }

  public Students getStudent_id_Ref() {
    return student_id_Ref;
  }

  public void setStudent_id_Ref(Students student_id_Ref) {
    this.student_id_Ref = student_id_Ref;
  }

  public List<Students> getStudentsList() {
    return StudentsList;
  }

  public void setStudentsList(List<Students> StudentsList) {
    this.StudentsList = StudentsList;
  }

  public List<Courses> getCoursesList() {
    return CoursesList;
  }

  public void setCoursesList(List<Courses> CoursesList) {
    this.CoursesList = CoursesList;
  }
}
