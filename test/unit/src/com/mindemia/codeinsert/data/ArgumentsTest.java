/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package com.mindemia.codeinsert.data;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;

public class ArgumentsTest {

    @Test
    public void testDeserializeArguments() throws Exception {
        // Create an instance of ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();

        // JSON string representing the Arguments object
        String json = """
        {
            "imports": ["java.util.List", "java.util.ArrayList"],
            "methods": [
                {
                    "modifier": "public",
                    "name": "exampleMethod",
                    "params": [
                        {
                            "name": "param1",
                            "type": "String"
                        }
                    ],
                    "body": "System.out.println(param1);",
                    "returnType": "void",
                    "throwsList": []
                }
            ]
        }
        """;

        // Deserialize the JSON string into an Arguments object
        Arguments arguments = objectMapper.readValue(json, Arguments.class);

        // Assertions to verify the deserialization
        assertNotNull(arguments);
        assertEquals(2, arguments.imports().size());
        assertEquals("java.util.List", arguments.imports().get(0));
        assertEquals("java.util.ArrayList", arguments.imports().get(1));

        List<Method> methods = arguments.methods();
        assertNotNull(methods);
        assertEquals(1, methods.size());

        Method method = methods.get(0);
        assertEquals("public", method.modifier());
        assertEquals("exampleMethod", method.name());
        assertEquals("void", method.returnType());
        assertEquals("System.out.println(param1);", method.body());
        assertTrue(method.throwsList().isEmpty());

        List<Parameter> params = method.params();
        assertNotNull(params);
        assertEquals(1, params.size());

        Parameter param = params.get(0);
        assertEquals("param1", param.name());
        assertEquals("String", param.type());
    }
    @Test
    public void testDeserializeActualResponse() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        var json = """
        {
                      "imports": [],
                      "methods": [{
                        "modifier": "public", "name": "main", "params": [{"name": "args", "type": "String[]"}],
                        "body": "System.out.println(\\\"Hello, World!\\\");",
                        "returnType": "void",
                        "throwsList": []}
                      ]
        }
        """;

        // Deserialize the JSON string into an Arguments object
        Arguments arguments = objectMapper.readValue(json, Arguments.class);
        assertNotNull(arguments);
    }
    
}
