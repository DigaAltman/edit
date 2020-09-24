package cn.bbzzzs.kafka.echo;

import java.nio.ByteBuffer;

public class Main {


    public static void main(String[] args) throws Exception {
        Server server = new Server();
        server.open();
    }

    public static String handleMessage(ByteBuffer bb) {
        StringBuilder sb = new StringBuilder();
        bb.flip();
        int start = 0;
        byte[] bytes = new byte[bb.limit()];
        while (start < bb.limit()) {
            bytes[start++] = bb.get();
        }
        return new String(bytes);
    }
}
