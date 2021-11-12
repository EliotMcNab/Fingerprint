package cs107;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Provides tools to compare fingerprint.
 */
public class Fingerprint {

    /**
     * The number of pixels to consider in each direction when doing the linear
     * regression to compute the orientation.
     */
    public static final int ORIENTATION_DISTANCE = 16;

    /**
     * The maximum distance between two minutiae to be considered matching.
     */
    public static final int DISTANCE_THRESHOLD = 5;

    /**
     * The number of matching minutiae needed for two fingerprints to be considered
     * identical.
     */
    public static final int FOUND_THRESHOLD = 20;

    /**
     * The distance between two angle to be considered identical.
     */
    public static final int ORIENTATION_THRESHOLD = 20;

    /**
     * The offset in each direction for the rotation to test when doing the
     * matching.
     */
    public static final int MATCH_ANGLE_OFFSET = 2;

    /**
     * The color of the minutiae and their orientation as in the processImage method
     */
    public static final int MINUTIA_CIRCLE_COLOR = 0xff0000;
    public static final int MINUTIA_CIRCLE_RADIUS = 5;

    public static final int MINUTIA_LINE_COLOR = 0xff00ff;
    public static final int MINUTIA_LINE_LENGTH = 10;

    // ==========================================================================
    //                          GETTING PIXEL INFORMATION
    // ==========================================================================

    /**
     * Returns true if the selected pixel is black
     * @param image image which contains the pixel
     * @param row y-coordinates of the pixel
     * @param col x-coordinates of the pixel
     * @return whether the pixel is black
     * @ TODO: 28.10.21 add index checking functionalities to fully-fledged image class
     */
    public static boolean isBlack(boolean[][] image, int row, int col) {

        // checks the pixel coordinates are valid
        // this is only to give more descriptive messages in case of error
        try {

            // tries to return the boolean value of the pixel
            return image[row][col];

        } catch (IndexOutOfBoundsException e) { // if the pixel coordinates are invalid...

            // ...gets the size of the image
            final int IMAGE_HEIGHT = image.length;
            final int IMAGE_WIDTH = image[0].length;

            // formats a new error message
            String errorMessage = String.format("Invalid pixel coordinates ! valid coordinates are" +
                                                "0 < x < %s and 0 < y < %s ! current coordinates: x = %s y = %s",
                                                IMAGE_WIDTH-1, IMAGE_HEIGHT-1, col, row);

            // throws error with clearer error message
            throw new IndexOutOfBoundsException(errorMessage);
        }
    }

    /**
     * Returns true if the selected pixel is black
     * @param neighbours neighbouring pixels to another pixel
     * @param curNeighbour neighbouring pixel whose value is to be checked
     * @return whether the pixel is black
     */
    public static boolean isBlack(boolean[] neighbours, int curNeighbour) {
        return neighbours[curNeighbour];
    }

    /**
     * Returns true if the selected pixel is white
     * @param neighbours neighbouring pixels to another pixel
     * @param curNeighbour neighbouring pixel whose value is to be checked
     * @return whether the pixel is white
     */
    public static boolean isWhite(boolean[] neighbours, int curNeighbour) {
        return !isBlack(neighbours, curNeighbour);
    }

  /**
   * Returns an array containing the value of the 8 neighbours of the pixel at
   * coordinates <code>(row, col)</code>.
   * <p>
   * The pixels are returned such that their indices corresponds to the following
   * diagram:<br>
   * ------------- <br>
   * | 7 | 0 | 1 | <br>
   * ------------- <br>
   * | 6 | _ | 2 | <br>
   * ------------- <br>
   * | 5 | 4 | 3 | <br>
   * ------------- <br>
   * <p>
   * If a neighbours is out of bounds of the image, it is considered white.
   * <p>
   * If the <code>row</code> or the <code>col</code> is out of bounds of the
   * image, the returned value should be <code>null</code>.
   *
   * @param image array containing each pixel's boolean value.
   * @param row   the row of the pixel of interest, must be between
   *              <code>0</code>(included) and
   *              <code>image.length</code>(excluded).
   * @param col   the column of the pixel of interest, must be between
   *              <code>0</code>(included) and
   *              <code>image[row].length</code>(excluded).
   * @return An array containing each neighbours' value.
   * @throws IllegalArgumentException current pixel is on an edge
   *                                  <list>
   *                                      <li>row = 0 or row = image.length</li>
   *                                      <li>col = 0 or col = image.length</li>
   *                                  </list>
   *
   */
    public static boolean[] getNeighbours(boolean[][] image, int row, int col) {
        assert (image != null); // special case that is not expected (the image is supposed to have been checked
                                // earlier)

        // TODO not functional yet but represents functionality of future Image class

        // the values around the current pixel
        boolean[] neighBoringValues = new boolean[8];

        // checks the values of the pixels above the current pixel
        checkUpperPixels(neighBoringValues, image, row, col);

        // checks the values of the pixels on the same row as the current pixel
        checkRowPixels(neighBoringValues, image, row, col);

        // checks the values of the pixels below the current pixel
        checkLowerPixels(neighBoringValues, image, row, col);

        // returns the values around the current pixel
        return neighBoringValues;
    }

    // region getNeighbours helper methods

    private static boolean hasPixelToRight(boolean[][] image, int row, int col) {
        return col != image[0].length-1;
    }

    private static boolean hasPixelToLeft(boolean[][] image, int row, int col) {
        return col != 0;
    }

    private static boolean hasPixelAbove(boolean[][] image, int row, int col) {
        return row != 0;
    }

    private static boolean hasPixelBelow(boolean[][] image, int row, int col) {
        return row != image.length-1;
    }

