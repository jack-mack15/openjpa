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

package PreparedQueryCacheImplTests;
import org.apache.openjpa.jdbc.kernel.PreparedQueryCacheImpl;
import org.apache.openjpa.jdbc.kernel.PreparedQueryImpl;
import org.apache.openjpa.kernel.BrokerImpl;
import org.apache.openjpa.kernel.PreparedQuery;
import org.apache.openjpa.kernel.PreparedQueryCache;
import org.apache.openjpa.kernel.QueryImpl;
import org.apache.openjpa.lib.rop.ListResultList;
import org.apache.openjpa.lib.rop.ResultList;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import java.util.Arrays;
import java.util.Collection;

import static java.lang.System.out;

@RunWith(value= Parameterized.class)
public class TestInitialize {

    private String id;
    private String sql;
    private boolean expected;
    private static PreparedQueryCache cache;

    @Parameters
    public static Collection<Object[]> getParameters(){
        return Arrays.asList(new Object[][]{
                //first     id             sql
                {true,      "testId",      "testSql"},
                {true,      "testId",      ""},
                {true,       "testId",      null},
                {true,      "",            "testSql"},
                {true,      "",            ""},
                {true,      "",            null},
                {true,      null,          "testSql"},
                {true,      null,          ""},
                {true,      null,          null}
        });
    }

    @BeforeClass
    public static void setUp(){
        cache = new PreparedQueryCacheImpl();

        PreparedQuery query1 = new PreparedQueryImpl("testId",   "testSql",null);
        PreparedQuery query2 = new PreparedQueryImpl("","testSql",null);
        PreparedQuery query3 = new PreparedQueryImpl("","",null);

        cache.cache(query1);
        cache.cache(query2);
        cache.cache(query3);
    }

    public TestInitialize(boolean firstExpected,String id,String sql){
        this.expected = firstExpected;
        this.id = id;
        this.sql = sql;
    }

    @Test
    public void testInitialize(){
        //tocca capi sto resulList che tocca fa
        ListResultList resultList = new ListResultList(null);
        PreparedQuery resultQuery = cache.initialize(id,resultList);
        Assert.assertNull(resultQuery);
    }
}
