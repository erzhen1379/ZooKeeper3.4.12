package com.github.demo.seri;

import org.apache.jute.*;
import org.apache.zookeeper.server.ByteBufferInputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 使用jute进行序列化
 * 1实现Record接口
 * 2.构建一个序列化器BinaryOutputArchive
 * 3.序列化 ：调用实体类serialize方法，将对象序列化到指定tag中
 * 4.调用实体类deserialize，从指定的tag中反序列化出数据内容
 */
public class MockReqHeader implements Record {
    private long sessionId;
    private String type;

    public MockReqHeader() {
    }

    public MockReqHeader(long sessionId, String type) {
        this.sessionId = sessionId;
        this.type = type;
    }

    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    //tag用于序列化和反序列化器标识对象自己的标记
    @Override
    public void serialize(OutputArchive archive, String tag) throws IOException {
        archive.startRecord(this, tag);
        archive.writeLong(sessionId, "sessionId");
        archive.writeString(type, "type");
        archive.endRecord(this, tag);


    }

    @Override
    public void deserialize(InputArchive archive, String tag) throws IOException {
        archive.startRecord(tag);
        sessionId = archive.readLong("sessionId");
        type = archive.readString("type");
        archive.endRecord(tag);
    }

    @Override
    public String toString() {
        return "MockReqHeader{" +
                "sessionId=" + sessionId +
                ", type='" + type + '\'' +
                '}';
    }

    public static void main(String[] args) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BinaryOutputArchive boa = BinaryOutputArchive.getArchive(baos);
        new MockReqHeader(1232223, "ping").serialize(boa, "header");
        //这里通常是通过TCP网络传输对象
        ByteBuffer bb = ByteBuffer.wrap(baos.toByteArray());
        //开始反序列化
        ByteBufferInputStream bbis = new ByteBufferInputStream(bb);
        BinaryInputArchive bbia = BinaryInputArchive.getArchive(bbis);
        MockReqHeader header2 = new MockReqHeader();
        header2.deserialize(bbia,"header");
        System.out.println(header2.toString());

        bbis.close();
        baos.close();
    }
}
