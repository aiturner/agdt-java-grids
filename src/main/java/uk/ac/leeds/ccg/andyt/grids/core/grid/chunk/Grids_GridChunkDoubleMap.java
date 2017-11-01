/**
 * Version 1.0 is to handle single variable 2DSquareCelled raster data.
 * Copyright (C) 2005 Andy Turner, CCG, University of Leeds, UK.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.
 */
package uk.ac.leeds.ccg.andyt.grids.core.grid.chunk;

import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridDouble;
import gnu.trove.TDoubleHashSet;
import gnu.trove.TDoubleObjectHashMap;
import gnu.trove.TDoubleObjectIterator;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
//import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeMap;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_2D_ID_int;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.utilities.Grids_AbstractIterator;

/**
 * Grids_AbstractGridChunkDouble extension that stores cell values in a
 * TDoubleObjectHashMap.
 */
public class Grids_GridChunkDoubleMap
        extends Grids_AbstractGridChunkDouble
        implements Serializable {

    //private static final long serialVersionUID = 1L;
    /**
     * A value initialised with grid that can be used to optimise storage.
     * Storage is optimised with the defaultValue set to the most common value.
     * By default the defaultValue is set to
     * this._Grid2DSquareCell.getNoDataValue().
     */
    private double defaultValue;

    /**
     * For storing values mapped to a Grids_2D_ID_int HashSet or an individual
     * Grids_2D_ID_int.
     */
    private TreeMap<Double, OffsetBitSet> data;

    /**
     * Default constructor.
     */
    protected Grids_GridChunkDoubleMap() {
    }

    /**
     * Creates a new Grid2DSquareCellDoubleChunkMap
     *
     * @param g
     * @param chunkID Default: default value to
     * grid2DSquareCellDouble.getNoDataValue()
     */
    protected Grids_GridChunkDoubleMap(
            Grids_GridDouble g,
            Grids_2D_ID_int chunkID) {
        this(
                g,
                chunkID,
                g.getNoDataValue(false));
    }

    /**
     * Creates a new Grid2DSquareCellDoubleChunkMap
     *
     * @param g
     * @param chunkID
     * @param defaultValue
     */
    protected Grids_GridChunkDoubleMap(
            Grids_GridDouble g,
            Grids_2D_ID_int chunkID,
            double defaultValue) {
        this.ChunkID = chunkID;
        initGrid(g);
        this.defaultValue = defaultValue;
        initData();
        this.SwapUpToDate = false;
    }

    /**
     * Creates a new Grid2DSquareCellDoubleChunkMap
     *
     * @param gridChunk
     * @param chunkID Default: default value to
     * grid2DSquareCellDouble.getNoDataValue() TODO: Optimise for different
     * types.
     * @param defaultValue
     */
    protected Grids_GridChunkDoubleMap(
            Grids_AbstractGridChunkDouble gridChunk,
            Grids_2D_ID_int chunkID,
            double defaultValue) {
        boolean handleOutOfMemoryError = false;
        this.ChunkID = chunkID;
        this.defaultValue = defaultValue;
        Grids_GridDouble g;
        g = gridChunk.getGrid();
        initGrid(g);
        int chunkNrows = g.getChunkNRows(ChunkID, handleOutOfMemoryError);
        int chunkNcols = g.getChunkNCols(ChunkID, handleOutOfMemoryError);
        initData();
        double value;
        for (int row = 0; row < chunkNrows; row++) {
            for (int col = 0; col < chunkNcols; col++) {
                value = gridChunk.getCell(
                        row,
                        col,
                        defaultValue,
                        handleOutOfMemoryError);
                if (value != defaultValue) {
                    initCell(
                            row,
                            col,
                            value);
                }
            }
        }
        this.SwapUpToDate = false;
    }

    /**
     * Initialises the data associated with this.
     */
    @Override
    protected final void initData() {
        data = new TreeMap<>();
    }

    /**
     * Returns this.data TODO: This could be made public if a copy is returned!
     *
     * @return
     */
    protected TreeMap<Double, OffsetBitSet> getData() {
        return data;
    }

    /**
     * Clears the data associated with this.
     */
    protected @Override
    void clearData() {
        data = null;
        System.gc();
    }

    /**
     * Returns values in row major order as a double[].
     *
     * @return
     */
    double[][] to2DDoubleArray() {
        Grids_GridDouble grid = getGrid();
        int nrows = grid.getChunkNRows(ChunkID, Grid.ge.HandleOutOfMemoryErrorFalse);
        int ncols = grid.getChunkNCols(ChunkID, Grid.ge.HandleOutOfMemoryErrorFalse);
        double[][] result;
        result = new double[nrows][ncols];
        Arrays.fill(result, grid.getNoDataValue(Grid.ge.HandleOutOfMemoryErrorFalse));
        Iterator<Double> ite = data.keySet().iterator();
        Double value;
        OffsetBitSet offsetBitSet;
        BitSet bitSet;
        int bitSetLength;
        int i;
        int row = 0;
        int col = 0;
        while (ite.hasNext()) {
            value = ite.next();
            offsetBitSet = data.get(value);
            bitSetLength = offsetBitSet._BitSet.length();
            for (i = 0; i < offsetBitSet.Offset; i++) {
                col++;
                if (col == ncols - 1) {
                    row++;
                    col = 0;
                }
            }
            for (i = offsetBitSet.Offset; i < bitSetLength + offsetBitSet.Offset; i++) {
                col++;
                if (col == ncols - 1) {
                    row++;
                    col = 0;
                }
                if (offsetBitSet._BitSet.get(i)) {
                    result[row][col] = value;
                }
            }
        }
        return result;
    }

    /**
     * Returns values in row major order as a double[].
     *
     * @return
     */
    protected @Override
    double[] toArrayIncludingNoDataValues() {
        Grids_GridDouble grid = getGrid();
        int nrows = grid.getChunkNRows(ChunkID, Grid.ge.HandleOutOfMemoryErrorFalse);
        int ncols = grid.getChunkNCols(ChunkID, Grid.ge.HandleOutOfMemoryErrorFalse);
        double[] result;
        result = new double[nrows * ncols];
        Arrays.fill(result, grid.getNoDataValue(Grid.ge.HandleOutOfMemoryErrorFalse));
        Iterator<Double> ite = data.keySet().iterator();
        Double value;
        OffsetBitSet offSetBitSet;
        int bitSetLength;
        int i;
        while (ite.hasNext()) {
            value = ite.next();
            offSetBitSet = data.get(value);
            bitSetLength = offSetBitSet._BitSet.length();
            for (i = offSetBitSet.Offset; i < bitSetLength + offSetBitSet.Offset; i++) {
                if (offSetBitSet._BitSet.get(i)) {
                    result[i] = value;
                }
            }
        }
        return result;
    }

    /**
     * @return
     */
    protected @Override
    double[] toArrayNotIncludingNoDataValues() {
        Grids_GridDouble grid = getGrid();
        int nrows = grid.getChunkNRows(ChunkID, Grid.ge.HandleOutOfMemoryErrorFalse);
        int ncols = grid.getChunkNCols(ChunkID, Grid.ge.HandleOutOfMemoryErrorFalse);
        double[] result;
        result = new double[nrows * ncols];
        Arrays.fill(result, grid.getNoDataValue(Grid.ge.HandleOutOfMemoryErrorFalse));
        Iterator<Double> ite = data.keySet().iterator();
        Double value;
        OffsetBitSet offSetBitSet;
        int i;
        while (ite.hasNext()) {
            value = ite.next();
            offSetBitSet = data.get(value);
            for (i = 0; i < offSetBitSet._BitSet.cardinality(); i++) {
                result[i] = value;
            }
        }
        return result;
    }

    /**
     * Returns the value at position given by: chunk cell row chunkCellRowIndex;
     * chunk cell row chunkCellColIndex.
     *
     * @param chunkCellRowIndex the row index of the cell w.r.t. the origin of
     * this chunk
     * @param chunkCellColIndex the column index of the cell w.r.t. the origin
     * of this chunk
     * @param noDataValue the _NoDataValue of this.grid2DSquareCellDouble
     * @return
     */
    protected @Override
    double getCell(
            int chunkCellRowIndex,
            int chunkCellColIndex,
            double noDataValue) {
        double result = noDataValue;
        Iterator<Double> ite;
        ite = data.keySet().iterator();
        double value;
        int position = chunkCellRowIndex * Grid.ChunkNCols;
        while (ite.hasNext()) {
            value = ite.next();
            
        }
        Grids_2D_ID_int chunkCellID = new Grids_2D_ID_int(
                chunkCellRowIndex,
                chunkCellColIndex);
        return getCell(
                chunkCellID,
                noDataValue);
    }

    /**
     * Returns the value of cell with CellID given by chunkCellID.
     *
     * @param chunkCellID The chunk CellID of cell thats value is returned.
     * @param noDataValue The Grid.NoDataValue.
     * @return
     */
    protected double getCell(
            Grids_2D_ID_int chunkCellID,
            double noDataValue) {
        
        TDoubleObjectIterator iterator = this.data.iterator();
        HashSet set;
        Grids_2D_ID_int individual;
        int ite;
        for (ite = 0; ite < this.data.size(); ite++) {
            iterator.advance();
            try {
                set = (HashSet) iterator.value();
                if (set.contains(chunkCellID)) {
                    return iterator.key();
                }
            } catch (java.lang.ClassCastException e) {
                individual = (Grids_2D_ID_int) iterator.value();
                if (individual.equals(chunkCellID)) {
                    return iterator.key();
                }
            }
        }
        return this.defaultValue;
    }

    /**
     * Initialises the value at position given by: chunk cell row
     * chunkCellRowIndex; chunk cell column chunkCellColIndex. Utility method
     * for constructor.
     *
     * @param chunkCellRowIndex the row index of the cell w.r.t. the origin of
     * this chunk
     * @param chunkCellColIndex the column index of the cell w.r.t. the origin
     * of this chunk
     * @param valueToInitialise the value with which the cell is initialised
     */
    @Override
    protected final void initCell(
            int chunkCellRowIndex,
            int chunkCellColIndex,
            double valueToInitialise) {
        Grids_2D_ID_int chunkCellID = new Grids_2D_ID_int(
                chunkCellRowIndex,
                chunkCellColIndex);
        initCell(
                chunkCellID,
                valueToInitialise);
    }

    /**
     * Initialises the value of the chunk referred to by chunkCellID to
     * valueToInitialise. Utility method for constructor.
     *
     *
     * @param chunkCellID the Grids_AbstractGridChunkDouble.Grids_2D_ID_int of
     * the cell to be initialised
     * @param valueToInitialise the value with which the cell is initialised
     */
    protected void initCell(
            Grids_2D_ID_int chunkCellID,
            double valueToInitialise) {
        if (this.data.containsKey(valueToInitialise)) {
            HashSet set;
            try {
                set = (HashSet) this.data.get(valueToInitialise);
                set.add(chunkCellID);
            } catch (java.lang.ClassCastException e) {
                Grids_2D_ID_int individual
                        = (Grids_2D_ID_int) this.data.get(valueToInitialise);
                set = new HashSet();
                set.add(individual);
                set.add(chunkCellID);
                this.data.put(
                        valueToInitialise,
                        set);
            }
        } else {
            this.data.put(
                    valueToInitialise,
                    chunkCellID);
        }
    }

    /**
     * Returns the value at position given by: chunk cell row chunkCellRowIndex;
     * chunk cell column chunkCellColIndex and sets it to valueToSet
     *
     * @param chunkCellRowIndex the row index of the cell w.r.t. the origin of
     * this chunk
     * @param chunkCellColIndex the column index of the cell w.r.t. the origin
     * of this chunk
     * @param valueToSet the value the cell is to be set to
     * @param _NoDataValue the _NoDataValue of this.grid2DSquareCellDouble
     * @return
     */
    protected @Override
    double setCell(
            int chunkCellRowIndex,
            int chunkCellColIndex,
            double valueToSet,
            double _NoDataValue) {
        Grids_2D_ID_int chunkCellID = new Grids_2D_ID_int(
                chunkCellRowIndex,
                chunkCellColIndex);
        return setCell(
                chunkCellID,
                valueToSet,
                _NoDataValue);
    }

    /**
     * Returns the value at position given by: chunk cell row chunkCellRowIndex;
     * chunk cell column chunkCellColIndex and sets it to valueToSet
     *
     *
     *
     * @param chunkCellID the Grids_AbstractGridChunkDouble.Grids_2D_ID_int of
     * the cell to be initialised
     * @param valueToSet the value the cell is to be set to
     * @param _NoDataValue the _NoDataValue of this.grid2DSquareCellDouble
     * @return
     */
    protected double setCell(
            Grids_2D_ID_int chunkCellID,
            double valueToSet,
            double _NoDataValue) {
        double result = this.defaultValue;
        TDoubleObjectIterator iterator = this.data.iterator();
        double value = _NoDataValue;
        boolean gotValue = false;
        boolean setValue;
        setValue = valueToSet == result;
        int ite;
        int maxIte = this.data.size();
        HashSet set;
        Grids_2D_ID_int individual;
        boolean remove = false;
        double removeValue = value;
        for (ite = 0; ite < maxIte; ite++) {
            if (!(gotValue && setValue)) {
                try {
                    iterator.advance();
                    value = iterator.key();
                    //if ( ! gotValue ) { // Slows things down!? Probably...
                    /* Is this better than:
                     * if ( iterator.value().getClass() == HashSet.class  ) {
                     * } else {
                     *     // ( iterator.value().getClass() == Grids_2D_ID_int.class )
                     * }
                     * ?
                     */
                    try {
                        //if ( iterator.value() == null ) { // Debugging code
                        //    int null0Debug = 0; // Debugging code
                        //} // Debugging code
                        set = (HashSet) iterator.value();
                        if (set.contains(chunkCellID)) {
                            if (value == valueToSet) {
                                return value;
                            }
                            if (set.size() == 2) {
                                // Convert other entry to a Grids_2D_ID_int
                                Iterator setIterator = set.iterator();
                                for (int setIteratorIndex = 0; setIteratorIndex < 2; setIteratorIndex++) {
                                    individual = (Grids_2D_ID_int) setIterator.next();
                                    if (!individual.equals(chunkCellID)) {
                                        iterator.setValue(individual);
                                    }
                                }
                            } else {
                                set.remove(chunkCellID);
                            }
                            result = value;
                            gotValue = true;
                        }
                    } catch (java.lang.ClassCastException e) {
                        individual = (Grids_2D_ID_int) iterator.value();
                        if (individual.equals(chunkCellID)) {
                            if (value == valueToSet) {
                                return value;
                            }
                            // Need to set a value to be removed and remove it once stopped iterating to avoid ConcurrentModificationException.
                            remove = true;
                            removeValue = value;
                            result = value;
                            gotValue = true;
                        }
                    }
                    //if ( ! setValue ) { // Slows things down!? Probably...
                    if (value == valueToSet) {
                        try {
                            set = (HashSet) iterator.value();
                            set.add(chunkCellID);
                        } catch (java.lang.ClassCastException cce) {
                            individual = (Grids_2D_ID_int) iterator.value();
                            set = new HashSet();
                            set.add(individual);
                            set.add(chunkCellID);
                            iterator.setValue(set);
                        } catch (Exception e1) {
                            // What is happening?
                            e1.toString();
                            int e1Debug = 0;
                        }
                        setValue = true;
                    }
                    //}
                    //} catch ( ConcurrentModificationException cme0 ) {
                    //    int cme0Debug = 0;
                    //} catch ( NoSuchElementException nsee0 ) {
                    //    int nsee0Debug = 0;
                    //} catch ( NullPointerException npe ) {
                    //    int npe0Debug = 0;
                } catch (Exception e0) {
                    e0.printStackTrace(System.err);
                }
            }
        }
        if (remove) {
            this.data.remove(removeValue);
        }
        if (!setValue) {
            this.data.put(valueToSet, chunkCellID);
        }
        if (isSwapUpToDate()) { // Optimisation? Want a setCellFast method closer to initCell? What about an unmodifiable readOnly type chunk?
            if (valueToSet != value) {
                setSwapUpToDate(false);
            }
        }
        return result;
    }

    /**
     * Returns the number of cells with non _NoDataValues as a BigInteger.
     *
     * @return
     */
    protected @Override
    BigInteger getNonNoDataValueCountBigInteger() {
        double _NoDataValue = getGrid().getNoDataValue(false);
        TDoubleObjectIterator iterator = this.data.iterator();
        BigInteger nonNoDataCountBigInteger = BigInteger.ZERO;
        if (this.defaultValue == _NoDataValue) {
            for (int ite = 0; ite < this.data.size(); ite++) {
                iterator.advance();
                try {
                    nonNoDataCountBigInteger = nonNoDataCountBigInteger.add(
                            new BigInteger(Integer.toString(((HashSet) iterator.value()).size())));
                } catch (java.lang.ClassCastException e) {
                    nonNoDataCountBigInteger = nonNoDataCountBigInteger.add(BigInteger.ONE);
                }
            }
        } else {
            for (int ite = 0; ite < this.data.size(); ite++) {
                iterator.advance();
                if (iterator.key() == _NoDataValue) {
                    try {
                        long chunkNrows = (long) this.getGrid().getChunkNRows(this.ChunkID,
                                getGrid().ge.HandleOutOfMemoryErrorFalse);
                        long chunkNcols = (long) this.getGrid().getChunkNCols(this.ChunkID,
                                getGrid().ge.HandleOutOfMemoryErrorFalse);
                        return new BigInteger(Long.toString(
                                (chunkNrows * chunkNcols)
                                - ((HashSet) iterator.value()).size()));
                    } catch (java.lang.ClassCastException e) {
                        long chunkNrows = (long) this.getGrid().getChunkNRows(this.ChunkID,
                                getGrid().ge.HandleOutOfMemoryErrorFalse);
                        long chunkNcols = (long) this.getGrid().getChunkNCols(this.ChunkID,
                                getGrid().ge.HandleOutOfMemoryErrorFalse);
                        return new BigInteger(Long.toString(
                                (chunkNrows * chunkNcols) - 1L));
                    }
                }
            }
        }
        return nonNoDataCountBigInteger;
    }

    /**
     * Returns the sum of all non _NoDataValues as a BigDecimal.
     *
     * @return
     */
    protected @Override
    BigDecimal getSumBigDecimal() {
        BigDecimal sum = new BigDecimal(0.0d);
        BigDecimal thisCount;
        TDoubleObjectIterator iterator = this.data.iterator();
        HashSet set;
        Grids_2D_ID_int individual;
        for (int ite = 0; ite < data.size(); ite++) {
            iterator.advance();
            try {
                thisCount = new BigDecimal(Integer.toString(
                        ((HashSet) iterator.value()).size()));
                sum = sum.add(new BigDecimal(iterator.key()).multiply(thisCount));
            } catch (java.lang.ClassCastException e) {
                sum = sum.add(new BigDecimal(iterator.key()));
            }
        }
        return sum;
    }

    /**
     * Returns the minimum of all non _NoDataValues as a double.
     *
     * @return
     */
    protected @Override
    double getMinDouble() {
        double min = Double.POSITIVE_INFINITY;
        TDoubleObjectIterator iterator = this.data.iterator();
        for (int ite = 0; ite < data.size(); ite++) {
            iterator.advance();
            min = Math.min(min, iterator.key());
        }
        return min;
    }

    /**
     * Returns the maximum of all non _NoDataValues as a double
     *
     * @return
     */
    protected @Override
    double getMaxDouble() {
        double max = Double.NEGATIVE_INFINITY;
        TDoubleObjectIterator iterator = this.data.iterator();
        for (int ite = 0; ite < data.size(); ite++) {
            iterator.advance();
            max = Math.max(max, iterator.key());
        }
        return max;
    }

    /**
     * Returns the Arithmetic Mean of all non _NoDataValues as a double. Using
     * BigDecimal this should be as precise as possible with doubles.
     *
     * @return
     */
    protected @Override
    double getArithmeticMeanDouble() {
        BigDecimal sum = new BigDecimal(0.0d);
        BigDecimal thisCount;
        BigDecimal totalCount = new BigDecimal(0.0d);
        BigDecimal oneBigDecimal = new BigDecimal(1.0d);
        TDoubleObjectIterator iterator = this.data.iterator();
        for (int ite = 0; ite < data.size(); ite++) {
            iterator.advance();
            try {
                thisCount = new BigDecimal(Integer.toString(
                        ((HashSet) iterator.value()).size()));
                totalCount = totalCount.add(thisCount);
                sum = sum.add(new BigDecimal(iterator.key()).multiply(thisCount));
            } catch (java.lang.ClassCastException e) {
                sum = sum.add(new BigDecimal(iterator.key()));
                totalCount = totalCount.add(oneBigDecimal);
            }
        }
        if (totalCount.compareTo(new BigDecimal(0.0d)) == 1) {
            return sum.divide(
                    totalCount,
                    325,
                    BigDecimal.ROUND_HALF_EVEN).doubleValue();
        } else {
            return getGrid().getNoDataValue(false);
        }
    }

    /**
     * For returning the mode of all non _NoDataValues as a TDoubleHashSet
     *
     * @return
     */
    protected @Override
    TDoubleHashSet getModeTDoubleHashSet() {
        TDoubleHashSet mode = new TDoubleHashSet();
        TDoubleObjectIterator iterator = this.data.iterator();
        int modeCount = 0;
        int thisCount;
        for (int ite = 0; ite < data.size(); ite++) {
            iterator.advance();
            try {
                thisCount = ((HashSet) iterator.value()).size();
            } catch (java.lang.ClassCastException e) {
                thisCount = 1;
            }
            if (thisCount > modeCount) {
                mode.clear();
                mode.add(iterator.key());
                modeCount = thisCount;
            } else {
                if (thisCount == modeCount) {
                    mode.add(iterator.key());
                }
            }
        }
        return mode;
    }

    /**
     * For returning the median of all non _NoDataValues as a double
     *
     * @return
     */
    protected @Override
    double getMedianDouble() {
        int scale = 325;
        double[] keys = this.data.keys();
        sort1(keys, 0, keys.length);
        long nonNoDataValueCountLong = getNonNoDataValueCountBigInteger().longValue();
        if (nonNoDataValueCountLong > 0) {
            long index = -1L;
            if (nonNoDataValueCountLong % 2L == 0L) {
                // Need arithmetic mean of ( ( nonNoDataValueCount / 2 ) - 1 )th
                // and ( nonNoDataValueCount / 2 )th values
                long requiredIndex = (nonNoDataValueCountLong / 2L) - 1L;
                boolean got = false;
                BigDecimal medianBigDecimal = null;
                for (int keyIndex = 0; keyIndex < keys.length; keyIndex++) {
                    try {
                        index += (long) ((HashSet) this.data.get(keys[keyIndex])).size();
                    } catch (java.lang.ClassCastException e) {
                        index++;
                    }
                    if (!got && index >= requiredIndex) {
                        medianBigDecimal = new BigDecimal(keys[keyIndex]);
                        got = true;
                    }
                    if (index >= requiredIndex + 1L) {
                        BigDecimal bigDecimalTwo = new BigDecimal(2.0d);
                        BigDecimal bigDecimal0 = new BigDecimal(keys[keyIndex]);
                        BigDecimal bigDecimal1 = medianBigDecimal.add(bigDecimal0);
                        bigDecimal0 = bigDecimal1.divide(
                                bigDecimalTwo,
                                scale,
                                BigDecimal.ROUND_HALF_EVEN);
                        return bigDecimal0.doubleValue();
                    }
                }
            } else {
                // Need ( ( nonNoDataValueCount ) / 2 )th value
                long requiredIndex = nonNoDataValueCountLong / 2L;
                for (int keyIndex = 0; keyIndex < keys.length; keyIndex++) {
                    try {
                        index += ((HashSet) this.data.get(keys[keyIndex])).size();
                    } catch (java.lang.ClassCastException e) {
                        index++;
                    }
                    if (index >= requiredIndex) {
                        return keys[keyIndex];
                    }
                }
            }
        }
        return getGrid().getNoDataValue(false);
    }

    /**
     * For returning the standard deviation of all non _NoDataValues as a double
     *
     * @return
     */
    protected @Override
    double getStandardDeviationDouble() {
        double standardDeviation = 0.0d;
        double mean = getArithmeticMeanDouble();
        TDoubleObjectIterator iterator = this.data.iterator();
        HashSet set;
        Grids_2D_ID_int individual;
        int ite;
        long count = 0L;
        long thisCount;
        for (ite = 0; ite < this.data.size(); ite++) {
            iterator.advance();
            try {
                thisCount = (((HashSet) iterator.value()).size());
                count += thisCount;
                standardDeviation
                        += ((iterator.key() - mean) * (iterator.key() - mean))
                        * thisCount;
            } catch (java.lang.ClassCastException e) {
                standardDeviation
                        += (iterator.key() - mean) * (iterator.key() - mean);
                count++;
            }
        }
        if ((count - 1.0d) > 0) {
            return Math.sqrt(standardDeviation / (double) (count - 1L));
        } else {
            return standardDeviation;
        }
    }

    /**
     * For returning the number of different values.
     *
     * @return
     */
    protected BigInteger getDiversityBigInteger() {
        return new BigInteger(Integer.toString(this.data.size()));
    }

    /**
     * Returns a Grids_GridChunkDoubleMapIterator for iterating over the cells
     * in this.
     *
     * @return
     */
    protected @Override
    Grids_AbstractIterator iterator() {
        return new Grids_GridChunkDoubleMapIterator(this);
    }

    /**
     * Simple inner class for wrapping an int and a BitSet.
     */
    public class OffsetBitSet {

        public int Offset;
        public BitSet _BitSet;

        public OffsetBitSet(int offset) {
            Offset = offset;
            _BitSet = new BitSet();
        }
    }

}
