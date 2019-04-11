package be.somedi.printandsend;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class LogAOP {

    private static final Logger LOGGER = LogManager.getLogger(LogAOP.class);

    @Before(value = "within(be.somedi.printandsend.jobs.WatchServiceOfDirectory)")
    public void logWatchService(JoinPoint joinPoint){
        LOGGER.info("In method: " + joinPoint.getSignature().getName());
    }
}
