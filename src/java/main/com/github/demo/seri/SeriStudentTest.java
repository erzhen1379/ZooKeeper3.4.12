package com.github.demo.seri;

import org.junit.Test;

import java.io.*;

/**
 * 测试序列化
 */
public class SeriStudentTest implements Serializable {
    @Test
    public void test() {
        Student student = new Student("dave", 8, 1);
        //输出流
        ObjectOutputStream oos;
        String path = "D:\\test.txt";
        try {
            oos = new ObjectOutputStream(new FileOutputStream(new File(path)));
            oos.writeObject(student);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("java Serializable");
        ObjectInputStream ois;
        try {
            ois = new ObjectInputStream(new FileInputStream(new File(path)));
            Student obj;
            obj = (Student) ois.readObject();
            System.out.println(obj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
