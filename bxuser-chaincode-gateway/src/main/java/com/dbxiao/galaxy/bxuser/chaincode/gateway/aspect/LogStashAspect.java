package com.dbxiao.galaxy.bxuser.chaincode.gateway.aspect;

import com.alibaba.fastjson.JSON;
import com.dbxiao.galaxy.bxuser.chaincode.gateway.api.query.CommonBody;
import com.dbxiao.galaxy.bxuser.chaincode.gateway.client.ChannelEnum;
import com.dbxiao.galaxy.bxuser.chaincode.gateway.service.LogStashService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author amen
 * @date 2022/11/10
 */
@Slf4j
@Aspect
@Component
public class LogStashAspect {

    private static final Long OPERATOR = 4934324034324L;

    @Autowired
    private LogStashService logStashService;

    @Pointcut("@annotation(LogStash)")
    public void logStash() {

    }
    @Around("logStash()")
    public Object auth(ProceedingJoinPoint pjp) throws Throwable {
        Object[] args = pjp.getArgs();
        Object cbO = args[0];
        CommonBody commonBody = (CommonBody) cbO;
        expand(commonBody);
        ChannelEnum channelEnum = commonBody.getChannelEnum();
        String method = commonBody.getMethod();

        try {

            Object rs = pjp.proceed(new Object[]{commonBody});
            logStashService.stash(channelEnum.getContract(), JSON.toJSONString(commonBody), Boolean.TRUE, OPERATOR, commonBody.getOperatorAt());
            return rs;
        } finally {
            logStashService.stash(channelEnum.getContract(), JSON.toJSONString(commonBody), Boolean.FALSE, OPERATOR, commonBody.getOperatorAt());

        }

    }

    private void expand(CommonBody commonBody) {
        commonBody.setOperatorId(OPERATOR);
        commonBody.setOperatorAt(System.currentTimeMillis());
    }


}
