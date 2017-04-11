package com.monadiccloud.core.contract.json.generation.plugins.jsonschema2pojo.rule.action;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;

import java.util.List;

import static java.util.Arrays.asList;

public class AddInterface implements ClassAction {
    protected List<String> requiredProperties;
    protected String interfaceName;

    public AddInterface(String requiredProperty, String interfaceName) {
        this(asList(requiredProperty), interfaceName);
    }

    public AddInterface(List<String> requiredProperties, String interfaceName) {
        this.requiredProperties = requiredProperties;
        this.interfaceName = interfaceName;
    }

    @Override
    public boolean supports(JDefinedClass jClass) {
        return jClass.fields().keySet().containsAll(requiredProperties);
    }

    @Override
    public JDefinedClass apply(JDefinedClass jClass) {
        JCodeModel codeModel = jClass.owner();
        JClass jInterface = codeModel.directClass(interfaceName);
        return jClass._implements(jInterface);
    }
}
