package com.monadiccloud.core.contract.json.generation.plugins.jsonschema2pojo.rule.action;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMod;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

public class AddGenericInterfaceTest {
    AddGenericInterface action;
    JCodeModel codeModel;
    JDefinedClass jClass;

    @Before
    public void setUp() throws Exception {
        codeModel = new JCodeModel();
        jClass = codeModel._class("com.monadiccloud.test.TestClass");

        action = new AddGenericInterface("field1", "com.monadiccloud.test.TestInterface");
    }

    @Test
    public void supports() throws Exception {
        assertFalse(action.supports(jClass));

        jClass.field(JMod.PRIVATE, String.class, "field1");
        assertTrue(action.supports(jClass));
    }

    @Test
    public void apply() throws Exception {
        // Generated class:
        // String field1;

        jClass.field(JMod.PRIVATE, String.class, "field1");

        action.apply(jClass);

        Iterator<JClass> interfaces = jClass._implements();
        assertTrue(interfaces.hasNext());
        assertEquals("com.monadiccloud.test.TestInterface<java.lang.String>", interfaces.next().fullName());
    }

    @Test
    public void apply_wrappedField() throws Exception {
        // Generated class:
        // List<String> field1;

        JDefinedClass listType = codeModel._class(List.class.getName());
        JClass fieldType = listType.narrow(String.class);
        jClass.field(JMod.PRIVATE, fieldType, "field1");

        action.unwrapFieldType(List.class);
        action.apply(jClass);

        Iterator<JClass> interfaces = jClass._implements();
        assertTrue(interfaces.hasNext());
        assertEquals("com.monadiccloud.test.TestInterface<java.lang.String>", interfaces.next().fullName());
    }
}