/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package com.mindemia.codeinsert.tools;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mindemia.codeinsert.data.GitPatchResponse;
import com.mindemia.codeinsert.data.InsertCodeToolCall;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author rickard
 */
public class ToolJsonBuilderTest {
    
    public ToolJsonBuilderTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of createToolsTemplate method, of class ToolJsonBuilder.
     */
    @Test
    public void testCreateToolsTemplate() {
        System.out.println("createToolsTemplate");
        ToolJsonBuilder instance = new ToolJsonBuilder();
        ArrayNode result = instance.createToolsTemplate();
        System.out.println(result.toPrettyString());
        assertNotNull(result);
    }

    /**
     * Test of buildGitPatchTool method, of class ToolJsonBuilder.
     */
    @Test
    public void testBuildGitPatchTool() {
        System.out.println("buildGitPatchTool");
        ToolJsonBuilder instance = new ToolJsonBuilder();
        ObjectNode result = instance.buildGitPatchTool();
        assertNotNull(result);
    }
    
}
