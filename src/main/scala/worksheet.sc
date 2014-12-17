import java.io.File
import java.nio.file.Paths

import org.openimaj.image.ImageUtilities
import org.openimaj.image.DisplayUtilities
import org.openimaj.image.processing.edges.CannyEdgeDetector
import org.openimaj.image.processor.ImageProcessor
import org.openimaj.image.FImage
import org.openimaj.image.MBFImage

import Stream._

object worksheet {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet

  val xs = 0 to 256                               //> xs  : scala.collection.immutable.Range.Inclusive = Range(0, 1, 2, 3, 4, 5, 6
                                                  //| , 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 2
                                                  //| 6, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 4
                                                  //| 5, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 6
                                                  //| 4, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 8
                                                  //| 3, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101,
                                                  //|  102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 
                                                  //| 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127, 128, 129, 130, 131, 1
                                                  //| 32, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143, 144, 145, 146, 14
                                                  //| 7, 148, 149, 150, 151, 152, 153, 154, 155, 156, 157, 158, 159, 160, 161, 162
                                                  //| , 163, 164, 165, 166, 167, 168, 169, 170, 171, 172, 173, 174, 175, 176, 177,
                                                  //|  178, 179, 180, 181, 182, 183, 184, 185, 186, 187, 188, 189, 190, 191, 192, 
                                                  //| 193, 194, 195, 196, 197,
                                                  //| Output exceeds cutoff limit.

  def isPowerOfTwo(x: Int) = {
    def isEvenAfterAllHalvingSteps(x: Int): Boolean =
      if (1 == x)
        true
      else if (0 != (1 & x))
        false
      else
        isEvenAfterAllHalvingSteps(x >> 1)
    0 < x && isEvenAfterAllHalvingSteps(x)
  }                                               //> isPowerOfTwo: (x: Int)Boolean

  xs.filter(isPowerOfTwo)                         //> res0: scala.collection.immutable.IndexedSeq[Int] = Vector(1, 2, 4, 8, 16, 32
                                                  //| , 64, 128, 256)

  Paths.get(".").toAbsolutePath()                 //> res1: java.nio.file.Path = /Applications/eclipse/Eclipse.app/Contents/MacOS/
                                                  //| .

  val image = ImageUtilities.readMBF(new File("/Users/gerardMurphy/Documents/workspace/vision/output_4.jpg"))
                                                  //> image  : org.openimaj.image.MBFImage = org.openimaj.image.MBFImage@68e965f5

  image.colourSpace                               //> res2: org.openimaj.image.colour.ColourSpace = RGB

  val blackAndWhiteImage = image.flatten()        //> blackAndWhiteImage  : org.openimaj.image.FImage = +0.618 +0.614 +0.617 +0.61
                                                  //| 3 +0.613 +0.617 +0.620 +0.624 +0.616 +0.617 +0.622 +0.624 +0.617 +0.605 +0.5
                                                  //| 97 +0.593 +0.604 ... +0.511 +0.510 +0.498 +0.506 +0.516 +0.515 +0.495 +0.495
                                                  //|  +0.518 +0.533 +0.519 +0.497 +0.485 +0.501 +0.519 
                                                  //| +0.614 +0.616 +0.613 +0.613 +0.613 +0.613 +0.620 +0.620 +0.622 +0.617 +0.618
                                                  //|  +0.618 +0.613 +0.608 +0.597 +0.600 +0.604 ... +0.511 +0.506 +0.490 +0.502 +
                                                  //| 0.512 +0.514 +0.499 +0.499 +0.510 +0.520 +0.511 +0.501 +0.489 +0.501 +0.511 
                                                  //| 
                                                  //| +0.610 +0.610 +0.609 +0.612 +0.612 +0.612 +0.616 +0.616 +0.626 +0.624 +0.617
                                                  //|  +0.610 +0.608 +0.605 +0.604 +0.605 +0.603 ... +0.511 +0.498 +0.482 +0.490 +
                                                  //| 0.508 +0.510 +0.498 +0.502 +0.502 +0.501 +0.499 +0.501 +0.497 +0.497 +0.499 
                                                  //| 
                                                  //| +0.605 +0.609 +0.612 +0.612 +0.612 +0.612 +0.612 +0.608 +0.630 +0.626 +0.613
                                                  //|  +0.604 +0.603 +0.604 +0.605 +0.612 +0.600 ... +0.508 +0.494 +0.475 +0.485 +
                                                  //| 0.505 +0.510 +0.498 +0.502 +0.493 +0.485
                                                  //| Output exceeds cutoff limit.

