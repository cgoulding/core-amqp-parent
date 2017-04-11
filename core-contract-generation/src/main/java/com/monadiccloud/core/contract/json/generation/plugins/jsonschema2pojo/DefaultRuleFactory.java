package com.monadiccloud.core.contract.json.generation.plugins.jsonschema2pojo;

import com.monadiccloud.core.contract.json.generation.plugins.jsonschema2pojo.rule.DefaultPropertiesRule;
import com.sun.codemodel.JDefinedClass;
import org.jsonschema2pojo.rules.Rule;
import org.jsonschema2pojo.rules.RuleFactory;

public class DefaultRuleFactory extends RuleFactory {
    @Override
    public Rule<JDefinedClass, JDefinedClass> getPropertiesRule() {
        return new DefaultPropertiesRule(this);
    }
}
