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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

@RunWith(value= Parameterized.class)
public class TestIsCachable {

    private PreparedQueryCache cache;
    private boolean expected;
    private String id;

    @Before
    public void setUp(){
        cache = new PreparedQueryCacheImpl();

        PreparedQuery query1 = new PreparedQueryImpl("valid",   "testSql",null);

        cache.cache(query1);

        cache.markUncachable("uncachable",null);
        cache.setExcludes("excluded");
        cache.endConfiguration();

    }

    @Parameters
    public static Collection<Object[]> getParameters(){
        return Arrays.asList(new Object[][]{
                //expected      id
                {true,          "valid"},
                {false,         "uncachable"},
                //questi tre casi di test sollevano eccezione NullPointerException
                {false,         "excluded"},
                {false,         "not valid"},
                {false,         null},
        });
    }

    public TestIsCachable(boolean expected, String id){
        this.id = id;
        this.expected = expected;
    }

    @Test
    public void testIsCacheable(){

        boolean result;
        try {
            result = cache.isCachable(id);
            assertEquals(expected, result);
        }
        catch(Exception e){
            assertTrue(e instanceof NullPointerException);
            assertFalse(expected);
        }
    }

    @After
    public void tearDown(){
        cache.clear();
    }
}
