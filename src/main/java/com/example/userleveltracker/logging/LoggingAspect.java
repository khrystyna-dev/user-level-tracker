package com.example.userleveltracker.logging;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StopWatch;

/**
 * Aspect for logging method execution time in the service layer.
 * This aspect uses Aspect-Oriented Programming (AOP) to intercept method calls in the service
 * implementation classes within the package {@code com.example.userleveltracker.service.impl}.
 * It logs the execution time of each method, providing insights into performance.
 *
 * <p>The logging is handled using the Log4j2 and execution time is measured using
 * the {@link StopWatch} class from the Spring Framework.</p>
 */
@Aspect
@Configuration
@Log4j2
public class LoggingAspect {

    /**
     * Advice that logs the execution time of methods in service implementation classes.
     * This method is executed around the advised methods. It starts a stopwatch before the
     * method execution and stops it afterward to measure the time taken for the method to execute.
     * The execution time is then logged using Log4j2.
     *
     * @param joinPoint the join point representing the method execution being advised
     * @return the result of the method execution
     * @throws Throwable if the method invocation throws an exception
     */
    @Around("execution(* com.example.userleveltracker.service.impl.*.*(..)))")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

        String className = methodSignature.getDeclaringType().getSimpleName();
        String methodName = methodSignature.getName();

        final StopWatch stopWatch = new StopWatch();

        stopWatch.start();
        Object result = joinPoint.proceed();
        stopWatch.stop();
        final double totalTimeMillis = stopWatch.getTotalTimeNanos() / Math.pow(10, 6);

        log.info("Execution time of {}.{} : {} ms", className, methodName, totalTimeMillis);

        return result;
    }
}
