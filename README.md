# Fingerprint Project
*by Eliot McNab & Mehdi Zoghlami*

*Neither of us has ever coded in Java previously*

## Added Methods :

* _**Refactoring**_

	* __isBlack__ : determines if a pixel is black
	* __isWhite__ : determiens if a pixel is white
	* __hasPixelToRight__ : determines if there *exists* a black pixel to the right of the current pixel
	* __hasPixelToLeft__ : determines if there *exists* a black pixel to the left of the current pixel
	* __haspixelBelow__ : determines if there *exists* a black pixel below the current pixel
	* __hasPixelAbove__ : determines if there *exists* a black pixel above the current pixel
	* __checkUpperPixels__ : Checks the pixels above the current pixel, if there are any, and adds them to the list of neighbouring pixels
	* __checkRowPixels__ : Checks the pixels on the same row as the current pixel, if there are any, and adds them to the list of neighbouring pixels
	* __checkLowerPixels__ : Checks the pixels below the current pixel,if there are any, and adds them to the list of neighbouring pixels
	* __connected__ : gets the pixels which are connected to a minutia
	*  __hasBlackNeighbour__ : detemines if a minutia has black pixels around it
	* __slopeParameters__ : gets the parameters (x², y² and x*y) necessary to compute the slope of a minutia
	* __getEuclideanDistance__ : determines the euclidian distance between two minutiae
	* __getOrientationDifference__ : gets the absolute difference in orientation between two minutiae

* _**Added Features**_

	* __processImage__ : Exports the array form of an image as a picture, emphasizing the minutiae and their orientation
	* __copy2DArray__ : Copies a 2D array into another array (/!\ does not create a new copy of the array) 
	* __getImage__ : Converts an image to a boolean array
	* __exportSkeleton__ : Exports an image under skeleton form

* _**Image Class**_

	can be found in Advanced_Fingerpint.zip

	*The Initial idea was to create a new data model to more efficiently represent an image. By saving an image as an array of bytes, 
	it is possiblle to consider 8 pixels at a time instead of just 1. Moreover, empty pixels can be more easily eliminated since the 
	value of an empty byte is 0. The ability to manipulated bytes through bitwise opearions would aslo have proven useful towards
	retrieving and editing the value of neighbouring pixels. This project was however laid to rest once it became clear it woulld 
	not be accepted as part of the project*

	* __Image__ (constructor) : creates a new image based on the path to that image
	* __toByteImage__ : creates the byte representation of an image
	* __fillByteRow__ : Generates a row of bytes representing a row of pixels in the image
	* __getPixelsRange__ :  Gets the values of pixels within a certain range of the image
	* __colorToByte__ : Converts the color value of a set of pixels into a binary representation of each pixel (0 for white, 1 for black)
	* __toBit__ :  Converts the RGB value of a pixel to a binary value  
	* __displayByteImage__ :  Displays the bytes composing the image
	* __formatByte__ : Converts a byte to a string and fixes its length to 8

	* __ImageNotGeneratedError__ :  custom error class used to specify inheriting from RuntimeException, used to specify that  a pixel's value can't be reached because 
	the byte form of the image hasn't been generated yet

	* __PixelOutOfBoundError__ : custom error class inheriting from RuntimeException, used to specify whern a pixel's coorinated are invalid