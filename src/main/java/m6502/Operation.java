package m6502;

public class Operation {
    public Op runner;
    public String name;

    public static interface Op {
        public void run(MemRef arg);
    }
    public Operation (String name, Op runner) {
        this.runner = runner;
        this.name = name;
    }
    public static class ArgType {
        public int mode,addSize,offset;
        public boolean indirect,indexed,offsetFirst;

        public static ArgType NULL() {
            ArgType a = new ArgType();
            return a;
        }
    }

}
