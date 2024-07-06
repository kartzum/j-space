package io.rdlab.cons.ms;

public class MultiplierHandler implements TinyServer.Handler {
    @Override
    public byte[] handle(byte[] data) {
        if (data != null) {
            return new byte[]{(byte) (data[0] * 2)};
        }
        return new byte[0];
    }
}
