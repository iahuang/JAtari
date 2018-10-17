package core;

public class Registers {
    public byte A = 0; // Accumulator
    public byte X = 0; // X
    public byte Y = 0; // Y
    public byte S = 0; // Stack pointer
    public byte P = 0; // Processor status

    public static class Flags {
        public boolean negative; // N
        public boolean overflow; // V
        public boolean breakcmd; // B
        public boolean decimal ; // D
        public boolean irq;      // I
        public boolean zero;     // Z
        public boolean carry;    // C
        public void reset() {
            negative = false;
            overflow = false;
            breakcmd = false;
            decimal = false;
            irq = true;
            zero = false;
            carry = false;
        }
        public Flags () {
            reset();
        }
    }
}