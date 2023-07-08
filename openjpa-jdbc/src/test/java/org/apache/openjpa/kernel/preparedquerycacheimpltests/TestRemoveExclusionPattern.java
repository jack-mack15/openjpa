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
import org.apache.openjpa.kernel.PreparedQuery;
import org.apache.openjpa.kernel.PreparedQueryCache;
import org.apache.openjpa.lib.conf.ConfigurationImpl;
import org.apache.openjpa.lib.log.LogFactoryImpl;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(value= Parameterized.class)
public class TestRemoveExclusionPattern {

    private PreparedQueryCache cache;
    private boolean expected;
    private String input;
    private int initialLenght;
    private boolean forCov;

    @Before
    public void setUp(){
        cache = new PreparedQueryCacheImpl();

        PreparedQuery query = new PreparedQueryImpl("testId",   "testSql",null);

        cache.cache(query);

        cache.endConfiguration();

        cache.setExcludes("testId");
        cache.setExcludes("otherId");
        cache.setExcludes("otherOtherId");
        initialLenght = cache.getExcludes().size();

    }

    @Parameters
    public static Collection<Object[]> getParameters(){
        return Arrays.asList(new Object[][]{
                //expected      forCov,     input
                {true,          true,       "testId"},
                {true,          false,      "testId"},
                {false,         false,      ""},
                {false,         false,      null},
                {false,         false,      "notInList"}
        });
    }

    public TestRemoveExclusionPattern(boolean expected, boolean forCov,String input){
        this.input = input;
        this.expected = expected;
        this.forCov = forCov;
    }

    @Test
    public void testRemoveExclusion(){
        if(forCov){
            //per aumentare coverage
            ConfigurationImpl mockedConf = mock(ConfigurationImpl.class);
            LogFactoryImpl logFactory = new LogFactoryImpl();
            LogFactoryImpl.LogImpl log = (LogFactoryImpl.LogImpl) logFactory.getLog("test");
            log.setLevel((short) 0);
            when(mockedConf.getLog(OpenJPAConfiguration.LOG_RUNTIME)).thenReturn(log);
            cache.setConfiguration(mockedConf);
        }
        try {

            cache.removeExclusionPattern(input);
            boolean result = cache.getExcludes().contains(input);

            if (expected) {
                Assert.assertNotEquals(expected, result);
                int finalLenght = cache.getExcludes().size();
                Assert.assertEquals(initialLenght-1,finalLenght);
            }
            else {
                Assert.assertEquals(expected, result);
                int finalLenght = cache.getExcludes().size();
                Assert.assertEquals(initialLenght,finalLenght);
            }

        }
        catch(Exception e){

            Assert.assertTrue(e instanceof NullPointerException);
            Assert.assertFalse(expected);

            int finalLenght = cache.getExcludes().size();
            Assert.assertEquals(initialLenght,finalLenght);
        }
    }

    @After
    public void tearDown(){
        cache.clear();
    }
}
