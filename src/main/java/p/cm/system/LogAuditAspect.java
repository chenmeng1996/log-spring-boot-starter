package p.cm.system;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import p.cm.system.handler.LogHandler;
import p.cm.util.SpringContext;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 记录操作日志到数据库
 */
@Slf4j
@Aspect
@Component
public class LogAuditAspect {

    @Autowired(required = false)
    private HttpServletRequest request;

    @Autowired
    private SpelParseService spelParseService;

    @Pointcut("@annotation(p.cm.system.LogAudit)")
    public void point() {}

    @Around("point()")
    public Object around(ProceedingJoinPoint joinPoint) {
        Date begin = new Date();
        if (log.isDebugEnabled()) {
            log.debug("开始计时: {}  URI: {}", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(begin), request.getRequestURI());
        }
        Object res = null;
        try {
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            Method method = methodSignature.getMethod();
            LogAudit logAudit = method.getAnnotation(LogAudit.class);
            String operation = logAudit.operation();
            String spel = logAudit.information();
            String information = parseSpel(spel, joinPoint);

            LogHandler logHandler = SpringContext.getBean(logAudit.handler());
            logHandler.handle(operation, information);

            res = joinPoint.proceed();
            return res;
        } catch (Throwable throwable) {
            log.error(throwable.getMessage(), throwable);
        } finally {
            Date end = new Date();
        }
        return res;
    }

    private String parseSpel(String spel, ProceedingJoinPoint joinPoint) {
        if (!spel.contains("#")) {
            return spel;
        }
        return generateBySpEL(spel, joinPoint);
    }

    public String generateBySpEL(String spel, ProceedingJoinPoint joinPoint) {
        // spring的表达式上下文对象
        StandardEvaluationContext context = new StandardEvaluationContext();

        // 通过joinPoint获取被注解方法
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();

        return spelParseService.parse(spel, method, String.class, joinPoint.getArgs());
    }
}
