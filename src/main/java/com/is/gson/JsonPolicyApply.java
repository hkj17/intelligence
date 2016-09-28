package com.is.gson;

import javax.enterprise.util.AnnotationLiteral;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/** 
 * @author zhuolin(zl@nbicc.com) 
 * @date 2014年12月24日
 * 类说明 
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonPolicyApply {

    JsonPolicyDef.Policy value();

    public static class JsonPolicyApplyLiteral extends AnnotationLiteral<JsonPolicyApply>
            implements JsonPolicyApply {


        /**
		 * 
		 */
		private static final long serialVersionUID = -5618753593181246531L;
		private JsonPolicyDef.Policy policy;

        public JsonPolicyApplyLiteral(JsonPolicyDef.Policy policy) {
            this.policy = policy;
        }

        @Override
        public JsonPolicyDef.Policy value() {
            return policy;
        }
    }
}
