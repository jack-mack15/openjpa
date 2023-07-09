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

import org.apache.openjpa.conf.OpenJPAConfiguration;
import org.apache.openjpa.jdbc.kernel.PreparedQueryCacheImpl;
import org.apache.openjpa.jdbc.kernel.PreparedQueryImpl;
import org.apache.openjpa.kernel.*;
import org.apache.openjpa.lib.conf.ConfigurationImpl;
import org.apache.openjpa.lib.log.Log;
import org.apache.openjpa.lib.log.LogFactoryImpl;
import org.hsqldb.server.ServerConfiguration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import java.util.Arrays;
import java.util.Collection;

import static java.lang.System.out;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(value= Parameterized.class)
public class TestCache {

    private PreparedQueryImpl query;
    private boolean firstExpected;
    private boolean getExpected;
    private PreparedQueryCacheImpl cache;

    @Before
    public void setUp(){
        cache = new PreparedQueryCacheImpl();
        cache.markUncachable("unCache",null);
        cache.setExcludes("excluded");

        //per aumentare il coverage
        PreparedQueryImpl alreadyCached = new PreparedQueryImpl("cached","testSql",null);
        cache.cache(alreadyCached);
        ConfigurationImpl mockedConf = mock(ConfigurationImpl.class);
        LogFactoryImpl logFactory = new LogFactoryImpl();
        LogFactoryImpl.LogImpl log = (LogFactoryImpl.LogImpl) logFactory.getLog("test");
        log.setLevel((short) 0);
        when(mockedConf.getLog(OpenJPAConfiguration.LOG_RUNTIME)).thenReturn(log);
        cache.setConfiguration(mockedConf);
    }

    @Parameters
    public static Collection<Object[]> getParameters(){

        PreparedQueryImpl valid = new PreparedQueryImpl("testId","testSql",null);
        PreparedQueryImpl uncachable = new PreparedQueryImpl("unCache","testSql",null);
        PreparedQueryImpl excluded = new PreparedQueryImpl("excluded","testSql",null);

        //per aumentarea coverage
        PreparedQueryImpl alreadyCached = new PreparedQueryImpl("cached","testSql",null);


        return Arrays.asList(new Object[][]{
                //first     second      query
                {true,      true,       valid},
                {false,     false,      uncachable},
                {false,     false,      excluded},
                {false,     false,      null},

                //per aumentare coverage
                {false,     true,      alreadyCached}
        });
    }

    public TestCache(boolean firstExpected, boolean getExpected, PreparedQueryImpl query){
        this.firstExpected = firstExpected;
        this.getExpected = getExpected;
        this.query = query;
    }

    @Test
    public void testSingleCache(){

        boolean result;

        try {

            result = cache.cache(query);
            Assert.assertEquals(firstExpected,result);

            PreparedQuery getQuery = cache.get(query.getIdentifier());

            if(getExpected)
                Assert.assertNotNull(getQuery);
            else
                Assert.assertNull(getQuery);
        }
        catch (Exception e){
            Assert.assertTrue(e instanceof NullPointerException);
            assertFalse(firstExpected);
        }

    }

}
