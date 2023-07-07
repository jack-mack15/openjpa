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

package org.apache.openjpa.kernel.nativejdbcseqtests;

import org.apache.openjpa.conf.OpenJPAConfiguration;
import org.apache.openjpa.jdbc.conf.JDBCConfiguration;
import org.apache.openjpa.jdbc.kernel.NativeJDBCSeq;
import org.apache.openjpa.jdbc.sql.DBDictionary;
import org.apache.openjpa.lib.log.Log;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ITRefreshDropSequence {

    private JDBCConfiguration mockConf;
    private Log mockedLog;
    private DBDictionary mockedDict;
    private NativeJDBCSeq nativeJDBCSeq;

    @Before
    public void setUp(){
        mockConf = mock(JDBCConfiguration.class);
        mockedLog = mock(Log.class);
        mockedDict = mock(DBDictionary.class);

        when(mockConf.getLog(OpenJPAConfiguration.LOG_RUNTIME)).thenReturn(mockedLog);
        when(mockedLog.isInfoEnabled()).thenReturn(true);
        when(mockConf.getDataSource2(null)).thenReturn(null);
        when(mockConf.getLog(JDBCConfiguration.LOG_SCHEMA)).thenReturn(mockedLog);
        when(mockedDict.getCreateSequenceSQL(any())).thenReturn(new String[0]);
        when(mockedDict.getDropSequenceSQL(any())).thenReturn(new String[0]);
        when(mockConf.getDBDictionaryInstance()).thenReturn(mockedDict);

        nativeJDBCSeq = new NativeJDBCSeq();
        nativeJDBCSeq.setConfiguration(mockConf);
    }


    @Test
    public void testRefreshSequence() throws SQLException {

        nativeJDBCSeq.refreshSequence();

        verifyMocks();
        verify(mockedDict).getCreateSequenceSQL(any());
    }

    @Test
    public void testDropSequence() throws SQLException {

        nativeJDBCSeq.dropSequence();

        verifyMocks();
        verify(mockedDict).getDropSequenceSQL(any());

    }

    private void verifyMocks(){
        verify(mockConf).getLog(OpenJPAConfiguration.LOG_RUNTIME);
        verify(mockConf).getDataSource2(null);
        verify(mockConf).getLog(JDBCConfiguration.LOG_SCHEMA);
        verify(mockConf).getDBDictionaryInstance();

        verify(mockedLog).isInfoEnabled();
    }

}
