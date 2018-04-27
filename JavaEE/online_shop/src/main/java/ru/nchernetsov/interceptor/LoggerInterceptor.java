package ru.nchernetsov.interceptor;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import java.io.Serializable;
import java.util.Arrays;

public class LoggerInterceptor implements Serializable {
    @AroundInvoke
    public Object logInvocation(InvocationContext invocationContext) throws Exception {
        final String method = invocationContext.getMethod().getName();
        System.out.println("Invoke method: " + method +
            " Parameters: " + Arrays.toString(invocationContext.getParameters()));
        return invocationContext.proceed();
    }
}
