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


import org.apache.openjpa.jdbc.conf.JDBCConfigurationImpl;
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
public class TestMarkUncachable {

    private PreparedQueryCache cache;
    private boolean expected;
    private String id;
    private PreparedQueryCache.Exclusion exclusion;

    @Before
    public void setUp(){
        cache = new PreparedQueryCacheImpl();

        PreparedQuery query1 = new PreparedQueryImpl("testId",   "testSql",null);
        PreparedQuery query2 = new PreparedQueryImpl("",   "testSql",null);

        cache.cache(query1);
        cache.cache(query2);

        cache.endConfiguration();

        //per aumentare coverage
        cache.setEnableStatistics(true);

        cache.setConfiguration(new JDBCConfigurationImpl());

    }

    @Parameters
    public static Collection<Object[]> getParameters(){
        PreparedQueryCache.Exclusion excl = new PreparedQueryCacheImpl.StrongExclusion("pattern","reason");

        return Arrays.asList(new Object[][]{
                //expected  id          exclusion
                {true,      "testId",   excl},
                {true,      "",         excl},
                {false,     null,       excl},
                {true,      "testId",   null},
                {true,      "",         null},
                {false,     null,       null},

                //per aumentare coverage
                {false,     "notInCache",     null}
        });
    }

    public TestMarkUncachable(boolean expected, String id, PreparedQueryCache.Exclusion exclusion){
        this.id = id;
        this.expected = expected;
        this.exclusion = exclusion;
    }

    @Test
    public void testIsCacheable() throws NoSuchFieldException {

        PreparedQuery result;

        result = cache.markUncachable(id,exclusion);
        if (expected)
            assertNotNull(result);
        else
            assertNull(result);

    }

    @After
    public void tearDown(){
        cache.clear();
    }
}
