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
import org.apache.openjpa.jdbc.conf.JDBCConfigurationImpl;
import org.apache.openjpa.jdbc.kernel.NativeJDBCSeq;
import org.apache.openjpa.jdbc.sql.DBDictionary;
import org.apache.openjpa.kernel.DelegatingStoreManager;
import org.apache.openjpa.kernel.StoreContext;
import org.apache.openjpa.lib.log.Log;
import org.apache.openjpa.meta.ClassMetaData;
import org.junit.Test;

import java.sql.SQLException;

import static java.lang.System.out;
import static org.mockito.Mockito.*;

public class ITAbstractNext {

    private NativeJDBCSeq nativeJDBCSeq;

    @Test
    public void test() throws SQLException {
        nativeJDBCSeq = new NativeJDBCSeq();

        nativeJDBCSeq.setType(2);

        MyJBDBCStoreManagerImpl store = new MyJBDBCStoreManagerImpl();
        ClassMetaData meta = null;

        StoreContext mockedCont = mock(StoreContext.class);
        DelegatingStoreManager mockedDel = mock(DelegatingStoreManager.class);
        JDBCConfigurationImpl mockedConf = mock(JDBCConfigurationImpl.class);
        DBDictionary mockedDict = mock(DBDictionary.class);
        Log mockedLog = mock(Log.class);

        store.setWhens();
        when(mockedCont.getStoreManager()).thenReturn(mockedDel);
        when(mockedDel.getInnermostDelegate()).thenReturn(store);
        when(mockedConf.getDBDictionaryInstance()).thenReturn(mockedDict);
        when(mockedConf.getLog(OpenJPAConfiguration.LOG_RUNTIME)).thenReturn(mockedLog);
        when(mockedLog.isWarnEnabled()).thenReturn(true);


        nativeJDBCSeq.setConfiguration(mockedConf);

        Object bho = nativeJDBCSeq.next(mockedCont,meta);
        out.println(bho.toString());

        verify(mockedCont).getStoreManager();
        verify(mockedDel).getInnermostDelegate();
        verify(mockedConf,times(3)).getDBDictionaryInstance();
        verify(mockedConf).getLog(OpenJPAConfiguration.LOG_RUNTIME);
        verify(mockedLog).isWarnEnabled();
        store.verifyMocks();

    }
}