  val imageProcessor = new ImageProcessor[MBFImage] {
    def processImage(image: MBFImage) {
      if (!isPowerOfTwo(image.getHeight()))
        throw new Exception("Height must be a power of two")
      if (!isPowerOfTwo(image.getWidth()))
        throw new Exception("Width must be a power of two")
      val normalisation = Math.sqrt(2)
      val mutableBufferForResultRowPixels = Array.ofDim[Float](image.getWidth())
      for (band <- 0 until image.numBands()) {
        def hiveOffWaveletCoefficientsAcrossWidth(startColumn: Int, row: Int) {
          val widthOfSectionBeingProcessed = image.getWidth() - startColumn

          if (32 < widthOfSectionBeingProcessed) {
            val gapBetweenWaveletCoefficientAndItsCorrespondingLowResolutionPixel = widthOfSectionBeingProcessed / 2
            for (waveletCoefficientIndex <- 0 until gapBetweenWaveletCoefficientAndItsCorrespondingLowResolutionPixel) {
              val lhsOfPairIndex = startColumn + 2 * waveletCoefficientIndex
              val rhsOfPairIndex = 1 + lhsOfPairIndex
              val lhs = image.getBand(band).pixels(row)(lhsOfPairIndex)
              val rhs = image.getBand(band).pixels(row)(rhsOfPairIndex)
              val waveletCoefficient = normalisation * 0.5 * (lhs - rhs)
              val lowResolution = normalisation * 0.5 * (lhs + rhs)
              val lowResolutionIndex = gapBetweenWaveletCoefficientAndItsCorrespondingLowResolutionPixel + waveletCoefficientIndex
              mutableBufferForResultRowPixels(waveletCoefficientIndex) = waveletCoefficient.toFloat
              mutableBufferForResultRowPixels(lowResolutionIndex) = lowResolution.toFloat
            }
            for (bufferIndex <- 0 until widthOfSectionBeingProcessed){
            	image.getBand(band).pixels(row)(startColumn + bufferIndex) = mutableBufferForResultRowPixels(bufferIndex)
            }
            hiveOffWaveletCoefficientsAcrossWidth(gapBetweenWaveletCoefficientAndItsCorrespondingLowResolutionPixel + startColumn, row)
          }
        }
        for (row <- 0 until image.getHeight()) {
          hiveOffWaveletCoefficientsAcrossWidth(0, row)
        }
      }
    }
  }                                               //> imageProcessor  : org.openimaj.image.processor.ImageProcessor[org.openimaj.
                                                  //| image.MBFImage] = worksheet$$anonfun$main$1$$anon$1@7e9131d5

  image.processInplace(imageProcessor)            //> res3: org.openimaj.image.MBFImage = org.openimaj.image.MBFImage@68e965f5

  DisplayUtilities.display(image)                 //> res4: javax.swing.JFrame = javax.swing.JFrame[frame0,0,23,1024x1046,layout=
                                                  //| java.awt.BorderLayout,title=Image: 0,maximized,defaultCloseOperation=HIDE_O
                                                  //| N_CLOSE,rootPane=javax.swing.JRootPane[,0,22,1024x1024,layout=javax.swing.J
                                                  //| RootPane$RootLayout,alignmentX=0.0,alignmentY=0.0,border=,flags=16777675,ma
                                                  //| ximumSize=,minimumSize=,preferredSize=],rootPaneCheckingEnabled=true]\

}