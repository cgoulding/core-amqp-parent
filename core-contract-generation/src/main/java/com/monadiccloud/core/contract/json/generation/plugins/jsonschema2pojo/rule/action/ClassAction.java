package com.monadiccloud.core.contract.json.generation.plugins.jsonschema2pojo.rule.action;

import com.sun.codemodel.JDefinedClass;

import java.util.function.Function;

public interface ClassAction extends Function<JDefinedClass, JDefinedClass> {
    boolean supports(JDefinedClass jClass);

}
