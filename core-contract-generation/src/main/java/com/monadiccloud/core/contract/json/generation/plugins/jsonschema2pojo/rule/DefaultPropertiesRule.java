package com.monadiccloud.core.contract.json.generation.plugins.jsonschema2pojo.rule;

import com.fasterxml.jackson.databind.JsonNode;
import com.monadiccloud.core.contract.json.generation.plugins.jsonschema2pojo.rule.action.AddGenericInterface;
import com.monadiccloud.core.contract.json.generation.plugins.jsonschema2pojo.rule.action.AddInterface;
import com.monadiccloud.core.contract.json.generation.plugins.jsonschema2pojo.rule.action.ClassAction;
import com.sun.codemodel.JDefinedClass;
import org.jsonschema2pojo.Schema;
import org.jsonschema2pojo.rules.PropertiesRule;
import org.jsonschema2pojo.rules.RuleFactory;

import java.util.List;

import static java.util.Arrays.asList;

public class DefaultPropertiesRule extends PropertiesRule {
    protected List<ClassAction> actions = asList(
//            new AddInterface(asList("xxx"), "com.monadiccloud.core.amqp.message.Xxx"),
//            new AddGenericInterface("yyy", "com.monadiccloud.core.amqp.message.Yyy")
    );

    public DefaultPropertiesRule(RuleFactory ruleFactory) {
        super(ruleFactory);
    }

    @Override
    public JDefinedClass apply(String nodeName, JsonNode node, JDefinedClass jClass, Schema schema) {
        jClass = super.apply(nodeName, node, jClass, schema);

        // Run actions once properties of jClass are populated
        for (ClassAction action : actions) {
            if (action.supports(jClass)) {
                jClass = action.apply(jClass);
            }
        }
        return jClass;
    }
}
