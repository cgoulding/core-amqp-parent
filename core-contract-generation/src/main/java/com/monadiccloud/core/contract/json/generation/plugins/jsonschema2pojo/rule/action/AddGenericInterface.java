package com.monadiccloud.core.contract.json.generation.plugins.jsonschema2pojo.rule.action;

import com.sun.codemodel.*;

import java.util.List;
import java.util.Objects;

public class AddGenericInterface implements ClassAction {
    private String property;
    private String interfaceName;
    private String wrapperClassName;

    public AddGenericInterface(String property, String interfaceName) {
        this.property = property;
        this.interfaceName = interfaceName;
    }

    public AddGenericInterface unwrapFieldType(Class wrapperClass) {
        this.wrapperClassName = wrapperClass.getName();
        return this;
    }

    @Override
    public boolean supports(JDefinedClass jClass) {
        return jClass.fields().containsKey(property);
    }

    @Override
    public JDefinedClass apply(JDefinedClass jClass) {
        JCodeModel codeModel = jClass.owner();
        JClass jInterface = codeModel.directClass(interfaceName);

        JFieldVar field = jClass.fields().get(property);
        JType fieldType = field.type();
        fieldType = unwrap(fieldType);
        JClass jGenericInterface = jInterface.narrow(fieldType);

        return jClass._implements(jGenericInterface);
    }

    protected JType unwrap(JType type) {
        if (!Objects.equals(type.erasure().fullName(), wrapperClassName)) {
            return type;
        }

        if (!(type instanceof JClass)) {
            return type;
        }
        JClass classType = (JClass) type;

        List<JClass> parameters = classType.getTypeParameters();
        if (parameters == null || parameters.isEmpty()) {
            return type;
        }

        return parameters.get(0);
    }
}
