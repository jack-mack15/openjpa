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
import org.apache.openjpa.kernel.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import java.util.Arrays;
import java.util.Collection;

@RunWith(value= Parameterized.class)
public class TestCache {

    private String id;
    private String sql;
    private boolean firstExpected;
    private boolean secondExpected;
    private boolean thirdExpected;

    @Parameters
    public static Collection<Object[]> getParameters(){
        return Arrays.asList(new Object[][]{
                //first     second      third       id             sql
                {true,      false,      true,       "testId",      "testSql"},
                {true,      false,      true,       "testId",      ""},
                {true,      false,      true,        "testId",      null},
                {true,      true,       true,       "",            "testSql"},
                {true,      true,       true,       "",            ""},
                {true,      true,       true,       "",            null},
                {true,      true,       false,      null,          "testSql"},
                {true,      true,       false,      null,          ""},
                {true,      true,       false,      null,          null}
        });
    }

    public TestCache(boolean firstExpected,boolean secondExpected,boolean thirdExpected,String id,String sql){

        this.firstExpected = firstExpected;
        this.secondExpected = secondExpected;
        this.thirdExpected = thirdExpected;
        this.id = id;
        this.sql = sql;
    }

    @Test
    public void testSingleCache(){

        boolean result;

        PreparedQueryCache cache = new PreparedQueryCacheImpl();
        PreparedQuery query = new PreparedQueryImpl(id,sql,null);

        result = cache.cache(query);
        Assert.assertEquals(firstExpected,result);
    }

    @Test
    public void testSecondCache(){

        boolean result;

        PreparedQueryCache cache = new PreparedQueryCacheImpl();
        PreparedQuery query = new PreparedQueryImpl("testId",   "testSql",null);
        cache.cache(query);

        PreparedQuery secondQuery = new PreparedQueryImpl(id,sql,null);

        result = cache.cache(secondQuery);
        Assert.assertEquals(secondExpected,result);

    }

    @Test
    public void testGet(){

        PreparedQuery result;

        PreparedQueryCache cache = new PreparedQueryCacheImpl();
        PreparedQuery query1 = new PreparedQueryImpl("testId",   "testSql",null);
        PreparedQuery query2 = new PreparedQueryImpl("","testSql",null);
        PreparedQuery query3 = new PreparedQueryImpl("","",null);

        cache.cache(query1);
        cache.cache(query2);
        cache.cache(query3);

        if(thirdExpected){
            result = cache.get(id);
            Assert.assertNotNull(result);
        }
        else{
            result = cache.get(id);
            Assert.assertNull(result);
        }


    }
}
