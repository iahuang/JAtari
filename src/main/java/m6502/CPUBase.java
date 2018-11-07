package m6502;

import java.io.IOException;
import java.io.InputStream;

import m6502.Registers.Flags;

// CPU classes include built-in memory. Allocate with new CPU(size)

public class CPUBase { // 6502 CPU with no instruction definitions
    public Registers reg;
    public Flags flags;
    public byte[] data;
    public byte[] prg;
    public int pcl; // Program counter
    OpLoader opl;
    boolean crossedPage = false; // Whether page boundary was crossed in memory addressing
    int cycle = 0; // Overall cycle count

    public CPUBase (int size) throws IOException {
        /* Initialize a CPU with [size] bytes of RAM for the CPU to access. Max 65536
        In CPUBase, OpLoader is never initialzed, meaning that no operations will be defined.
        Any attempt to run an instruction will result in a NullPointerException
        */
        reg = new Registers();
        flags = new Flags();
        data = new byte[size];
        pcl = 0;    
    }

    public void loadPrg(byte[] prg) {
        // Load a bytearray as a ROM
        this.prg = prg;
        pcl = 0;
    }

    public void loadRom(String romPath) throws IOException {
        // Load a ROM file from the disk
        InputStream in = getClass().getClassLoader().getResourceAsStream(romPath);
        loadPrg(new byte[in.available()]);
    }

    public void loadHexdump(String hexdump) {
        // Load a hexdump as a ROM
        prg = new byte[hexdump.length()/2];

        for (int i=0; i < hexdump.length(); i+= 2) {
            byte newByte = Byte.parseByte(hexdump.substring(i, i+2),16);
            prg[i/2] = newByte;
        }
    }

    public void runIter() {
        // Execute the next instruction in ROM
        int opcode = consume(1);
        Instruction inst = opl.opcodes.get((Integer) opcode);
        crossedPage = false;
        inst.run(this);


    }

    public void setReg(int r, byte b) {
        // Set a register by index
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

    public int consume(int size) {
        // Parse n bytes into unsigned int and move PCL forward
        int sum = 0;
        for (int i = size-1; i >= 0; i--) {
            byte n = prg[pcl + i];
            sum = (sum << 8) | (n & 0xff);
        }
        pcl += size;
        return sum;
    }
    
}

