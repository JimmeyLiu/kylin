package org.kylin.processor.handler.tps.rule;

import java.util.Map;

/**
 * Created by jimmey on 15-7-28.
 */
public class TpsRule {

    Map<String, TokenBucketLimiter> appRule;
    Map<String, TokenBucketLimiter> methodRule;
    TokenBucketLimiter serviceRule;

    public TpsRule(Map<String, TokenBucketLimiter> appRule, Map<String, TokenBucketLimiter> methodRule, TokenBucketLimiter serviceRule) {
        this.appRule = appRule;
        this.methodRule = methodRule;
        this.serviceRule = serviceRule;
    }

    public boolean hitAppRule(String appName) {
        TokenBucketLimiter limiter = appRule.get(appName);
        return limiter != null && limiter.check();
    }

    public boolean hitMethodRule(String method) {
        TokenBucketLimiter limiter = methodRule.get(method);
        return limiter != null && limiter.check();
    }

    public boolean hitServiceRule() {
        return serviceRule != null && serviceRule.check();
    }

}
