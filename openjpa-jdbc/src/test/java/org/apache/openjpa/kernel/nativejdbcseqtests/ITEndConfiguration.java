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
import org.apache.openjpa.jdbc.identifier.QualifiedDBIdentifier;
import org.apache.openjpa.jdbc.kernel.NativeJDBCSeq;
import org.apache.openjpa.jdbc.schema.Schemas;
import org.apache.openjpa.jdbc.schema.Sequence;
import org.apache.openjpa.jdbc.sql.DBDictionary;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ITEndConfiguration {

    @Test
    public void testEndConfiguration() {
        NativeJDBCSeq nativeJDBCSeq = new NativeJDBCSeq();
        nativeJDBCSeq.setSequence("testSequence");

        JDBCConfiguration mockConf = mock(JDBCConfiguration.class);
        DBDictionary mockDict = mock(DBDictionary.class);
        MockedStatic<Schemas> mockStaticSchemas = Mockito.mockStatic(Schemas.class);

        mockDict.nextSequenceQuery = "test";
        mockStaticSchemas.when(() -> Schemas.getNewTableSchemaIdentifier(mockConf))
                .thenReturn(null);
        when(mockDict.getFullName(any(Sequence.class))).thenReturn("test");
        when(mockConf.getDBDictionaryInstance()).thenReturn(mockDict);

        nativeJDBCSeq.setConfiguration(mockConf);

        nativeJDBCSeq.endConfiguration();

        verify(mockConf).getDBDictionaryInstance();

        verify(mockDict).getFullName(any(Sequence.class));

        mockStaticSchemas.verify(
                () -> Schemas.getNewTableSchemaIdentifier(mockConf),
                times(1)
        );

        mockStaticSchemas.close();
    }

}
