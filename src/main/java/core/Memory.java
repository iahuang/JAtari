package core;

public class Memory {
    public byte[] data;

    public Memory (int size) {
        data = new byte[size];
    }
    public byte absolute(int address) {
        return data[address];
    }
    public byte zeroPage (byte at) {
        return data[at];
    }
}