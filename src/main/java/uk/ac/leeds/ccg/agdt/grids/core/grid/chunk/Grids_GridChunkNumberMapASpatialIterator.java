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
package uk.ac.leeds.ccg.agdt.grids.core.grid.chunk;

/**
 * For iterating through the values in a Grids_GridChunkNumberMap instance. The
 * values are returned in no particular spatial order.
*
 * @author Andy Turner
 * @version 1.0.0
 */
public abstract class Grids_GridChunkNumberMapASpatialIterator
        extends Grids_AbstractChunkIterator {

    private static final long serialVersionUID = 1L;

    protected int NumberOfCells;
    protected int DefaultValueIndex;
    protected int NoDataValueIndex;
    protected int DataMapBitSetIndex;
    protected int DataMapBitSetNumberOfValues;
    protected int DataMapHashSetNumberOfValues;
    protected int DataMapHashSetIndex;
    protected boolean hasNext;

    public Grids_GridChunkNumberMapASpatialIterator(
            Grids_AbstractGridChunk chunk) {
        super(chunk);
                NumberOfCells = chunk.ChunkNRows * chunk.ChunkNCols;
        NumberOfCells = chunk.ChunkNRows * chunk.ChunkNCols;
        //NumberOfDefaultValues = chunk.getNumberOfDefaultValues(NumberOfCells);
        DefaultValueIndex = 0;
        hasNext = true;
        //NumberOfNoDataValues = NumberOfCells - NumberOfDefaultValues;
        DataMapBitSetIndex = 0;
        DataMapHashSetIndex = 0;
    }

    /**
     * Returns <tt>true</tt> if the iteration has more elements. (In other
     * words, returns <tt>true</tt> if <tt>next</tt> would return an element
     * rather than throwing an exception.)
     *
     * @return <tt>true</tt> if the iterator has more elements.
     */
    public boolean hasNext() {
        return hasNext;
    }
}
