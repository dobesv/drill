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
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.drill.exec.store.hdf5.writers;

import ch.systemsx.cisd.hdf5.IHDF5Reader;
import org.apache.drill.common.types.TypeProtos;
import org.apache.drill.exec.physical.resultSet.RowSetLoader;
import org.apache.drill.exec.store.hdf5.HDF5Utils;
import org.apache.drill.exec.vector.accessor.ScalarWriter;

import java.util.Arrays;
import java.util.List;

public class HDF5StringDataWriter extends HDF5DataWriter {

  private String[] data;

  private final List<String> listData;

  private final ScalarWriter rowWriter;

  // This constructor is used when the data is a 1D column.  The column is inferred from the datapath
  public HDF5StringDataWriter(IHDF5Reader reader, RowSetLoader columnWriter, String datapath) {
    super(reader, columnWriter, datapath);
    data = reader.readStringArray(datapath);
    listData = Arrays.asList(data);
    fieldName = HDF5Utils.getNameFromPath(datapath);
    rowWriter = makeWriter(columnWriter, fieldName, TypeProtos.MinorType.VARCHAR, TypeProtos.DataMode.OPTIONAL);
  }

  public HDF5StringDataWriter(IHDF5Reader reader, RowSetLoader columnWriter, String fieldName, List<String> data) {
    super(reader, columnWriter, null);
    this.fieldName = fieldName;
    this.listData = data;
    rowWriter = makeWriter(columnWriter, fieldName, TypeProtos.MinorType.VARCHAR, TypeProtos.DataMode.OPTIONAL);
  }

  public boolean write() {
    if (counter > listData.size()) {
      return false;
    } else {
      rowWriter.setString(listData.get(counter++));
      return true;
    }
  }

  public boolean hasNext() {
    return counter < data.length;
  }

  @Override
  public int getDataSize() {
    return listData.size();
  }
}
