package cn.fb.flutter_social.handler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import top.zibin.luban.InputStreamProvider;

public class MyInputStream implements InputStreamProvider {

    byte[] bytes;

    public MyInputStream(byte[] bytes) {
        this.bytes = bytes;
    }

    @Override
    public InputStream open() throws IOException {

        return new ByteArrayInputStream(bytes);
    }

    @Override
    public String getPath() {
        return null;
    }
}
