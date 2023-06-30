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
import org.apache.openjpa.jdbc.kernel.NativeJDBCSeq;
import org.apache.openjpa.jdbc.meta.ClassMapping;
import org.apache.openjpa.kernel.StoreContext;
import org.apache.openjpa.meta.ClassMetaData;
import org.junit.Test;

import static org.mockito.Mockito.mock;

public class ITAbstractNext {

    private NativeJDBCSeq nativeJDBCSeq;

    @Test
    public void test(){
        nativeJDBCSeq = new NativeJDBCSeq();
        JDBCConfigurationImpl conf = new JDBCConfigurationImpl();
        nativeJDBCSeq.setConfiguration(conf);

        StoreContext mockedCont = mock(StoreContext.class);
        ClassMetaData mockedMeta = mock(ClassMetaData.class);

        //StoreContext

        //nativeJDBCSeq.next(mockedCont,mockedMeta);
    }
}
