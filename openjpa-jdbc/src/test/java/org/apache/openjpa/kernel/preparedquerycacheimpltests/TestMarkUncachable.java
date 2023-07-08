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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import java.util.Arrays;
import java.util.Collection;

import static java.lang.System.out;
import static org.junit.Assert.*;

@RunWith(value= Parameterized.class)
public class TestMarkUncachable {

    private PreparedQueryCache cache;
    private boolean expected;
    private String id;
    private PreparedQueryCache.Exclusion exclusion;
    private PreparedQueryImpl queryInCache;

    @Before
    public void setUp(){
        cache = new PreparedQueryCacheImpl();

        queryInCache = new PreparedQueryImpl("testId",   "testSql",null);

        cache.cache(queryInCache);

        cache.endConfiguration();


        cache.setConfiguration(new JDBCConfigurationImpl());

        //per aumentare coverage
        cache.setEnableStatistics(true);
        PreparedQueryCache.Exclusion strongExcl = new PreparedQueryCacheImpl.StrongExclusion("pattern","reason");
        cache.markUncachable("notInCache",strongExcl);
    }

    @Parameters
    public static Collection<Object[]> getParameters(){
        PreparedQueryCache.Exclusion strongExcl = new PreparedQueryCacheImpl.StrongExclusion("pattern","reason");
        PreparedQueryCache.Exclusion weakExcl = new PreparedQueryCacheImpl.WeakExclusion("pattern","reason");
        PreparedQueryCache.Exclusion invalid = new PreparedQueryCacheImpl.WeakExclusion(null,null);

        return Arrays.asList(new Object[][]{
                //expected      id              exclusion
                {true,          "testId",       strongExcl},
                {false,         "notInCache",   strongExcl},
                {false,         "",             strongExcl},
                {false,         null,           strongExcl},
                {true,          "testId",       weakExcl},
                {false,         "notInCache",   weakExcl},
                {false,         "",             weakExcl},
                {false,         null,           weakExcl},
                {true,          "testId",       invalid},
                {false,         "notInCache",   invalid},
                {false,         "",             invalid},
                {false,         null,           invalid},
                {true,          "testId",       null},
                {false,         "notInCache",   null},
                {false,         "",             null},
                {false,         null,           null},

                //per aumentare coverage
                {false,     "notInCache",     weakExcl}
        });
    }

    public TestMarkUncachable(boolean expected, String id, PreparedQueryCache.Exclusion exclusion){
        this.id = id;
        this.expected = expected;
        this.exclusion = exclusion;
    }

    @Test
    public void testIsCacheable() {

        PreparedQuery result;

        result = cache.markUncachable(id,exclusion);
        if (result != null) {
            assertEquals(queryInCache, result);
            Assert.assertTrue(expected);
            Assert.assertFalse(cache.isCachable(id));
        }
        else {
            Assert.assertFalse(expected);
            Assert.assertFalse(cache.isCachable(id));
        }
    }

    @After
    public void tearDown(){
        cache.clear();
    }
}
