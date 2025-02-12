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
import org.apache.openjpa.jdbc.kernel.JDBCFetchConfigurationImpl;
import org.apache.openjpa.jdbc.kernel.PreparedQueryCacheImpl;
import org.apache.openjpa.jdbc.kernel.PreparedQueryImpl;
import org.apache.openjpa.kernel.*;
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
import static org.mockito.Mockito.*;

@RunWith(value= Parameterized.class)
public class TestRegister {


    private PreparedQueryCache cache;
    private Object expected;
    private String id;
    private Query query;
    private FetchConfiguration conf;

    @Before
    public void setUp(){
        cache = new PreparedQueryCacheImpl();

        PreparedQuery query = new PreparedQueryImpl("testId",   "testSql",null);

        cache.cache(query);

        cache.endConfiguration();

        //per pit
        cache.markUncachable("uncachable",null);

    }


    @Parameters
    public static Collection<Object[]> getParameters(){

        BrokerImpl broker = new BrokerImpl();
        BrokerImpl spy = spy(broker);
        OpenJPAConfiguration mockJpaConf = mock(OpenJPAConfiguration.class);
        FetchConfigurationImpl mockConf = mock(FetchConfigurationImpl.class);
        StoreQuery mockedQuery = mock(StoreQuery.class);

        when(mockConf.clone()).thenReturn(new FetchConfigurationImpl());
        when(mockJpaConf.getLog()).thenReturn(null);
        when(spy.getConfiguration()).thenReturn(mockJpaConf);
        when(spy.getFetchConfiguration()).thenReturn(mockConf);

        Query query = new QueryImpl(spy,"testLanguage",mockedQuery);
        Query delQuery = new DelegatingQuery(query);

        FetchConfiguration jdbcConf = new JDBCFetchConfigurationImpl();
        FetchConfigurationImpl fetchImpl = new FetchConfigurationImpl();
        FetchConfigurationImpl fetchImpl2 = new FetchConfigurationImpl();
        FetchConfiguration fetchDel = new DelegatingFetchConfiguration(fetchImpl);

        //per aumentare coverage
        FetchConfigurationImpl fetchImpl3 = new FetchConfigurationImpl();
        fetchImpl2.setHint("openjpa.hint.IgnorePreparedQuery","true");
        fetchImpl3.setHint("openjpa.hint.InvalidatePreparedQuery","true");
        Query lateQuery1 = new QueryImpl(spy,"openjpa.SQL",mockedQuery);
        Query lateQuery2 = new QueryImpl(spy,"openjpa.MethodQL",mockedQuery);


        return Arrays.asList(new Object[][]{
                //expected         id               query           FetchConf
                {null,             "testId",        query,          null},
                {true,             "notInCache",    query,          jdbcConf},
                {true,             "",              query,          fetchImpl},
                {false,            null,            query,          fetchDel},
                {false,            "testId",        null,           fetchImpl},
                {false,            "notInCache",    null,           jdbcConf},
                {false,            "",              null,           fetchDel},
                {false,            null,            null,           null},
                {null,             "testId",        delQuery,       jdbcConf},
                {true,             "notInCache",    delQuery,       fetchDel},
                {true,             "",              delQuery,       fetchImpl},
                {false,            null,            delQuery,       fetchImpl},

                //per aumentare coverage
                {false,            "testid",        query,          fetchImpl2},
                {false,            "testid",        query,          fetchImpl3},
                {false,            "testid",        lateQuery1,     fetchImpl3},
                {false,            "testid",        lateQuery2,     fetchImpl3},
                //per pit
                {false,            "uncachable",    query,          fetchImpl}
        });
    }

    public TestRegister(Object expected, String id, Query query, FetchConfiguration conf){
        this.id = id;
        this.expected = expected;
        this.conf = conf;
        this.query = query;
    }

    @Test
    public void testRemoveExclusion(){
        try {
            Object res = cache.register(id, query, conf);
            Assert.assertEquals(expected,res);
        }
        catch(Exception e){
            //non dovrei entrare qui dentro
            Assert.assertTrue(e instanceof NullPointerException);
        }
    }

    @After
    public void tearDown(){
        cache.clear();
    }
}
