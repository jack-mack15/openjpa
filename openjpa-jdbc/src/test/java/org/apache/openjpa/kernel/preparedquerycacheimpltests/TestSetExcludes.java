/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.openjpa.kernel.preparedquerycacheimpltests;

import org.apache.openjpa.jdbc.kernel.PreparedQueryCacheImpl;
import org.apache.openjpa.jdbc.kernel.PreparedQueryImpl;
import org.apache.openjpa.kernel.PreparedQuery;
import org.apache.openjpa.kernel.PreparedQueryCache;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static java.lang.System.out;
import static org.junit.Assert.assertFalse;

@RunWith(value= Parameterized.class)
public class TestSetExcludes {
    private PreparedQueryCache cache;
    private boolean expected;
    private String input;
    private int initialLen;

    @Before
    public void setUp(){
        cache = new PreparedQueryCacheImpl();

        PreparedQuery query1 = new PreparedQueryImpl("testId",   "testSql",null);
        PreparedQuery query2 = new PreparedQueryImpl("testId2",   "testSql2",null);
        PreparedQuery query3 = new PreparedQueryImpl("testId3",   "testSql2",null);
        PreparedQuery query4 = new PreparedQueryImpl("testId4",   "testSql2",null);

        cache.cache(query1);
        cache.cache(query2);
        cache.cache(query3);
        cache.cache(query4);

        cache.endConfiguration();

        initialLen = cache.getExcludes().size();
    }

    @Parameters
    public static Collection<Object[]> getParameters(){
        return Arrays.asList(new Object[][]{
                //expected      input
                {true,          "testId2;testId3;testId4"},
                {true,          "testId;notInCache;test3"},
                {true,          "testId"},
                {true,          "notInCache"},
                {true,          "testId;testId"},
                {false,         ""},
                {null,          null}
        });
    }

    public TestSetExcludes(boolean expected,String input){
        this.input = input;
        this.expected = expected;
    }

    @Test
    public void testSetExcludes(){

        try {
            String[] rules;
            cache.setExcludes(input);
            if (input != null) {
                rules = input.split(";");
                int addedRules = 0;
                for (String rule: rules){
                    if (cache.isExcluded(rule) != null)
                        addedRules++;
                }
                Assert.assertEquals(initialLen-addedRules,cache.getExcludes().size());
            }
            else{
                Assert.assertNull(expected);
                Assert.assertEquals(initialLen,cache.getExcludes().size());
            }
        }

        catch (Exception e){
            //questo branch è rimasto come dimostrazione che il valore di output atteso della
            //stringa null iniziale era scorretto

            Assert.assertTrue(e instanceof NullPointerException);
            Assert.assertEquals(input,null);
        }
    }

    @After
    public void tearDown(){
        cache.clear();
    }
}
