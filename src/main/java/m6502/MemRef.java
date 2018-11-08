package m6502;
import static m6502.TypeConvert.*;

public class MemRef { // I decided "screw polymorphism" we're doing it this way
    public int mode, index, offset;
    public boolean indirect, indexed, offsetFirst;

    public MemRef(int mode, int index, int offset, boolean indirect, boolean indexed, boolean offsetFirst) {
        /*
         * Index: The value associated with [mode]
         * Offset: The register ID to offset with
         * 
         * Modes:
         * 0 - Null
         * 1 - Immediate / Relative
         * 2 - Addressed: abs, zpg, zpi etc.
         * 3 - Register
         */

        this.mode = mode;
        this.index = index;
        this.offset = offset;
        this.indirect = indirect;
        this.offsetFirst = offsetFirst;
        this.indexed = indexed;
    }

    public static MemRef getRegister(int r) {
        return new MemRef(3, r, 0, false, false, false);
    }
    public int getPointer(CPU c) {
        byte[] regs = c.compileRegs();
        int pointer = index;
        
        if (indexed && offsetFirst) {
            pointer+=b_uint8(regs[offset]);

        }
        if (indirect) {
            pointer = b_uint16(c.data[pointer],c.data[pointer+1]);
        }
        if (indexed && !offsetFirst) {
            int pointerFirst = pointer;
            pointer+=b_uint8(regs[offset]);
            if (pointerFirst / 256 != pointer / 256) {
                c.crossedPage = true;
            }
        }

        return pointer;
    }
    public byte get(CPU c) {
        switch (mode) {
            case 0:
            return 0;
            case 1:
            return (byte)index;
            case 2:
            int pointer = getPointer(c);
            return c.data[pointer];
            case 3:
            return c.compileRegs()[index];
        }
        return 0;
    }
    public void set(CPU c, byte n) {
        switch (mode) {
            case 2:
            int pointer = getPointer(c);
            c.data[pointer] = n;
            case 3:
            c.setReg(index, n);
        }
    }
}