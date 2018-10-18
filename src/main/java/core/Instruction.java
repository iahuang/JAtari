package core;

import core.Operation.ArgType;

public class Instruction {
    ArgType atype;
    Operation op;
    int opcode;
    int cycles;
    boolean plusPage;
    boolean branching;

    public Instruction(int opcode, Operation op, ArgType atype) {
        this.atype = atype;
        this.op = op;
        this.opcode = opcode;
    }

    MemRef buildRef(CPUBase c) {
        return new MemRef(atype.mode, c.consume(atype.addSize), atype.offset, atype.indirect, atype.indexed,
                atype.offsetFirst);
    }

    public void run(CPUBase c) {
        MemRef arg = buildRef(c);
        op.runner.run(arg);
        cycles+=cycles;
        if (plusPage) {

        }
    }
}