    /**
     * Checks the pixels above the current pixel, if there are any, and adds them to the list of neighbouring pixels
     * @param neighbouringValues value of pixels neighbouring the current pixel
     * @param image image containing the pixel
     * @param row y-coordinates of the pixel
     * @param col x-coordinates of the pixel
     */
    private static void checkUpperPixels(boolean[] neighbouringValues, boolean[][] image, int row, int col) {

        // region pixel 0 & default values

        // if there are no pixels above...
        if (!hasPixelAbove(image, row, col)) {
            // ...considers the pixels that should have been above as white pixels
            neighbouringValues[0] = false;       // the pixel in position 0
            neighbouringValues[1] = false;       // the pixel in position 1
            neighbouringValues[7] = false;       // the pixel in position 7

            // exits the method
            return;
        }

        // if there are pixels above adds the value of the pixel
        // directly above to the neighbouring values
        neighbouringValues[0] = image[row-1][col];                                       // the pixel in position 0

        // endregion

        // region pixel 1

        // if there is a pixel to the right of the current pixel...
        if (hasPixelToRight(image, row, col)) {
            // ...adds the value of that pixel to the neighbouring values
            neighbouringValues[1] = image[row - 1][col + 1];                             // the pixel in position 1
        }
        // if there is no pixel to the right of the current pixel (i.e. we are the left edge of the image)
        else {
            // considers the upper right pixel to be a white pixel
            neighbouringValues[1] = false;                                               // the pixel in position 1
        }

        // endregion

        // region pixel 7

        // if there is a pixel to the left of the current pixel...
        if (hasPixelToLeft(image, row, col)) {
            // ...adds the value of the upper left pixel to the neighbouring values
            neighbouringValues[7] = image[row - 1][col - 1];                             // the pixel in position 7
        }
        // if there is no pixel to the right of the current pixel (i.e. we are the left edge of the image)
        else {
            // considers the upper left pixel to be a white pixel
            neighbouringValues[7] = false;                                               // the pixel in position 7
        }

        // endregion
    }

    /**
     * Checks the pixels on the same row as the current pixel,
     * if there are any, and adds them to the list of neighbouring pixels
     * @param neighbouringValues value of pixels neighbouring the current pixel
     * @param image image containing the pixel
     * @param row y-coordinates of the pixel
     * @param col x-coordinates of the pixel
     */
    private static void checkRowPixels(boolean[] neighbouringValues, boolean[][] image, int row, int col) {

        // region pixel 2

        // if there exists a pixel to the right of the current pixel
        if (hasPixelToRight(image, row, col)) {
            // ...adds the value of the pixel to the neighbouring values
            neighbouringValues[2] = image[row][col + 1];                                     // the pixel in position 2
        }

        // if there is no pixel to the right of the current pixel...
        else {
            // ...considers it as a white pixel
            neighbouringValues[2] = false;                                                   // the pixel in position 2
        }

        // endregion

        // region pixel 6

        // if there exists a pixel to the left of the current pixel
        if (hasPixelToLeft(image, row, col)) {
            // ...adds the value of the pixel to the neighbouring values
            neighbouringValues[6] = image[row][col - 1];                                     // the pixel in position 6
        }

        // if there is no pixel to the right of the current pixel...
        else {
            // ...considers it as a white pixel
            neighbouringValues[6] = false;                                                   // the pixel in position 6
        }

        // endregion
    }

    /**
     * Checks the pixels below the current pixel,if there are any, and adds them to the list of neighbouring pixels
     * @param neighbouringValues value of pixels neighbouring the current pixel
     * @param image image containing the pixel
     * @param row y-coordinates of the pixel
     * @param col x-coordinates of the pixel
     */
    private static void checkLowerPixels(boolean[] neighbouringValues, boolean[][] image, int row, int col) {

        // region pixel 4 & default values

        // if there are no pixels above the current pixel...
        if (!hasPixelBelow(image, row, col)) {
            // ...considers the pixels that should have been below as white pixels
            neighbouringValues[3] = false;       // the pixel in position 3
            neighbouringValues[4] = false;       // the pixel in position 4
            neighbouringValues[5] = false;       // the pixel in position 5

            // exits the method
            return;
        }

        // if there are pixels above adds the value of the pixel
        // directly above to the neighbouring values
        neighbouringValues[4] = image[row+1][col];                                       // the pixel in position 4

        // endregion

        // region pixel 3

        // if there is a pixel to the right of the current pixel...
        if (hasPixelToRight(image, row, col)) {
            // ...adds the value of the lower right pixel to the neighbouring values
            neighbouringValues[3] = image[row + 1][col + 1];                             // the pixel in position 3
        }
        // if there is no pixel to the right of the current pixel (i.e. we are the left edge of the image)
        else {
            // considers the lower right pixel to be a white pixel
            neighbouringValues[3] = false;                                               // the pixel in position 3
        }

        // endregion

        // region pixel 5

        // if there is a pixel to the left of the current pixel...
        if (hasPixelToLeft(image, row, col)) {
            // ...adds the value of the lower left pixel to the neighbouring values
            neighbouringValues[5] = image[row + 1][col - 1];                             // the pixel in position 5
        }
        // if there is no pixel to the right of the current pixel (i.e. we are the left edge of the image)
        else {
            // considers the lower left pixel to be a white pixel
            neighbouringValues[5] = false;                                               // the pixel in position 5
        }

        // endregion
    }

    // endregion

    /**
    * Computes the number of black (<code>true</code>) pixels among the neighbours
    * of a pixel.
    *
    * @param neighbours array containing each pixel value. The array must respect
    *                   the convention described in
    *                   {@link #getNeighbours(boolean[][], int, int)}.
    * @return the number of black neighbours.
    */
    public static int blackNeighbours(boolean[] neighbours) {

        // the number of black neighbours around the current pixel
        int numBlackNeighbours = 0;

        // checks every neighbour...
        for (boolean neighbour : neighbours) {
            // ...and if the neighbour is a black pixel...
            if (neighbour) {
                // ...counts it
                numBlackNeighbours++; // using post-incrementation here as the return value is of no importance
            }
        }

        // returns the final number of neighbouring black pixels
        return numBlackNeighbours;
    }

