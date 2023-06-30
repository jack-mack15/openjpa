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
import org.apache.openjpa.jdbc.conf.JDBCConfigurationImpl;
import org.apache.openjpa.jdbc.identifier.DBIdentifier;
import org.apache.openjpa.jdbc.identifier.QualifiedDBIdentifier;
import org.apache.openjpa.jdbc.kernel.NativeJDBCSeq;
import org.apache.openjpa.jdbc.meta.ClassMapping;
import org.apache.openjpa.jdbc.schema.Schema;
import org.apache.openjpa.jdbc.schema.SchemaGroup;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ITAddSchema {
    private NativeJDBCSeq nativeJDBCSeq;
    @Before
    public void setUp(){
        nativeJDBCSeq = new NativeJDBCSeq();
        JDBCConfigurationImpl conf = new JDBCConfigurationImpl();
        nativeJDBCSeq.setConfiguration(conf);
    }
    @Test
    public void mockedTest(){

        ClassMapping mockedMap = mock(ClassMapping.class);
        SchemaGroup mockedGroup = mock(SchemaGroup.class);
        Schema mockedSchema = mock(Schema.class);
        QualifiedDBIdentifier mockPath = mock(QualifiedDBIdentifier.class);
        MockedStatic<QualifiedDBIdentifier> mockedStaticId = Mockito.mockStatic(QualifiedDBIdentifier.class);

        when(mockPath.getSchemaName()).thenReturn(null);
        mockedStaticId.when(() -> QualifiedDBIdentifier.getPath(any(DBIdentifier.class)))
                .thenReturn(mockPath);
        when(mockedGroup.isKnownSequence(any(QualifiedDBIdentifier.class))).thenReturn(false);
        when(mockedGroup.getSchema(any(DBIdentifier.class))).thenReturn(null);
        when(mockedGroup.addSchema(any(DBIdentifier.class))).thenReturn(mockedSchema);
        when(mockedSchema.importSequence(null)).thenReturn(null);


        nativeJDBCSeq.addSchema(mockedMap,mockedGroup);

        verify(mockPath).getSchemaName();
        mockedStaticId.verify(
                () -> QualifiedDBIdentifier.getPath(any(DBIdentifier.class)),
                times(1)
        );
        verify(mockedGroup).isKnownSequence(any(QualifiedDBIdentifier.class));
        verify(mockedGroup).getSchema(any(DBIdentifier.class));
        verify(mockedGroup).addSchema(any(DBIdentifier.class));
        verify(mockedSchema).importSequence(null);

        mockedStaticId.close();
    }


    @Test
    public void testAddSchema() {

        ClassMapping classMapping = null;
        SchemaGroup schemaGroup = new SchemaGroup();

        nativeJDBCSeq.addSchema(classMapping,schemaGroup);

        Schema schema = schemaGroup.getSchema((String) null);
        Assert.assertNotNull(schema);

        nativeJDBCSeq.setSchema("test");

        nativeJDBCSeq.addSchema(classMapping,schemaGroup);

        schema = schemaGroup.getSchema("test");
        Assert.assertNotNull(schema);

    }

}
