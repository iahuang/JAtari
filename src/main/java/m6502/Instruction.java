package m6502;

import m6502.Operation.ArgType;

public class Instruction {
    ArgType atype;
    Operation op;
    int opcode;
    boolean plusPage;
    boolean branching;
    int cycles;

    public Instruction(int opcode, Operation op, ArgType atype) {
        this.atype = atype;
        this.op = op;
        this.opcode = opcode;
    }

    MemRef buildRef(CPUBase c) {
        return new MemRef(atype.mode, c.consume(atype.addSize), atype.offset, atype.indirect, atype.indexed,
                atype.offsetFirst);
    }

    int getPage(int n) {
        return n/256; // Integer division truncates decimals
    }

    public void run(CPUBase c) {
        int newCycles = 0; // Cycles run this instruction

        MemRef arg = buildRef(c);
        int pastPcl = c.pcl;
        op.runner.run(arg);
        if (branching) { 
            // Add +1 cycle if branch operation succeeded, another +! if the PCL changed pages
            newCycles+=((pastPcl != c.pcl)? 1 : 0) + ((getPage(pastPcl) != getPage(c.pcl))? 1 : 0);   
        }

        newCycles+=cycles; // Add the number of cycles it has to take to run the instruction

        if (plusPage && c.crossedPage) {
            newCycles+=1; // Add +1 cycle if any memory addresses crossed a page boundary
        }
        
    }
}