    /**
    * Computes the number of white to black transitions among the neighbours of
    * pixel.
    *
    * @param neighbours array containing each pixel value. The array must respect
    *                   the convention described in
    *                   {@link #getNeighbours(boolean[][], int, int)}.
    * @return the number of white to black transitions.
     * @throws IllegalArgumentException when neighbours array is not of correct size (7)
    */
    public static int transitions(boolean[] neighbours) {

        // region checking neighbours array size (error handling)

        // checks the array of neighbouring values is of the current format (1D length 7 array)
        // gets the length of the array
        final int NEIGHBOURING_ARRAY_LENGTH = neighbours.length;

        // if the array is not of the right format...
        if (NEIGHBOURING_ARRAY_LENGTH != 8) {
            // ...formats a new error
            String errorMessage = String.format("array of neighbouring values must be of length 8" +
                                                " current length: %s", NEIGHBOURING_ARRAY_LENGTH);

            // ...throws an IllegalArgumentException specifying the array is of invalid length
            throw new IllegalArgumentException(errorMessage);
        }

        // endregion

        // region calculate the number of transitions

        // the number of transitions
        int numTransitions = 0;

        // the number of neighbours
        final int NUM_NEIGHBOURS = 8;
        final int LAST_NEIGHBOUR = NUM_NEIGHBOURS-1;
        final int FIRST_NEIGHBOUR = 0;

        // goes through each neighbour...
        for (int currentNeighbour = 1; currentNeighbour < NUM_NEIGHBOURS; currentNeighbour++) {

            int previousNeighbour = currentNeighbour - 1;

            // ...and if the current neighbour is black and the previous neighbour is white...
            if (isBlack(neighbours, currentNeighbour) && isWhite(neighbours, previousNeighbour)) {
                // ...counts this as a transition
                numTransitions++; // using post-incrementation again, same reason as before
            }
        }

        // if the last neighbouring pixel is white and the first pixel is black, counts this as a transition
        if (isWhite(neighbours,LAST_NEIGHBOUR) && isBlack(neighbours, FIRST_NEIGHBOUR)) numTransitions++;

        // endregion

        // returns the final number of transition
        return numTransitions;
    }

    // ==========================================================================
    //                               THINNING
    // ==========================================================================

    /**
    * Returns <code>true</code> if the images are identical and false otherwise.
    *
    * @param image1 array containing each pixel's boolean value.
    * @param image2 array containing each pixel's boolean value.
    * @return <code>True</code> if they are identical, <code>false</code>
    *         otherwise.
    */
    public static boolean identical(boolean[][] image1, boolean[][] image2) {

        // region checks image are of correct size

        // gets the dimensions of the image
        final int IMAGE_HEIGHT = image1.length;
        final int IMAGE_WIDTH = image1[0].length;

        // if the images are not the same size...
        if (IMAGE_HEIGHT != image2.length && IMAGE_WIDTH != image2[0].length) {
            // ...then the images are not identical, exits the method
            return false;
        }

        // endregion

        // region compares images pixel by pixel

        // loops through every row of pixels in the image...
        for (int y = 0; y < IMAGE_HEIGHT; y++) {
            // ...and every pixel in these row...
            for (int x = 0; x < IMAGE_WIDTH; x++) {

                // ...and gets the current pixel for each image
                boolean pixel1 = image1[y][x];
                boolean pixel2 = image2[y][x];

                // if the pixels in the two images are not identical...
                if (pixel1 != pixel2) {
                    // ...then the images are different, exits the loop
                    return false;
                }
            }
        }

        // endregion

        // if no difference has been detected, then the images are identical
        return true;
    }

    /**
    * Internal method used by {@link #thin(boolean[][])}.
    *
    * @param image array containing each pixel's boolean value.
    * @param step  the step to apply, Step 0 or Step 1.
    * @return A new array containing each pixel's value after the step.
    */
    public static boolean[][] thinningStep(boolean[][] image, int step) {

        // the dimensions of the image
        final int IMAGE_HEIGHT = image.length;
        final int IMAGE_WIDTH  = image[0].length;

        // creates a copy of the original image
        // would normally just edit the original for performance reasons but signatureCheck
        // does not allow the method return type to be void
        boolean[][] imageCopy = new boolean[IMAGE_HEIGHT][IMAGE_WIDTH];
        copy2DArray(image, imageCopy);

        // loops through every row of pixels in the image
        for (int y = 0; y < IMAGE_HEIGHT; y++) {

            // loops through every pixel in each row
            for (int x = 0; x < IMAGE_WIDTH; x++) {

                // the neighbours of the current pixel
                boolean[] currentNeighbours = getNeighbours(image, y, x);
                int currentBlackNeighbours = blackNeighbours(currentNeighbours);

                // the condition for the pixel to be deleted
                boolean isBlackPixel = image[y][x];
                boolean allNeighboursAreNotNull = currentNeighbours != null;
                boolean hasValidNumNeighbours = currentBlackNeighbours >= 2 && currentBlackNeighbours <= 6;
                boolean isOnlyOneTransition = transitions(currentNeighbours) == 1;

                // the value of the specific tiles to check for each step
                boolean check1White;
                boolean check2White;

                if (step == 1) {    // step 1
                    check1White = !currentNeighbours[0] || !currentNeighbours[2] || !currentNeighbours[4];
                    check2White = !currentNeighbours[2] || !currentNeighbours[4] || !currentNeighbours[6];
                } else {            // step 2
                    check1White = !currentNeighbours[0] || !currentNeighbours[2] || !currentNeighbours[6];
                    check2White = !currentNeighbours[0] || !currentNeighbours[4] || !currentNeighbours[6];
                }

                // if the pixel satisfies all the above condition...
                if (isBlackPixel
                        && allNeighboursAreNotNull
                        && hasValidNumNeighbours
                        && isOnlyOneTransition
                        && check1White && check2White) {

                    // ...removes it from the image (sets it to white)
                    imageCopy[y][x] = false;
                }
            }
        }

        // returns the formatted image
        return imageCopy;
    }

