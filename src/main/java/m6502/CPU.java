package m6502;

import java.io.IOException;

public class CPU extends CPUBase {
    // CPU but with specific operations implemented

    public static interface BranchCondition {
        public boolean eval();
    }

    public Ops ops = new Ops();
    public CPU (int size) throws IOException {
        super(size);
        opl = new OpLoader();
        opl.Load(this, getClass().getClassLoader(), "optable.txt", "cycles.txt");
    }
    public class Ops {
        public Operation branchOperation(String name, BranchCondition condition) {
            return new Operation(name,(arg) -> {
                if (condition.eval()) {
                    pcl+=get(arg);
                }
            });
        }

        public Operation placeholder(String name) {
            return new Operation(name,(arg) -> {
                System.out.println("[WARNING] Ran unimplemented operation "+name);
            });
        }

        public Operation compareOperation(String name, int regId) {
            return new Operation(name,(arg) -> {
                flags.carry = getReg(regId) >= get(arg);
                flags.zero = getReg(regId) == get(arg);
                flags.negative = getReg(regId) <= get(arg); // NOTE: may be < instead
            });
        }
        
        public byte get(MemRef ref) {
            return ref.get(CPU.this);
        }
        public void setNegative() {
            flags.negative = reg.A < 0; 
        }
        public void setZero() {
            flags.zero = (reg.A == 0);
        }
        public void setZN() {
            setNegative();
            setZero();
        }

        // The actual implementation for the 6502 operations
        public Operation[] ops = {
            new Operation("ADC",(arg) -> {
                int n = get(arg)+reg.A;
                flags.overflow = Math.abs(n) > 128;
                flags.carry = Math.abs(n) > 255;
                reg.A+=get(arg);
                setZN();
            }),
            new Operation("AND",(arg) -> {
                reg.A = (byte)(reg.A & get(arg));
                setZN();
            }),
            new Operation("ASL",(arg) -> {
                flags.carry = ((reg.A << 7) & 1) != 0;
                reg.A = (byte)(reg.A << 1);
                setZN();
            }),
            new Operation("BIT",(arg) -> {
                reg.A = (byte)(reg.A & get(arg));
                flags.overflow = ((reg.A << 6) & 1) != 0;
                flags.negative = ((reg.A << 7) & 1) != 0;
                setZero();
            }),
            // Branch operations
            branchOperation("BCS", ()->{return flags.carry;}),
            branchOperation("BCC", ()->{return !flags.carry;}),
            branchOperation("BEQ", ()->{return flags.zero;}),
            branchOperation("BNE", ()->{return !flags.zero;}),
            branchOperation("BMI", ()->{return flags.negative;}),
            branchOperation("BPL", ()->{return !flags.negative;}),
            branchOperation("BVS", ()->{return flags.overflow;}),
            branchOperation("BVC", ()->{return !flags.overflow;}),
            new Operation("BRK", (arg) -> {
                System.exit(0);
            }),
            new Operation("CLC", (arg) -> {
                flags.carry = false;
            }),
            new Operation("CLD", (arg) -> {
                flags.decimal = false;
            }),
            new Operation("CLI", (arg) -> {
                flags.irq = false;
            }),
            new Operation("CLV", (arg) -> {
                flags.overflow = false;
            }),
            // Compare operations
            compareOperation("CMP", 0),
            compareOperation("CPX", 1),
            compareOperation("CPY", 2)
            

        };

    }
}