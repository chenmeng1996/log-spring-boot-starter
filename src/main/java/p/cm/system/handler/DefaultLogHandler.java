package p.cm.system.handler;

import org.aspectj.lang.ProceedingJoinPoint;

public class DefaultLogHandler implements LogHandler {
    @Override
    public void handle(String operation, String information) {
        System.out.println("operation=" + operation);
        System.out.println("information=" + information);
    }
}
