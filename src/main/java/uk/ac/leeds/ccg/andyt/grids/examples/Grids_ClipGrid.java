/**
 * Copyright (C) 2017 Andy Turner, CCG, University of Leeds, UK
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package uk.ac.leeds.ccg.andyt.grids.examples;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Dimensions;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridIntFactory;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridDoubleFactory;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_AbstractGridNumber;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridDouble;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkDoubleArrayFactory;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkIntArrayFactory;
import uk.ac.leeds.ccg.andyt.grids.io.Grids_ImageExporter;
import uk.ac.leeds.ccg.andyt.grids.io.Grids_ESRIAsciiGridExporter;
import uk.ac.leeds.ccg.andyt.grids.io.Grids_Files;
import uk.ac.leeds.ccg.andyt.grids.process.Grids_Processor;
import uk.ac.leeds.ccg.andyt.grids.process.Grids_ProcessorDEM;
import uk.ac.leeds.ccg.andyt.grids.utilities.Grids_Utilities;

/**
 * A class for giving an example of a clip operation on a grid.
 *
 * @author geoagdt
 */
public class Grids_ClipGrid
        extends Grids_Processor {

    private long Time;
    boolean HandleOutOfMemoryError;
    String Filename;

    protected Grids_ClipGrid() {
    }

    /**
     * Creates a new RoofGeneralisation using specified Directory. WARNING:
     * Files in the specified Directory may get overwritten.
     *
     * @param ge
     */
    public Grids_ClipGrid(Grids_Environment ge) {
        super(ge);
        Time = System.currentTimeMillis();
        HandleOutOfMemoryError = true;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        File dir = new File(
                System.getProperty("user.dir"));
        System.out.print("" + dir.toString());
        if (dir.exists()) {
            System.out.println(" exists.");
            dir.mkdirs();
        } else {
            System.out.println(" does not exist.");
        }
        Grids_Environment ge = new Grids_Environment(dir);
        Grids_ClipGrid t = new Grids_ClipGrid(ge);
        t.run();
    }

    public void run() {
        try {
            ge.setProcessor(this);
            Grids_Files gf;
            gf = ge.getFiles();
            File input;
            input = new File(
                    gf.getInputDataDir(),
                    "p15oct.asc");
            //C:\Users\geoagdt\src\grids\data\input

            Grids_ESRIAsciiGridExporter eage = new Grids_ESRIAsciiGridExporter(ge);
            Grids_ImageExporter imageExporter = new Grids_ImageExporter(ge);
            File workspaceDirectory = new File(
                    gf.getGeneratedDataDir(),
                    "/Workspace/");

            //String[] imageTypes = new String[0];
            String[] imageTypes = new String[1];
            imageTypes[0] = "PNG";
            String inputName;
            inputName = input.getName();
            System.out.println("inputFilename " + input);
            String inputNameWithoutExtension = inputName.substring(0, inputName.length() - 4);
            File outputDirectory;
            outputDirectory = new File(
                    gf.getOutputDataDir(),
                    getClass().getName());
            Grids_GridDouble gridDouble = null;
            Grids_GridDouble g;
            // Load input
            boolean notLoadedAsGrid = true;
            if (notLoadedAsGrid) {
                File dir;
                dir = new File(
                        ge.getFiles().getGeneratedGridDoubleDir(),
                        inputNameWithoutExtension);
                gridDouble = (Grids_GridDouble) GridDoubleFactory.create(
                        dir,
                        input,
                        HandleOutOfMemoryError);
                // clip gridDouble
                dir = new File(
                        ge.getFiles().getGeneratedGridDoubleDir(),
                        "Clipped" + inputNameWithoutExtension);
//                g = (Grids_GridDouble) GridDoubleFactory.create(
//                        dir,
//                        gridDouble,
//                        0,
//                        0,
//                        nRows - 1,
//                        nCols - 1,
//                        HandleOutOfMemoryError);
//                gridDouble = g;
//                // Cache input
//                boolean swapToFileCache = true;
//                gridDouble.writeToFile(swapToFileCache,
//                        HandleOutOfMemoryError);
                ge.getGrids().add(gridDouble);
                System.out.println("<outputImage>");
                System.out.println("outputDirectory " + outputDirectory);
                gridDouble.setName(inputNameWithoutExtension, HandleOutOfMemoryError);
                try {
                    outputImage(
                            gridDouble,
                            outputDirectory,
                            imageExporter,
                            imageTypes,
                            HandleOutOfMemoryError);
                } catch (IOException ex) {
                    Logger.getLogger(Grids_ClipGrid.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("</outputImage>");
            }
        } catch (Exception | Error e) {
            e.printStackTrace(System.err);
        }
    }

    public long getTime() {
        return Time;
    }
}