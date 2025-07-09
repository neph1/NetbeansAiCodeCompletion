/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package com.mindemia.codeinsert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mindemia.codeinsert.data.AIResponse;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.openide.util.Exceptions;

/**
 *
 * @author rickard
 */
public class ResponseParserTest {
    
    public ResponseParserTest() {
    }
    
    /**
     * Test of parseResponse method, of class ResponseParser.
     */
    @Test
    public void testCreateResponse() {
        System.out.println("parseResponse");
        String responseBody = """
                              {
                                "id": "b0ced7388df14845a9abd4de77577d07",
                                "object": "chat.completion",
                                "created": 1749755051,
                                "model": "codestral-2501",
                                "choices": [
                                  {
                                    "index": 0,
                                    "message": {
                                      "role": "assistant",
                                      "tool_calls": [
                                        {
                                          "id": "QtPVztX2a",
                                          "function": {
                                            "name": "insert_code",
                                            "arguments": "{\"imports\": [], \"methods\": [{\"modifier\": \"public\",\"name\": \"main\", \"params\": [{\"name\": \"args\", \"type\": \"String[]\"}], \"body\": \"System.out.println(\\\"Hello, World!\\\");\", \"returnType\": \"void\", \"throwsList\": []}]}"
                                          }
                                        }
                                      ]
                                    }
                                  }
                                ]
                              }
                              """;
        String responseBody2 = """
                               {
                                 "id": "33a796130951461cb723cf85f96ea2c9",
                                 "object": "chat.completion",
                                 "created": 1750359724,
                                 "model": "codestral-2501",
                                 "choices": [
                                   {
                                     "index": 0,
                                     "message": {
                                       "role": "assistant",
                                       "tool_calls": [
                                         {
                                           "id": "a2JGu8VWC",
                                           "function": {
                                             "name": "git_patch",
                                             "arguments": {
                                               "patch": "diff --git a/Main.java b/Main.java\nindex 0000000..1234567\n--- a/Main.java\n+++ b/Main.java\n@@ -1,10 +1,15 @@\n class One {\n   public  null <init>();\n }\n class Two {\n   public  null <init>();\n }\n class Three {\n   public  null <init>();\n }\n mygame;\n\n import otherpack.Three;\n\n /**\n  *\n  * @author rickard\n  */\n public class Main {\n     One one = new One();\n\n     Three three = new Three();\n\n+    public void helloWorld() {\n+        System.out.println(\\\"Hello, World!\\\");\n+    }\n }"
                                             }
                                           },
                                           "index": 0
                                         }
                                       ],
                                       "content": "To generate a \"Hello World\" method as a git patch, we need to create a method that prints \"Hello, World!\" to the console. Below is the git patch that adds this method to the `Main` class:\n\n```plaintext\ndiff --git a/Main.java b/Main.java\nindex 0000000..1234567\n--- a/Main.java\n+++ b/Main.java\n@@ -1,10 +1,15 @@\n class One {\n   public  null <init>();\n }\n class Two {\n   public  null <init>();\n }\n class Three {\n   public  null <init>();\n }\n mygame;\n\n import otherpack.Three;\n\n /**\n  *\n  * @author rickard\n  */\n public class Main {\n     One one = new One();\n\n     Three three = new Three();\n\n+    public void helloWorld() {\n+        System.out.println(\"Hello, World!\");\n+    }\n }\n```\n\nNow, I will call the `git_patch` function with this patch."
                                     },
                                     "finish_reason": "tool_calls"
                                   }
                                 ],
                                 "usage": {
                                   "prompt_tokens": 240,
                                   "total_tokens": 681,
                                   "completion_tokens": 441
                                 }
                               }
                               """;
        ResponseParser instance = new ResponseParser();
        String expResult = "";
        AIResponse result = null;
        try {
            result = instance.createResponse(responseBody2);
        } catch (JsonProcessingException ex) {
            Exceptions.printStackTrace(ex);
            fail();
        }
        assertNotNull(result);
    }
    
}
