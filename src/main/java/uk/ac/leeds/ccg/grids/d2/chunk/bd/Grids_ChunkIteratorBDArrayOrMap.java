/*
 * Copyright 2019 Andy Turner, University of Leeds.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.leeds.ccg.grids.d2.chunk.bd;

import java.math.BigDecimal;
import uk.ac.leeds.ccg.grids.d2.chunk.Grids_ChunkRowMajorOrderIterator;
import uk.ac.leeds.ccg.grids.d2.grid.bd.Grids_GridBD;

/**
 * For iterating through the values in a Grids_GridChunkBDArray instance.
 * The values are not returned in any particular order.
*
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_ChunkIteratorBDArrayOrMap
        extends Grids_ChunkRowMajorOrderIterator {

    private static final long serialVersionUID = 1L;

    protected BigDecimal[][] data;

    public Grids_ChunkIteratorBDArrayOrMap(Grids_ChunkBDArray chunk) {
        super(chunk);
        data = chunk.getData();
    }

    public Grids_ChunkIteratorBDArrayOrMap(Grids_ChunkBDMap chunk) {
        super(chunk);
        data = chunk.to2DBDArray();
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration.
     */
    public BigDecimal next() {
        next0();
        return data[row][col];
    }

    public void remove() {
        data[row][col] = ((Grids_GridBD) grid).getNoDataValue();
    }
}
