import core.CPU;
import core.Operation;

public class Main {
    public static void main(String[] args) throws Exception{
        CPU cpu = new CPU(128);
        cpu.loadHexdump("6910");
    
        cpu.runIter();
    }
}