    /**
    * Compute the skeleton of a boolean image.
    *
    * @param image array containing each pixel's boolean value.
    * @return array containing the boolean value of each pixel of the image after
    *         applying the thinning algorithm.
    */
    public static boolean[][] thin(boolean[][] image) {

        // the dimensions of the image
        final int IMAGE_HEIGHT = image.length;
        final int IMAGE_WIDTH  = image[0].length;

        // the image from the last and current step
        boolean[][] previousImage = new boolean[IMAGE_HEIGHT][IMAGE_WIDTH];
        boolean[][] currentImage = new boolean[IMAGE_HEIGHT][IMAGE_WIDTH];

        // initialises the current image as a copy of the initial image
        copy2DArray(image, currentImage);

        // while we can still apply some thinning to the image...
        while (!identical(previousImage, currentImage)) {

            // applies thinning to the image
            previousImage = thinningStep(currentImage, 1); // step 1
            currentImage = thinningStep(previousImage, 0); // step 2
        }

        // returns the thinned image
        return currentImage;
    }

    /**
    * Computes all pixels that are connected to the pixel at coordinate
    * <code>(row, col)</code> and within the given distance of the pixel.
    *
    * @param image    array containing each pixel's boolean value.
    * @param row      the first coordinate of the pixel of interest.
    * @param col      the second coordinate of the pixel of interest.
    * @param distance the maximum distance at which a pixel is considered.
    * @return An array where <code>true</code> means that the pixel is within
    *         <code>distance</code> and connected to the pixel at
    *         <code>(row, col)</code>.
    */

