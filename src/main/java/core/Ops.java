package core;
import core.Operation;

public class Ops {
    // 7 Negative
    // 6 Overflow
    // 5 Nothing
    // 4 Break Command
    // 3 Decimal Mode
    // 2 IRQ Disable
    // 1 Zero
    // 0 Carry
    public static void setNegative(CPU c) {
        c.flags.negative = c.reg.A < 0; 
    }
    public static void setZero(CPU c) {
        c.flags.zero = (c.reg.A == 0);
    }
    public static void setZN(CPU c) {
        setNegative(c);
        setZero(c);
    }
    public static Operation[] ops = {
        new Operation("ADC",(c, arg) -> {
            int n = arg.get(c)+c.reg.A;
            c.flags.overflow = Math.abs(n) > 128;
            c.flags.carry = Math.abs(n) > 255;
            c.reg.A+=arg.get(c);
            setZN(c);
        }),
        new Operation("AND",(c, arg) -> {
            c.reg.A = (byte)(c.reg.A & arg.get(c));
            setZN(c);
        }),
        new Operation("ASL",(c, arg) -> {
            c.flags.carry = ((c.reg.A << 7) & 1) != 0;
            c.reg.A = (byte)(c.reg.A << 1);
            setZN(c);
        }),
        new Operation("BCC",(c, arg) -> {
            if (!c.flags.carry) {
                c.pcl+=(int)arg.get(c);
            }
        }),
        new Operation("BCS",(c, arg) -> {
            if (c.flags.carry) {
                c.pcl+=(int)arg.get(c);
            }
        }),
        new Operation("BEQ",(c, arg) -> {
            if (c.flags.zero) {
                c.pcl+=(int)arg.get(c);
            }
        }),
        new Operation("BIT",(c, arg) -> {
            c.reg.A = (byte)(c.reg.A & arg.get(c));
            c.flags.overflow = ((c.reg.A << 6) & 1) != 0;
            c.flags.negative = ((c.reg.A << 7) & 1) != 0;
            setZero(c);
        })
    };
}