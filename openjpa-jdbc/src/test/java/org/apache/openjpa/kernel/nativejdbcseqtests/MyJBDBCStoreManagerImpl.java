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

import org.apache.openjpa.jdbc.conf.JDBCConfiguration;
import org.apache.openjpa.jdbc.kernel.JDBCStoreManager;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import static org.mockito.Mockito.*;

public class MyJBDBCStoreManagerImpl extends JDBCStoreManager {
    private Connection mockedConn = mock(Connection.class);
    private PreparedStatement mockedStat = mock(PreparedStatement.class);
    private ResultSet mockedRes = mock(ResultSet.class);
    private JDBCConfiguration mockedConf = mock(JDBCConfiguration.class);
    private DataSource mockedSource= mock(DataSource.class);

    public Connection getConnection() {
        return mockedConn;
    }

    public void setWhens() throws SQLException {
        when(mockedRes.next()).thenReturn(true);
        when(mockedRes.getLong(1)).thenReturn((long) 10000);
        when(mockedStat.executeQuery()).thenReturn(mockedRes);
        when(mockedConn.prepareStatement(null)).thenReturn(mockedStat);
        doNothing().when(mockedConn).close();
    }

    public void verifyMocks() throws SQLException {
        verify(mockedRes).next();
        verify(mockedRes).getLong(1);
        verify(mockedStat).executeQuery();
        verify(mockedConn).prepareStatement(null);
        verify(mockedConn).close();

        verify(mockedConf).getDataSource2(any());
        verify(mockedSource).getConnection();
    }

    public JDBCConfiguration getConfiguration(){

        when(mockedConf.getDataSource2(any())).thenReturn(mockedSource);
        Connection mockedConn = mock(Connection.class);

        try {
            when(mockedSource.getConnection()).thenReturn(mockedConn);
        } catch (SQLException e) {
            //Do nothing
        }

        return mockedConf;
    }
}
