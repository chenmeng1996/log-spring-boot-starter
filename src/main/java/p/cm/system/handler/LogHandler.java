package p.cm.system.handler;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import p.cm.system.LogAudit;

import java.lang.reflect.Method;

@Component
public interface LogHandler {
    void handle(String operation, String information);
}
