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
import org.junit.Test;

import java.sql.SQLException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ITRefreshSequence {

    @Test
    public void testRefreshSequence() throws SQLException {

        NativeJDBCSeq nativeJDBCSeq = new NativeJDBCSeq();

        JDBCConfiguration mockConf = mock(JDBCConfiguration.class);
        Log mockedLog = mock(Log.class);
        DBDictionary mockedDict = mock(DBDictionary.class);

        when(mockConf.getLog(OpenJPAConfiguration.LOG_RUNTIME)).thenReturn(mockedLog);
        when(mockedLog.isInfoEnabled()).thenReturn(true);
        when(mockConf.getDataSource2(null)).thenReturn(null);
        when(mockConf.getLog(JDBCConfiguration.LOG_SCHEMA)).thenReturn(mockedLog);
        when(mockedDict.getCreateSequenceSQL(any())).thenReturn(new String[0]);
        when(mockConf.getDBDictionaryInstance()).thenReturn(mockedDict);

        nativeJDBCSeq.setConfiguration(mockConf);
        nativeJDBCSeq.refreshSequence();
    }

}