    public static boolean [][]connected(boolean[][] image, int row, int col, int distance) {
        int n = image.length;
        int m = image[0].length;
        boolean[][] connected_pixels = new boolean[n][m];

        connected_pixels[row][col] = image[row][col];
       boolean[][] connected_inter = new boolean[n][m];

        do{
            for (int i = 0; i < n; i++) {
                System.arraycopy(connected_pixels[i], 0, connected_inter[i], 0, m);
            }

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    if (isBlack(image, i, j) &&
                            (hasBlackNeighbour(getNeighbours(connected_pixels, i, j))) &&
                            (Math.abs(i - row) <= (distance)) &&
                            (Math.abs(j - col) <=( distance))) {
                        connected_pixels[i][j] = true;
                    }
                }
            }

        } while(!identical(connected_inter,connected_pixels));

                return connected_pixels ;
            }

    public static boolean[][] connectedPixels(boolean[][] image, int row, int col, int distance) {
        boolean [][] connected_pixels;

        connected_pixels=connected(image,row ,col,distance);

        return connected_pixels;
    }
    public static boolean hasBlackNeighbour(boolean[] blackNeighbours){
        for (int i=0;i<blackNeighbours.length;i++){
            if (blackNeighbours[i]==true) return true;
        }
        return false;
    }

    // ==========================================================================
    //                              GETTING MINUTIAE
    // ==========================================================================

    /**
     * Computes the parameters needed for linear regression
     * @param connectedPixels neighbouring pixel to selected minutia
     * @param minutiaRow y-coordinates of the minutia
     * @param minutiaCol x-coordinates of the minutia
     * @return array of parameters needed to compute linear regression
     * <list>
     *     <li>0: sum(X²)</li>
     *     <li>1: sum(Y²)</li>
     *     <li>2: sum(X*Y)</li>
     * </list>
     */
    public static int[] slopeParameters(boolean[][] connectedPixels, int minutiaRow, int minutiaCol){

        // the variable which will hold the parameters
        // involved in computing the slope of a minutia
        int[] parameters = new int[3];

        // the variable which will hold the sum of the
        // squares of the X-coordinates of each pixel
        int sumXX = 0;

        // the variable which will hold the sum of the
        // squares of the y-coordinates of each pixel
        int sumYY = 0;

        // the variable which will hold the sum of the
        // x-coordinates and y-coordinates of each pixel
        int sumXY = 0;

        // the size of the image
        final int IMAGE_HEIGHT = connectedPixels.length ;
        final int IMAGE_WIDTH  = connectedPixels[0].length ;

        // for every row in the connected pixel...
        for (int pixelRow = 0; pixelRow < IMAGE_HEIGHT; pixelRow++) {
            // ...and every pixel in that row...
            for (int pixelCol = 0; pixelCol < IMAGE_WIDTH; pixelCol++) {
                // ...if the pixel is a black pixel...
                if (isBlack(connectedPixels, pixelRow, pixelCol)) {

                    // ...adjusts it's coordinates
                    int X = centerCol(pixelCol, minutiaCol);
                    int Y = centerRow(pixelRow, minutiaRow);

                    // ...adds it's coordinates to each respective sum
                    sumXX += X * X;
                    sumYY += Y * Y;
                    sumXY += X * Y;
                }
            }
        }

        // adds the final sums to the slope parameters
        parameters[0] = sumXX;
        parameters[1] = sumYY;
        parameters[2] = sumXY;

        return parameters;
    }

    /**
     * Computes the slope of a minutia using linear regression.
     *
     * @param connectedPixels the result of
     *                        {@link #connectedPixels(boolean[][], int, int, int)}.
     * @param row             the row of the minutia.
     * @param col             the col of the minutia.
     * @return the slope.
     */
    public static double computeSlope(boolean[][] connectedPixels, int row, int col) {

        // the variable holding the slope of the minutia
        double slope = 0;

        // gets the parameters needed for linear regression
        int[] parameters = slopeParameters(connectedPixels, row, col);

        final int SUM_XX = parameters[0];  // sum(X²)
        final int SUM_YY = parameters[1];  // sum(Y²)
        final int SUM_XY = parameters[2];  // sum(X*Y)

        // determines the slope of the minutia
        if (SUM_XX >= SUM_YY) {
            slope =  (double) SUM_XY / SUM_XX;
        } else if (SUM_XX < SUM_YY) {
            slope = (double) SUM_YY /  SUM_XY;
        } else if (SUM_XX == 0) {                   // special case where the slope is vertical
            slope = Double.POSITIVE_INFINITY;
        }

        // if the slope approaches a large number...
        if (Math.abs(slope) > 100) {
            // ...also considers it to be vertical
            slope = Double.POSITIVE_INFINITY;
        }

        // returns the final slope
        return slope;
    }

    /**
     * Computes the orientation of a minutia in radians.
     *
     * @param connectedPixels the result of
     *                        {@link #connectedPixels(boolean[][], int, int, int)}.
     * @param row             the row of the minutia.
     * @param col             the col of the minutia.
     * @param slope           the slope as returned by
     *
     * @return the orientation of the minutia in radians.
     *
     */

    public static double computeAngle(boolean[][] connectedPixels, int row, int col, double slope) {

        // whether the minutia has more pixels above or below it
        final boolean MORE_PIXELS_ABOVE = hasMorePixelsAbove(connectedPixels, row, col, slope);
        final boolean MORE_PIXELS_BELOW = !MORE_PIXELS_ABOVE;

        // region SPECIAL CASE : minutia has a vertical slope
        if (slope == Double.POSITIVE_INFINITY) {

            if (MORE_PIXELS_ABOVE) {
                return Math.PI / 2;
            } else {
                return -Math.PI/2;
            }
        }
        // endregion

        // region SPECIAL CASE : minutia has a horizontal slope
        if (slope == 0) {

            // here pixels above refer to pixels to the right of the minutia
            // while pixels below refer to those to the left of the minutia
            if (MORE_PIXELS_ABOVE) {
                return 0;
            } else {
                return Math.PI;
            }
        }
        // endregion

        // calculates the angle of the minutia
        double angle = Math.atan(slope);

        // whether the minutia's angle is positive or negative
        final boolean IS_POSITIVE_ANGLE = angle >= 0;
        final boolean IS_NEGATIVE_ANGLE = angle < 0;

        // adjusts the angle of the minutia if necessary
        if ((IS_POSITIVE_ANGLE && MORE_PIXELS_BELOW) || (IS_NEGATIVE_ANGLE && MORE_PIXELS_ABOVE)){
            return angle + Math.PI ;
        }

        // returns the final angle (in radians) of the minutia
        return angle;
    }

    // region computeAngle helper methods

    /**
     * Determines if a minutia has more pixels above or below it
     * @param connectedPixels pixels which are connected to the minutia
     * @param row y-coordinates of the minutia
     * @param col x-coordinates of the minutia
     * @param slope slope of the minutia
     * @return whether the minutia has more pixels above or below it
     * @implNote if a minutia has a slope of 0, the pixel above refer to the pixels
     * to the right if the minutia and the pixels below to those to the left of the minutia
     */
    private static boolean hasMorePixelsAbove(boolean[][] connectedPixels, int row, int col, double slope) {

        // the number of pixels above and below the minutia
        int pixelBelow = 0;
        int pixelsAbove = 0;

        // the size of the image
        final int IMAGE_HEIGHT = connectedPixels.length;
        final int IMAGE_WIDTH  = connectedPixels[0].length;

        // for every connected row...
        for (int pixelRow = 0; pixelRow < IMAGE_HEIGHT; pixelRow++) {

            // ...and every connected pixel...
            for (int pixelCol = 0; pixelCol < IMAGE_WIDTH; pixelCol++) {

                // ...checks if the pixel is black
                if (isBlack(connectedPixels, pixelRow, pixelCol)) {

                    // SPECIAL CASE : the slope of the minutia is vertical
                    if (itAboveVerticalSlope(slope, pixelRow, row)) pixelsAbove++;

                    // SPECIAL CASE : the slope of the minutia is horizontal
                    if (isRightOfHorizontalSlope(slope, pixelRow, row)) pixelsAbove++;

                    // adjusts the pixel's coordinates
                    int adjustedRow = centerRow(pixelRow, row);
                    int adjustedCol = centerCol(pixelCol, col);

                    // pixel is above the minutia
                    if (isAbove(adjustedRow, adjustedCol, slope)) pixelsAbove++;

                    // pixel is below the minutia
                    else pixelBelow++;
                }
            }
        }

        // whether there are more pixels above the current minutia
        return pixelsAbove >= pixelBelow;
    }

    /**
     * Determines if a pixel is above a minutia with an infinite slope
     * @param slope slope of the minutia
     * @param pixelRow y-coordinates of the pixel
     * @param minutiaRow y-coordinates of the minutia
     * @return whether the pixel is above or below the minutia
     */
    private static boolean itAboveVerticalSlope(double slope, int pixelRow, int minutiaRow) {
        return (slope == Double.POSITIVE_INFINITY) && (pixelRow < minutiaRow);
    }

    /**
     * Determines if a pixel to the right of a minutia with a slope of 0
     * @param slope slope of the minutia
     * @param pixelCol x-coordinates of the pixel
     * @param minutiaCol x-coordinates of the minutia
     * @return whether the pixel is above or below the minutia
     */
    private static boolean isRightOfHorizontalSlope(double slope, int pixelCol, int minutiaCol) {
        return (slope == 0) && (pixelCol > minutiaCol);
    }

    /**
     * Centers a pixel's x-coordinates to a minutia
     * @param pixelCol the pixel's x-coordinates
     * @param minutiaCol the minutia's x-coordinates
     * @return adjusted x-coordinates
     */
    private static int centerCol(int pixelCol, int minutiaCol) {
        // centers the pixel
        return pixelCol - minutiaCol;
    }

    /**
     * Centers a pixel's y-coordinates to a minutia
     * @param pixelRow the pixel's y-coordinates
     * @param minutiaRow the minutia's y-coordinates
     * @return adjusted y-coordinates
     */
    private static int centerRow(int pixelRow, int minutiaRow) {
        // centers the pixel
        return minutiaRow - pixelRow;
    }

    /**
     * Determines whether a pixel is above a minutia
     * @param row the pixel's y-coordinates
     * @param col the pixel's x-coordinates
     * @param slope the slope of the minutia
     * @return true if the pixel is above or on the same level as the minutia, false otherwise
     */
    private static boolean isAbove(int row, int col, double slope) {
        // determines if the pixel is above the minutia
        return row >= -col/slope;
    }

    // endregion

    /**
    * Computes the orientation of the minutia that the coordinate <code>(row,
    * col)</code>.
    *
    * @param image    array containing each pixel's boolean value.
    * @param row      the first coordinate of the pixel of interest.
    * @param col      the second coordinate of the pixel of interest.
    * @param distance the distance to be considered in each direction to compute
    *                 the orientation.
    * @return The orientation in degrees.
    */
    public static int computeOrientation(boolean[][] image, int row, int col, int distance) {

        boolean [][] connectedPixels = connectedPixels( image,  row,  col,  distance);

        double slope = computeSlope(connectedPixels,row,col);
        double angle = computeAngle(connectedPixels,row,col,slope);

        angle=Math.toDegrees(angle);

        if (angle<0){
            angle += 360;
        }

        angle=Math.round(angle);

        return (int) angle;
    }

    /**
    * Extracts the minutiae from a thinned image.
    *
    * @param image array containing each pixel's boolean value.
    * @return The list of all minutiae. A minutia is represented by an array where
    *         the first element is the row, the second is column, and the third is
    *         the angle in degrees.
    * @see #thin(boolean[][])
    */
    public static List<int[]> extract(boolean[][] image) {

        // variable which contains the coordinates of all the minutiae in a fingerprint
        ArrayList<int[]> minutiaCoordinates = new ArrayList<int[]>();

        // size of the image
        final int IMAGE_HEIGHT = image.length;
        final int IMAGE_WIDTH = image[0].length;

        // for every row in the image...
        for (int row = 1; row < IMAGE_HEIGHT-1; row++){
            // ...and every pixel in each row...
            for (int col = 1 ; col < IMAGE_WIDTH-1; col++){

                // if the pixel is black and is not too clos to the previous minutia
                if (isBlack(image, row, col)) {

                    // ...gets its neighbours
                    boolean[] curNeighbours = getNeighbours(image, row, col);

                    // determines whether the number of transitions in the pixels
                    // neighbouring the current pixel are characteristic of a minutia
                    int numTransitions = transitions(curNeighbours);
                    final boolean ARE_MINUTIA_TRANSITIONS = numTransitions == 1 || numTransitions == 3;

                    // if the transitions are characteristic of a minutia...
                    if (ARE_MINUTIA_TRANSITIONS) {

                        // ...counts the pixel as a minutia
                        int angle = computeOrientation(image,row,col,ORIENTATION_DISTANCE);
                        minutiaCoordinates.add(new int[]{row, col, angle});
                    }
                }
            }
        }

        // returns the coordinates of every minutia in the fingerprint
        return minutiaCoordinates;
    }

    // ==========================================================================
    //                              MINUTIA COMPARISON
    // ==========================================================================

    /**
    * Applies the specified rotation to the minutia.
    *
    * @param minutia   the original minutia.
    * @param centerRow the row of the center of rotation.
    * @param centerCol the col of the center of rotation.
    * @param rotation  the rotation in degrees.
    * @return the minutia rotated around the given center.
    */
    public static int[] applyRotation(int[] minutia, int centerRow, int centerCol, int rotation) {

        // extracts the characteristics of the minutia
        final int MINUTIA_ROW           = minutia[0];
        final int MINUTIA_COL           = minutia[1];
        final int MINUTIA_ORIENTATION   = minutia[2];

        // converts the rotation from degrees to radians
        final double ROTATION_RAD       = Math.toRadians(rotation);

        // determines the relative position of the minutia
        final int RELATIVE_COL = centerCol(MINUTIA_COL, centerCol);
        final int RELATIVE_ROW = centerRow(MINUTIA_ROW, centerRow);

        // applies the rotation to the minutia
        final int ROTATED_COL = (int) (RELATIVE_COL * Math.cos(ROTATION_RAD) - RELATIVE_ROW * Math.sin(ROTATION_RAD));
        final int ROTATED_ROW = (int) (RELATIVE_COL * Math.sin(ROTATION_RAD) + RELATIVE_ROW * Math.cos(ROTATION_RAD));

        // determines the final coordinates of the minutia after it has been rotated
        final int NEW_ROW = centerRow   - ROTATED_ROW;
        final int NEW_COL = ROTATED_COL + centerCol;

        // determines the orientation of the minutia after it has been rotated
        final int NEW_ORIENTATION = (MINUTIA_ORIENTATION + rotation) % 360;

        // saves the characteristics of the rotated minutia into an array
        // 0 : row
        // 1 : column
        // 2 : orientation
        int[] rotatedMinutia    = new int[3];
        rotatedMinutia[0]       = NEW_ROW;
        rotatedMinutia[1]       = NEW_COL;
        rotatedMinutia[2]       = NEW_ORIENTATION;

        // returns the characteristics of the rotated minutia
        return rotatedMinutia;
    }

    /**
    * Applies the specified translation to the minutia.
    *
    * @param minutia        the original minutia.
    * @param rowTranslation the translation along the rows.
    * @param colTranslation the translation along the columns.
    * @return the translated minutia.
    */
    public static int[] applyTranslation(int[] minutia, int rowTranslation, int colTranslation) {

        // extracts the characteristics of the minutia
        final int MINUTIA_ROW           = minutia[0];
        final int MINUTIA_COL           = minutia[1];
        final int MINUTIA_ORIENTATION   = minutia[2];

        // determines the new characteristics of the minutia after translation
        final int NEW_ROW               = MINUTIA_ROW -rowTranslation;
        final int NEW_COL               = MINUTIA_COL-colTranslation;
        final int NEW_ORIENTATION       = MINUTIA_ORIENTATION;

        // saves the characteristics of the translated minutia into an array
        // 0 : row
        // 1 : column
        // 2 : orientation
        int[] translatedMinutia     = new int[3];
        translatedMinutia[0]        = NEW_ROW;
        translatedMinutia[1]        = NEW_COL;
        translatedMinutia[2]        = NEW_ORIENTATION;

        // returns the characteristics of the translated minutia
        return translatedMinutia;
    }

    /**
    * Computes the row, column, and angle after applying a transformation
    * (translation and rotation).
    *
    * @param minutia        the original minutia.
    * @param centerCol      the column around which the point is rotated.
    * @param centerRow      the row around which the point is rotated.
    * @param rowTranslation the vertical translation.
    * @param colTranslation the horizontal translation.
    * @param rotation       the rotation.
    * @return the transformed minutia.
    */
    public static int[] applyTransformation(int[] minutia, int centerRow, int centerCol, int rowTranslation,
                                            int colTranslation, int rotation) {

        // rotates the minutia
        int[] rotatedMinutiae       = applyRotation(minutia, centerRow, centerCol, rotation);
        // translates the minutia
        int[] translatedMinutia     = applyTranslation(rotatedMinutiae, rowTranslation, colTranslation);

        // returns the final rotated and translated minutia
        return translatedMinutia;
    }

    /**
    * Computes the row, column, and angle after applying a transformation
    * (translation and rotation) for each minutia in the given list.
    *
    * @param minutiae       the list of minutiae.
    * @param centerCol      the column around which the point is rotated.
    * @param centerRow      the row around which the point is rotated.
    * @param rowTranslation the vertical translation.
    * @param colTranslation the horizontal translation.
    * @param rotation       the rotation.
    * @return the list of transformed minutiae.
    */
    public static List<int[]> applyTransformation(List<int[]> minutiae, int centerRow, int centerCol, int rowTranslation,
                                                  int colTranslation, int rotation) {

        // the variable containing all the minutiae after they have been transformed
        List <int[]> allTransformedMinutia = new ArrayList<int[]>();

        // for every minutia...
        for (int[] curMinutia : minutiae) {

            // ...applies the transformation to it
            int[] transformedMinutia = applyTransformation(curMinutia, centerRow, centerCol,
                    rowTranslation, colTranslation, rotation);

            // ...saves the transformed minutia
            allTransformedMinutia.add(transformedMinutia);

        }

        // returns all the minutiae once they have been transformed
        return allTransformedMinutia;
    }

    /**
    * Counts the number of overlapping minutiae.
    *
    * @param minutiae1      the first set of minutiae.
    * @param minutiae2      the second set of minutiae.
    * @param maxDistance    the maximum distance between two minutiae to consider
    *                       them as overlapping.
    * @param maxOrientation the maximum difference of orientation between two
    *                       minutiae to consider them as overlapping.
    * @return the number of overlapping minutiae.
    */
    public static int matchingMinutiaeCount(List<int[]> minutiae1, List<int[]> minutiae2, int maxDistance,
                                            int maxOrientation) {

        // the number of matches found so far
        int nbMatches=0;

        // the number of remaining potential matches
        int remainingMatches = minutiae1.size();

        // for every minutia in the first list of minutiae...
        minutiaLoop1 : for (int[] curMinutia1 : minutiae1) {

            // if there are less remaining potential matches than needed for a valid match...
            if (nbMatches + remainingMatches < FOUND_THRESHOLD) {
                // ...stops looking for more matches
                return nbMatches;
            }

            // ...gets the characteristics of the current minutia in that list
            int minutiaRow1             = curMinutia1[0];
            int minutiaCol1             = curMinutia1[1];
            int minutiaOrientation1     = curMinutia1[2];

            // for every minutia in the second list of minutiae...
            for (int[] curMinutia2 : minutiae2) {

                // ...gets the characteristics of the current minutia in that list
                int minutiaRow2                 = curMinutia2[0];
                int minutiaCol2                 = curMinutia2[1];
                int minutiaOrientation2         = curMinutia2[2];

                // computes the distance between minutiae
                int minutiaRowDistance          = (minutiaRow1 - minutiaRow2) * (minutiaRow1 - minutiaRow2);
                int minutiaColDistance          = (minutiaCol1 - minutiaCol2) * (minutiaCol1 - minutiaCol2);
                double minutiaEuclideanDistance = Math.sqrt(minutiaRowDistance + minutiaColDistance);

                // computes hte difference in orientation between the two minutiae
                double orientation_difference = Math.abs(minutiaOrientation1 - minutiaOrientation2);

                // determines if the minutiae are close enough
                final boolean MINUTIAE_ARE_CLOSE_ENOUGH = (minutiaEuclideanDistance <= maxDistance)
                        && (orientation_difference <= maxOrientation);

                // if the number of matches has reached the required threshold...
                if (nbMatches >= FOUND_THRESHOLD) {
                    // ...stops looking for more matches
                    return nbMatches;
                }

                // the minutiae are within the allowed distance...
                if (MINUTIAE_ARE_CLOSE_ENOUGH) {
                    // ...counts them as a match
                    nbMatches++;
                    // moves on to the next loop iteration
                    continue minutiaLoop1;
                }
            }

            // one less possible match per loop iteration
            remainingMatches--;

        }

        // in the case where not enough matches have been found
        // returns the number of matches which has been found anyway
        return nbMatches;
    }

    /**
    * Compares the minutiae from two fingerprints.
    *
    * @param minutiae1 the list of minutiae of the first fingerprint.
    * @param minutiae2 the list of minutiae of the second fingerprint.
    * @return Returns <code>true</code> if they match and <code>false</code>
    *         otherwise.
    */
    public static boolean match(List<int[]> minutiae1, List<int[]> minutiae2) {

        // the number of matching minutiae which have been found
        int nbMatches = 0;

        // the remaining number of POTENTIAL minutia matches
        int potentialMatches = minutiae1.size();

        // for every minutia in the first list of minutiae...
        for (int[] curMinutia1 : minutiae1) {

            // ...if there are fewer matches remaining than needed for a valid match...
            if (nbMatches + potentialMatches < FOUND_THRESHOLD) {
                // ...stops looking for more matches and
                // considers the fingerprints to be different
                return false;
            }

            // ...gets the characteristics of the current minutia in that list
            int minutiaRow1             = curMinutia1[0];
            int minutiaCol1             = curMinutia1[1];
            int minutiaOrientation1     = curMinutia1[2];

            // for every minutia in the second list of minutiae...
            for (int[] curMinutia2 : minutiae2) {

                // ...gets the characteristics of the current minutia in that list
                int minutiaRow2         = curMinutia2[0];
                int minutiaCol2         = curMinutia2[1];
                int orientation_two = curMinutia2[2];

                int rotation=orientation_two-minutiaOrientation1;

                for (int angle=rotation-MATCH_ANGLE_OFFSET;angle<=rotation+MATCH_ANGLE_OFFSET;angle++) {

                    List<int[]> transformed_minutiae= applyTransformation(minutiae2, minutiaRow1, minutiaCol1,
                                                                minutiaRow2 - minutiaRow1,
                                                                minutiaCol2 - minutiaCol1,angle );

                    nbMatches = matchingMinutiaeCount(minutiae1, transformed_minutiae,
                                                        DISTANCE_THRESHOLD,ORIENTATION_THRESHOLD);
                    if (nbMatches>=FOUND_THRESHOLD){
                        return true ;
                    }
                }
            }

            potentialMatches--;
        }

        return false;
    }

    /**
     * Converts an image to a boolean array
     * @param path path (respective to Main.java) of the image
     * @return image in array form
     */
    public static boolean[][] getImage(String path) {
        // gets the image from the specified path
        return Helper.readBinary(path);
    }

    /**
     * Exports the array form of an image as a picture, emphasizing the minutiae and their orientation
     * @param image the image in boolean array form
     * @param fileName the name of the exported image
     */
    public static void processImage(boolean[][] image, String fileName) {

        // gets the size of the original image
        final int IMAGE_HEIGHT = image.length;
        final int IMAGE_WIDTH = image[0].length;

        // creates a copy of the original image so as not to modify it
        boolean[][] imageCopy = new boolean[IMAGE_HEIGHT][IMAGE_WIDTH];
        copy2DArray(image, imageCopy);

        // applies thinning to the initial image
        imageCopy = thin(image);

        // extracts the minutiae from the thinned image
        List<int[]> minutiae = extract(imageCopy);

        // converts the copy of the image to ARGB format
        int[][] ARGBImageCopy = Helper.fromBinary(imageCopy);

        // makes the minutiae more visible on the final image
        Helper.drawMinutia(ARGBImageCopy, minutiae);

        // saves the final image to a png
        Helper.writeARGB(fileName, ARGBImageCopy);

    }

    /**
     * Gets the column on which a minutia is located
     * @param minutia current minutia
     * @return x-coordinates of the minutia
     */
    private static int getMinutiaCol(int[] minutia) {
        // gets the x-coordinates of the minutia
        return minutia[1];
    }

    /**
     * Gets the row on which a minutia is located
     * @param minutia current minutia
     * @return y-coordinates of the minutia
     */
    private static int getMinutiaRow(int[] minutia) {
        // gets the y-coordinates of the minutia
        return minutia[0];
    }

    /**
     * Gets the angle orientation of a minutia
     * @param minutia current minutia
     * @return angle in degrees
     */
    private static int getMinutiaAngle(int[] minutia) {
        // gets the minutia's angle in degrees
        return minutia[2];
    }

    // region DEBUG METHODS

    // TODO Make this work
    private static void copy2DArray(boolean[][] original, boolean[][] copy) {

        // size of the array
        final int ORIGINAL_HEIGHT = original.length;
        final int ORIGINAL_WIDTH = original[0].length;

        // loops through every row in the array...
        for (int y = 0; y < ORIGINAL_HEIGHT; y++) {
            // ...and copies its content
            copy[y] = Arrays.copyOf(original[y], ORIGINAL_WIDTH);
        }
    }

    private static void emphasizeDifferences(int[][] original, int[][] toCompare) {

        // the size of the image
        final int IMAGE_HEIGHT = original.length;
        final int IMAGE_WIDTH = original[0].length;

        // loops through every row of the image...
        for (int y = 0; y < IMAGE_HEIGHT; y++) {
            // ...and every pixel for that row
            for (int x = 0; x < IMAGE_WIDTH; x++) {
                // ...if the pixels in both images are the same, and they are black
                /*if (original[y][x] == toCompare[y][x] && original[y][x] == "fff") {
                    // ...dims them
                    original[y][x] =
                }*/

                // ...if the pixels in both images are different...
//                if (original[y][x] != toCompare )
            }
        }
    }

    private static void createDebugImage(ArrayList<boolean[][]> debugImages) {

        // the number of debug images
        final int NUM_IMAGES = debugImages.size();

        // the size of each individual debug image
        final int IMAGE_HEIGHT = debugImages.get(0).length;
        final int IMAGE_WIDTH = debugImages.get(0)[0].length;

        // the width of the final image
        final int FINAL_IMAGE_WIDTH = IMAGE_WIDTH * NUM_IMAGES;

        // formatted debug image
        final boolean[][] FINAL_DEBUG_IMAGE = new boolean[IMAGE_HEIGHT][FINAL_IMAGE_WIDTH];

        // adds every debug image end-to-end to the final image
        for (int y = 0; y < IMAGE_HEIGHT; y++) {
            for (int x = 0; x < FINAL_IMAGE_WIDTH; x++) {

                // the current debug image to add
                int currentImage = x / IMAGE_WIDTH;

                // the index of the pixel to add from the debug image
                int currentPixel = x % (IMAGE_WIDTH);

                // adds the currently selected pixel to the final image
                FINAL_DEBUG_IMAGE[y][x] = debugImages.get(currentImage)[y][currentPixel];
            }
        }

        // saves the debug image under png format
        Helper.writeBinary("debug_fingerprint.png", FINAL_DEBUG_IMAGE);
    }

    public static void exportSkeleton(String imagePath, String imageName) {

        boolean[][] image = getImage(imagePath);

        Helper.writeBinary(imageName, thin(image));

    }

    // endregion
}
