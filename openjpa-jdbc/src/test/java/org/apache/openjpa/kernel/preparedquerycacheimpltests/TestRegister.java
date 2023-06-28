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
    private QueryImpl query;
    private FetchConfigurationImpl conf;

    @Before
    public void setUp(){
        cache = new PreparedQueryCacheImpl();

        PreparedQuery query1 = new PreparedQueryImpl("testId",   "testSql",null);
        PreparedQuery query2 = new PreparedQueryImpl("testId2",   "testSql2",null);

        cache.cache(query1);
        cache.cache(query2);

        cache.endConfiguration();

    }


    @Parameters
    public static Collection<Object[]> getParameters(){

        BrokerImpl broker = new BrokerImpl();
        BrokerImpl spy = spy(broker);

        FetchConfigurationImpl mockConf = mock(FetchConfigurationImpl.class);

        when(mockConf.clone()).thenReturn(new FetchConfigurationImpl());

        OpenJPAConfiguration mockJpaConf = mock(OpenJPAConfiguration.class);

        when(mockJpaConf.getLog()).thenReturn(null);
        when(spy.getConfiguration()).thenReturn(mockJpaConf);
        when(spy.getFetchConfiguration()).thenReturn(mockConf);

        StoreQuery mockedQuery = mock(StoreQuery.class);

        QueryImpl query = new QueryImpl(spy,"testLanguage",mockedQuery);

        FetchConfigurationImpl conf1 = new FetchConfigurationImpl();
        conf1.setHint("openjpa.hint.IgnorePreparedQuery","true");
        conf1.setHint("openjpa.hint.InvalidatePreparedQuery","true");

        FetchConfigurationImpl conf2 = new FetchConfigurationImpl();
        conf2.setHint("openjpa.hint.IgnorePreparedQuery","test");
        conf2.setHint("openjpa.hint.InvalidatePreparedQuery","test");

        return Arrays.asList(new Object[][]{
                //expected         id               query       FetchConf
                {null,             "testId",        query,      null},
                {true,             "otherId",       query,      null},
                {true,             "",              query,      null},
                {false,            null,            query,      null},
                {false,            "testId",        null,       null},
                {false,            "otherId",       null,       null},
                {false,            "",              null,       null},
                {false,            null,            null,       null},
                {false,            "testId",        query,      conf1},
                {false,            "otherId",       query,      conf1},
                {false,            "",              query,      conf1},
                {false,            null,            query,      conf1},
                {false,            "testId",        null,       conf1},
                {false,            "otherId",       null,       conf1},
                {false,            "",              null,       conf1},
                {false,            null,            null,       conf1},
                {null,             "testId",        query,      conf2},
                {true,             "otherId",       query,      conf2},
                {true,             "",              query,      conf2},
                {false,            null,            query,      conf2},
                {false,            "testId",        null,       conf2},
                {false,            "otherId",       null,       conf2},
                {false,            "",              null,       conf2},
                {false,            null,            null,       conf2}
        });
    }

    public TestRegister(Object expected, String id, QueryImpl query, FetchConfigurationImpl conf){
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
