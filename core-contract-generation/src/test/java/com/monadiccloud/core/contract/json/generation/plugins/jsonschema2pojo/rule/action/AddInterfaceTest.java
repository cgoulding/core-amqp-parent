package com.monadiccloud.core.contract.json.generation.plugins.jsonschema2pojo.rule.action;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMod;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

public class AddInterfaceTest {
    AddInterface action;
    JDefinedClass jClass;

    @Before
    public void setUp() throws Exception {
        JCodeModel codeModel = new JCodeModel();
        jClass = codeModel._class("com.monadiccloud.core.contract.TestClass");

        action = new AddInterface(asList("field1", "field2"), "com.monadiccloud.core.contract.TestInterface");
    }

    @Test
    public void supports() throws Exception {
        assertFalse(action.supports(jClass));

        jClass.field(JMod.PRIVATE, String.class, "field1");
        assertFalse(action.supports(jClass));

        jClass.field(JMod.PRIVATE, String.class, "field2");
        assertTrue(action.supports(jClass));
    }

    @Test
    public void apply() throws Exception {
        action.apply(jClass);

        Iterator<JClass> interfaces = jClass._implements();
        assertTrue(interfaces.hasNext());
        assertEquals("com.monadiccloud.core.contract.TestInterface", interfaces.next().fullName());
    }
}