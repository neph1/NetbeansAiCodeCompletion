/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package com.mindemia.codeinsert.tools.gitpatch;

import java.util.List;
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
public class PatchParserTest {
    
    public PatchParserTest() {
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
     * Test of parse method, of class PatchParser.
     */
    @Test
    public void testParse() {
        String diffText = "--git a/Main.java b/Main.java\nindex 0000001..0ddf123\n--- a/Main.java\n+++ b/Main.java\n@@ -1,10 +1,15 @@\n class One {\n   public  null <init>();\n }\n class Two {\n   public  null <init>();\n }\n class Three {\n   public  null <init>();\n }\n mygame;\n\n import otherpack.Three;\n\n /**\n *\n * @author rickard\n */\n public class Main {\n     One one = new One();\n\n     Three three = new Three();\n\n+    public static void main(String[] args) {\n+        System.out.println(\\\"Hello, World!\\\");\n+    }\n }\n";
        List<GitPatch> result = PatchParser.parse(diffText);
        assertEquals(1, result.size());
    }
    
}
