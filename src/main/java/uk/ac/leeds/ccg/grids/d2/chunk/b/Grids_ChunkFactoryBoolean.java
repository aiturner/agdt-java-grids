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
package uk.ac.leeds.ccg.grids.d2.chunk.b;

import uk.ac.leeds.ccg.grids.d2.Grids_2D_ID_int;
import uk.ac.leeds.ccg.grids.d2.chunk.Grids_ChunkFactory;
import uk.ac.leeds.ccg.grids.d2.grid.b.Grids_GridBoolean;

/**
 * A factory for constructing Grids_AbstractGridChunkDouble instances.
*
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_ChunkFactoryBoolean extends Grids_ChunkFactory {

    private static final long serialVersionUID = 1L;

    public Grids_ChunkFactoryBoolean() {
    }

    public Grids_ChunkBooleanArray create(Grids_GridBoolean grid,
            Grids_2D_ID_int chunkID) {
        return new Grids_ChunkBooleanArray(grid, chunkID);
    }

    public Grids_ChunkBooleanArray create(Grids_ChunkBooleanArray chunk,
            Grids_2D_ID_int chunkID) {
        return new Grids_ChunkBooleanArray(chunk.getGrid(), chunkID);
    }

}