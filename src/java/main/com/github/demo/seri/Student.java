package com.github.demo.seri;

import java.io.Serializable;

/**
 * 测试序列化
 */
public class Student implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userName;
    private int age;
    private long grade;

    public Student(String userName, int age, long grade) {
        this.userName = userName;
        this.age = age;
        this.grade = grade;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public long getGrade() {
        return grade;
    }

    public void setGrade(long grade) {
        this.grade = grade;
    }

    @Override
    public String toString() {
        return "Student{" +
                "userName='" + userName + '\'' +
                ", age=" + age +
                ", grade=" + grade +
                '}';
    }
}
