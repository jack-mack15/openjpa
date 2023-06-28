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

@RunWith(value= Parameterized.class)
public class TestRemoveExclusion {

    private PreparedQueryCache cache;
    private int expected;
    private String input;

    @Before
    public void setUp(){
        cache = new PreparedQueryCacheImpl();

        PreparedQuery query1 = new PreparedQueryImpl("testId",   "testSql",null);
        PreparedQuery query2 = new PreparedQueryImpl("testId2",   "testSql2",null);

        cache.cache(query1);
        cache.cache(query2);

        cache.endConfiguration();
        cache.setExcludes("testId");
        cache.setExcludes("otherId");
    }

    @Parameters
    public static Collection<Object[]> getParameters(){
        return Arrays.asList(new Object[][]{
                //expected      input
                {1,             "testId"},
                {2,             ""},
                {2,             null},
                {1,             "otherId"}
        });
    }

    public TestRemoveExclusion(int expected, String input){
        this.input = input;
        this.expected = expected;
    }

    @Test
    public void testRemoveExclusion(){

        try {
            cache.removeExclusionPattern(input);
        }
        catch(Exception e){
            Assert.assertTrue(e instanceof NullPointerException);
        }

        int resultExclusions = cache.getExcludes().size();
        Assert.assertEquals(expected,resultExclusions);

    }

    @After
    public void tearDown(){
        cache.clear();
    }
}
