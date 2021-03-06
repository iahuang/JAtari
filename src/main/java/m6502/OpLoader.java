package m6502;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import m6502.Operation.ArgType;

public class OpLoader {
    Map<Integer, Instruction> opcodes = new HashMap<>();
    Map<String, Operation> ops = new HashMap<>();

    public static ArgType parseAType(String s) {
        ArgType a = new ArgType();
        if (s == "A") {
            return ArgType.NULL();
        }

        a.mode = (s.contains("#") | s.contains("rel")) ? 1 : 2;
        // If the specifier contains # or rel it means that the next value
        // is meant to be taken literally (mode 1)
        // Otherwise, it is meant to be interpreted as a memory address (mode 2)
        if (s.contains("impl")) {
            a.mode = 0;
        }
        // If the operation addressing type is "implied", there is no explicit
        // memory address used and MemRef null can be used
        if (s.contains("X")) {
            a.offset = 1;
        }
        if (s.contains("Y")) {
            a.offset = 2;
        }

        if (s.contains("ind")) {
            a.indirect = true;
        }
        if (s.contains(",")) {
            a.indexed = true;
        }
        if (s.contains(",ind")) {
            a.offsetFirst = true;
        }
        return a;
    }

    public void Load(CPU c, ClassLoader cl, String codeTablePath, String infoTablePath) throws IOException {
        for (Operation op : c.ops.ops) {
            ops.put(op.name, op);
        }

        InputStream in = cl.getResourceAsStream(codeTablePath);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;

        int opcode = 0;
        while ((line = reader.readLine()) != null) {
            if (!line.equals("null")) {
                String[] splitLine = line.split(" ");

                String opname = splitLine[0];
                Operation op = ops.get(opname);
                ArgType atype = parseAType(splitLine[1]);

                Instruction inst = new Instruction(opcode, op, atype);
                opcodes.put((Integer) opcode, inst);
            }

            opcode++;
        }
        // Parse cycle counts
        in = cl.getResourceAsStream(infoTablePath);
        reader = new BufferedReader(new InputStreamReader(in));
        while ((line = reader.readLine()) != null) {
            String[] splitLine = line.split(" ");
            Integer code = Integer.parseInt(splitLine[0], 16);
            Integer cycleCount = Integer.parseInt(splitLine[1]);
            Integer codeSize = Integer.parseInt(splitLine[1]);
            String extraCycle = splitLine[3];
            opcodes.get(code).cycles = cycleCount;
            opcodes.get(code).atype.addSize = codeSize - 1;

            // Dynamic cycle count rules
            switch (extraCycle) {
            case "pageboundary":
                opcodes.get(code).plusPage = true;

            case "branch":
                opcodes.get(code).branching = true;
            }
        }
    }
}