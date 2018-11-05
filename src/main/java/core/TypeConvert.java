package core;

// For some reason the Java library does not include byte parsing stuff

public class TypeConvert {
    public static int b_uint16(byte a, byte b) { // Uses little-endian (backwards)
        return ((b & 0xff) << 8) | (a & 0xff);
    }
    public static int b_uint8(byte a) {
        return a & 0xff;
    }
    public static int b_dec(byte a) {
        return 0;
    }
}