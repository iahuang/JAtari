package core;

import java.io.IOException;
import java.io.InputStream;

import core.Registers.Flags;

// Includes built-in memory. Allocate with new CPU(size)

public class CPU {
    public Registers reg;
    public Flags flags;
    public byte[] data;
    public byte[] prg;
    public int pcl; // Program counter
    OpLoader opl;

    public CPU(int size) throws IOException {
        reg = new Registers();
        flags = new Flags();
        data = new byte[size];
        pcl = 0;
        opl = new OpLoader();
        opl.Load(getClass().getClassLoader(), "optable.txt", "cycles.txt");
    }

    public void loadPrg(byte[] prg) {
        this.prg = prg;
        pcl = 0;
    }

    public void loadRom(String romPath) {
        InputStream in = getClass().getClassLoader().getResourceAsStream(romPath);
        loadPrg(toByteArray(in));
    }

    public void loadHexdump(String hexdump) {
        prg = new byte[hexdump.length()/2];

        for (int i=0; i < hexdump.length(); i+= 2) {
            byte newByte = Byte.parseByte(hexdump.substring(i, i+2),16);
            prg[i/2] = newByte;
        }
    }

    private byte[] toByteArray(InputStream in) {
        return null;
    }

    public void runIter() {
        int opcode = consume(1);
        Instruction inst = opl.opcodes.get((Integer) opcode);
        inst.run(this);
        System.out.println(reg.A);
    }

    public void setReg(int r, byte b) {
        switch (r) {
        case 0:
            reg.A = b;
        case 1:
            reg.X = b;
        case 2:
            reg.Y = b;
        case 3:
            reg.S = b;
        case 4:
            reg.P = b;
        }
    }

    public byte[] compileRegs() {
        return new byte[] { reg.A, reg.X, reg.Y, reg.S, reg.P };
    }

    public int consume(int size) { // Parse n bytes into unsigned int and move PCL forward
        int sum = 0;
        for (int i = size-1; i >= 0; i--) {
            byte n = prg[pcl + i];
            sum = (sum << 8) | (n & 0xff);
        }
        pcl += size;
        return sum;
    }
}