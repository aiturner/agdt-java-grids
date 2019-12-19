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
package io.github.agdturner.grids.d2.chunk;

/**
 * For iterating over the values in a chunk in no particular spatial order. 
 * Such iteration can be faster than a particular order of iteration.
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public abstract class Grids_ChunkNumberMapASpatialIterator
        extends Grids_ChunkIterator {

    private static final long serialVersionUID = 1L;

    protected int NumberOfCells;
    protected int DefaultValueIndex;
    protected int NoDataValueIndex;
    protected int DataMapBitSetIndex;
    protected int DataMapBitSetNumberOfValues;
    protected int DataMapHashSetNumberOfValues;
    protected int DataMapHashSetIndex;
    protected boolean hasNext;

    public Grids_ChunkNumberMapASpatialIterator(Grids_Chunk chunk) {
        super(chunk);
        NumberOfCells = chunk.ChunkNRows * chunk.ChunkNCols;
        //NumberOfDefaultValues = chunk.getNumberOfDefaultValues(NumberOfCells);
        DefaultValueIndex = 0;
        hasNext = true;
        //NumberOfNoDataValues = NumberOfCells - NumberOfDefaultValues;
        DataMapBitSetIndex = 0;
        DataMapHashSetIndex = 0;
    }

    /**
     * @return {@code true} if the iterator has more elements.
     */
    @Override
    public boolean hasNext() {
        return hasNext;
    }
}
