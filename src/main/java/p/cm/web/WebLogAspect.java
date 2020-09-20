package p.cm.web;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * web日志
 */
@Slf4j
@Aspect
@Component
public class WebLogAspect {

    /**
     * 定义切入点，切入点为com.xx.controller下的所有函数
     */
    @Pointcut("execution(public * com.xx.controller..*.*(..))")
    public void webLog() {
    }

    /**
     * 前置通知：在连接点之前执行
     * 记录request内容
     */
    @Before("webLog()")
    public void deBefore(JoinPoint joinPoint) throws Throwable {
        /**
         * JoinPoint可以获得通知的签名信息，如目标方法名、目标方法参数信息等
         * RequestContextHolder获取请求信息，session信息
         */
        //接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        //记录下请求内容
        StringBuilder sb = new StringBuilder();
        String indent = StringUtils.repeat(' ', 4);
        String s =
                sb.append(String.format("\nurl: %s", request.getRequestURL().toString())).append("\n")
                        .append(indent).append(String.format("server: %s", request.getServerName())).append("\n")
                        .append(indent).append(String.format("http_method: %s", request.getMethod())).append("\n")
                        .append(indent).append(String.format("ip: %s", request.getRemoteAddr())).append("\n")
                        .append(indent).append(
                                String.format("class_method: %s.%s",
                                        joinPoint.getSignature().getDeclaringTypeName(),
                                        joinPoint.getSignature().getName())).append("\n")//aop代理类.代理的方法
                        .append(indent).append(String.format("args: %s", Arrays.toString(joinPoint.getArgs())))//目标方法的参数信息
                        .toString();
        log.debug(s);
    }

    /**
     * @param ret 代理方法的返回值
     * @throws Throwable
     */
    @AfterReturning(returning = "ret", pointcut = "webLog()")
    public void doAfterReturning(Object ret) throws Throwable {
        //处理完请求，返回内容
        log.debug(String.format("response: %s", ret));
    }

}
