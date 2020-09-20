package p.cm.system;

import lombok.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;

@Service
public class SpelParseService implements BeanFactoryAware {

    /**
     * 用于SpEL表达式解析.
     */
    @Autowired
    private SpelExpressionParser spelExpressionParser;

    /**
     * 用于获取方法参数定义名字.
     */
    private final DefaultParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

 
    private BeanFactory beanFactory;
 
    public <T> T parse(String expression, Method method, Class<T> cls, Object... args) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        // 将Spring 的bean上下文放入 Spel 解析的上线文中
        context.setBeanResolver(new BeanFactoryResolver(beanFactory));
        // 类似于 @Cacheable 中的 root 对象、method 对象，这里我们也默认把 method、args 变量写入当前上下文中
        context.setVariable("method", method);
        context.setVariable("args", args);

        // 使用spring的DefaultParameterNameDiscoverer获取方法形参名数组
        String[] paramNames = nameDiscoverer.getParameterNames(method);
        // 给上下文赋值
        for(int i = 0 ; i < args.length ; i++) {
            context.setVariable(paramNames[i], args[i]);
        }

        // 下面是 支持 #p0 #p1 这样取变量
        if (args != null && args.length > 0) {
            for (int i = 0, len = args.length; i < len; i++) {
                context.setVariable("p" + i, args[i]);
            }
        }
        Expression exp = spelExpressionParser.parseExpression(expression, ParserContext.TEMPLATE_EXPRESSION);
        return exp.getValue(context, cls);
    }
 
    @Override
    public void setBeanFactory(@NonNull BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
 
    @Bean
    public SpelExpressionParser spelExpressionParser() {
        return new SpelExpressionParser();
    }

    public String hello() {
        return "hello";
    }
}