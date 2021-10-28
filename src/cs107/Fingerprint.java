package cs107;

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
    public final int ORIENTATION_DISTANCE = 16;

  /**
   * The maximum distance between two minutiae to be considered matching.
   */
    public final int DISTANCE_THRESHOLD = 5;

  /**
   * The number of matching minutiae needed for two fingerprints to be considered
   * identical.
   */
    public final int FOUND_THRESHOLD = 20;

  /**
   * The distance between two angle to be considered identical.
   */
    public final int ORIENTATION_THRESHOLD = 20;

  /**
   * The offset in each direction for the rotation to test when doing the
   * matching.
   */
    public final int MATCH_ANGLE_OFFSET = 2;

    // TODO implement properly as part of a custom image class
    private static void pixelOutOfBoundsError() {

    }

    /**
     * Returns true if the selected pixel is black
     * @param image image which contains the pixel
     * @param row y-coordinates of the pixel
     * @param col x-coordinates of the pixel
     * @return whether the pixel is black
     * @// TODO: 28.10.21 add index checking functionalities to fully-fledged image class
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
     * @param image image which contains the pixel
     * @param row y-coordinates of the pixel
     * @param col x-coordinates of the pixel
     * @return whether the pixel is white
     */
    public static boolean isWhite(boolean[][] image, int row, int col) {
        return !isBlack(image, row, col);
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
        pixelOutOfBoundsError();

        // the values around the current pixel
        boolean[] neighBoringValues = new boolean[8];

        // whether the current pixels is on a border
        boolean hasPixelAbove = row != 0;
        boolean hasPixelBelow = row != image.length-1;
        boolean hasPixelToLeft = col != 0;
        boolean hasPixelToRight = col != image[0].length-1;

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

        // for debug purposes, used to generate an image of the fingerprint for each thinning step taken
        int i = 0;

        // while we can still apply some thinning to the image...
        while (!identical(previousImage, currentImage)) {

            // applies thinning to the image
            previousImage = thinningStep(currentImage, 1); // step 1
            currentImage = thinningStep(previousImage, 0); // step 2

            /*// generates up to the first 20 steps in thinning the fingerprint
            if (i < 20) {
                // saves the 1st step
                Helper.writeBinary("./debug_fingerprints/debug_skeleton" + i + ".png", previousImage);
                i++;
                // saves the 2nd step
                Helper.writeBinary("./debug_fingerprints/debug_skeleton" + i + ".png", currentImage);
                i++;
            }*/
        }

        // returns the thinned image
        return currentImage;
    }

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
    public static boolean[][] connectedPixels(boolean[][] image, int row, int col, int distance) {
      //TODO implement
      return null;
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
      //TODO implement
      return 0;
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
    */
    public static double computeAngle(boolean[][] connectedPixels, int row, int col, double slope) {
      //TODO implement
      return 0;
    }

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
      //TODO implement
      return 0;
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
      //TODO implement
      return null;
    }

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
      //TODO implement
      return null;
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
      //TODO implement
      return null;
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
      //TODO implement
      return null;
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
      //TODO implement
      return null;
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
      //TODO implement
      return 0;
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
      //TODO implement
      return false;
    }
}
