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
import org.apache.openjpa.jdbc.kernel.SelectResultObjectProvider;
import org.apache.openjpa.jdbc.sql.DBDictionary;
import org.apache.openjpa.jdbc.sql.SQLBuffer;
import org.apache.openjpa.jdbc.sql.SelectImpl;
import org.apache.openjpa.kernel.*;
import org.apache.openjpa.kernel.exps.QueryExpressions;
import org.apache.openjpa.kernel.exps.Value;
import org.apache.openjpa.lib.rop.ListResultList;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import java.util.Arrays;
import java.util.Collection;

import static org.mockito.Mockito.*;

@RunWith(value= Parameterized.class)
public class TestInitialize {

    private String id;
    private boolean isNull;
    private boolean expected;
    private PreparedQueryCache cache;


    @Parameters
    public static Collection<Object[]> getParameters(){
        return Arrays.asList(new Object[][]{
                //expected  id          isNull
                {true,      "testId",   true},
                {true,      "",         true},
                {true,      null,       true},
                {true,      "testId",   false},
                {true,      "",         false},
                {false,     null,       false},
        });
    }


    @Before
    public void setUp(){
        cache = new PreparedQueryCacheImpl();

        PreparedQuery query1 = new PreparedQueryImpl("testId",   "testSql",null);
        PreparedQuery query2 = new PreparedQueryImpl("","",null);

        cache.cache(query1);
        cache.cache(query2);

        cache.endConfiguration();
    }


    public TestInitialize(boolean expected,String id,boolean isNull){
        this.expected = expected;
        this.id = id;
        this.isNull = isNull;
    }


    //test aggiunto per aumentare il coverage
    @Test
    public void testPositive() {


        if(isNull){
            ListResultList testResultList = null;
            try {
                cache.initialize(id, testResultList);
            }
            catch(Exception e){
                Assert.assertTrue(e instanceof NullPointerException);
            }

        }
        else{
            ListResultList testPositiveResultList = new ListResultList(null);

            Object[] objects = new Object[2];

            DBDictionary mockedDict = mock(DBDictionary.class);

            //first element of objects
            SelectResultObjectProvider mockedProvider = mock(SelectResultObjectProvider.class);
            SelectImpl mockedSelectExecutor = mock(SelectImpl.class);
            when(mockedSelectExecutor.getStartIndex()).thenReturn((long) 0);
            when(mockedSelectExecutor.getEndIndex()).thenReturn(Long.MAX_VALUE);
            when(mockedSelectExecutor.hasMultipleSelects()).thenReturn(false);
            SQLBuffer mockedBuffer = new SQLBuffer(mockedDict);
            when(mockedSelectExecutor.getSQL()).thenReturn(mockedBuffer);
            when(mockedProvider.getSelect()).thenReturn(mockedSelectExecutor);

            objects[0] = mockedProvider;


            //second element of objects
            StoreQuery.Executor mockedExecutor = mock(StoreQuery.Executor.class);
            QueryExpressions queryExpressions = new QueryExpressions();
            Value mockedValue = mock(Value.class);
            Value[] valueArray = new Value[1];
            valueArray[0] = mockedValue;
            QueryExpressions[] testQuery = new QueryExpressions[1];
            testQuery[0] = queryExpressions;
            when(mockedExecutor.getQueryExpressions()).thenReturn(testQuery);

            objects[1] = mockedExecutor;


            testPositiveResultList.setUserObject(objects);

            PreparedQuery resultQuery = cache.initialize(id,testPositiveResultList);
            if(expected)
                Assert.assertNotNull(resultQuery);
            else
                Assert.assertNull(resultQuery);
        }
    }

    @After
    public void tearDown(){
        cache.clear();
    }
}
