package com.github.demo.seri;

import org.apache.jute.BinaryInputArchive;
import org.apache.jute.BinaryOutputArchive;
import org.junit.Test;

import java.io.*;

/**
 * 测试序列化
 */
public class SeriJuteTest implements Serializable {
    @Test
    public void testJute() throws IOException {
        Student student = new Student("dave", 8, 1);
        //输出流
        ObjectOutputStream oos;
        String path = "D:\\jute.txt";
        InputStream inputStream;
        OutputStream outputStream;
        System.out.println("序列化操作");
        outputStream = new FileOutputStream(new File(path));
        BinaryOutputArchive binaryOutputArchive = BinaryOutputArchive.getArchive(outputStream);
        binaryOutputArchive.writeString(student.getUserName(), "userName");
        binaryOutputArchive.writeInt(student.getAge(), "age");
        binaryOutputArchive.writeLong(student.getGrade(), "grade");
        outputStream.flush();
        outputStream.close();

        System.out.println("反序列操作");
        inputStream = new FileInputStream(new File(path));
        BinaryInputArchive binaryInputArchive = BinaryInputArchive.getArchive(inputStream);
        String userName = binaryInputArchive.readString("userName");
        int age = binaryInputArchive.readInt("age");
        long grade = binaryInputArchive.readLong("grade");
        System.out.println("userName=" + userName + ";age=" + age + ":grade" + grade);


    }
}
