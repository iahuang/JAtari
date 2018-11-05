package core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import core.Operation.ArgType;

public class OpLoader {
    Map<Integer, Instruction> opcodes = new HashMap<>();
    Map<String, Operation> ops = new HashMap<>();

    public static ArgType parseAType(String s) {
        ArgType a = new ArgType();
        if (s == "A") {
            return ArgType.NULL();
        }
        a.addSize = (s.contains("abs") | s.contains("rel")) ? 2 : 1;
        a.mode = (s.contains("#") | s.contains("rel")) ? 1 : 2;
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

    public void Load(CPU c, ClassLoader cl, String codeTablePath, String cycleTablePath) throws IOException {
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
        in = cl.getResourceAsStream(cycleTablePath);
        reader = new BufferedReader(new InputStreamReader(in));
        while ((line = reader.readLine()) != null) {
            String[] splitLine = line.split(" ");
            Integer code = Integer.parseInt(splitLine[0], 16);
            Integer cycleCount = Integer.parseInt(splitLine[1]);
            String extraCycle = splitLine[2];
            opcodes.get(code).cycles = cycleCount;
            
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