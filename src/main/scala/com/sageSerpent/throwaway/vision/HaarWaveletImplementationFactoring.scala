package com.sageSerpent.throwaway.vision

import org.openimaj.image.MBFImage

protected abstract class HaarWaveletImplementationFactoring {
  def processingFor(image: MBFImage) = {
    if (!BargainBasement.isPowerOfTwo(image.getHeight()))
      throw new Exception("Height must be a power of two")
    if (!BargainBasement.isPowerOfTwo(image.getWidth()))
      throw new Exception("Width must be a power of two")

    val normalisation = Math.sqrt(2)
    val mutableBufferForResultPixels = Array.ofDim[Float](image.getWidth() max image.getHeight())

    abstract class ProcessingStep {
      val startX: Int
      val lengthOfSectionInXDirectionBeingProcessed: Int
      val numberOfPixelsInYDirection: Int
      def gapBetweenWaveletCoefficientAndItsCorrespondingLowResolutionPixel = lengthOfSectionInXDirectionBeingProcessed / 2
      def getImagePixel(band: Int, x: Int, y: Int): Float
      def setImagePixel(band: Int, x: Int, y: Int, value: Float): Unit

      def subContext(): ProcessingStep // No need for F-bounds or 'this.type'.
      
      val widthOfFinalLowResolutionImage = 1

      def hiveOffWaveletCoefficientsAlongSectionInXDirection() {
        if (widthOfFinalLowResolutionImage < lengthOfSectionInXDirectionBeingProcessed) {
          for (band <- 0 until image.numBands()) {
            for (y <- 0 until numberOfPixelsInYDirection) {
              for (waveletCoefficientIndex <- 0 until gapBetweenWaveletCoefficientAndItsCorrespondingLowResolutionPixel) {
                val lhsOfPairIndex = startX + 2 * waveletCoefficientIndex
                val rhsOfPairIndex = 1 + lhsOfPairIndex
                val lhs = getImagePixel(band, lhsOfPairIndex, y)
                val rhs = getImagePixel(band, rhsOfPairIndex, y)
                val waveletCoefficient = normalisation * 0.5 * (lhs - rhs)
                val lowResolution = normalisation * 0.5 * (lhs + rhs)
                val lowResolutionIndex = gapBetweenWaveletCoefficientAndItsCorrespondingLowResolutionPixel + waveletCoefficientIndex
                mutableBufferForResultPixels(waveletCoefficientIndex) = waveletCoefficient.toFloat
                mutableBufferForResultPixels(lowResolutionIndex) = lowResolution.toFloat
              }
              for (bufferIndex <- 0 until lengthOfSectionInXDirectionBeingProcessed) {
                setImagePixel(band, startX + bufferIndex, y, mutableBufferForResultPixels(bufferIndex))
              }
            }
          }
          subContext().hiveOffWaveletCoefficientsAlongSectionInXDirection()
        }
      }

      def rebuildDetailFromWaveletCoefficientsAlongSectionInXDirection() {
        if (widthOfFinalLowResolutionImage < lengthOfSectionInXDirectionBeingProcessed) {
          subContext().rebuildDetailFromWaveletCoefficientsAlongSectionInXDirection()
          for (band <- 0 until image.numBands()) {
            for (y <- 0 until numberOfPixelsInYDirection) {
              for (xOffset <- 0 until gapBetweenWaveletCoefficientAndItsCorrespondingLowResolutionPixel) {
                val waveletCoefficientIndex = startX + xOffset
                val lowResolutionIndex = gapBetweenWaveletCoefficientAndItsCorrespondingLowResolutionPixel + waveletCoefficientIndex
                val waveletCoefficient = getImagePixel(band, waveletCoefficientIndex, y)
                val lowResolution = getImagePixel(band, lowResolutionIndex, y)
                val lhs = (waveletCoefficient + lowResolution) / normalisation
                val lhsIndex = 2 * xOffset
                mutableBufferForResultPixels(lhsIndex) = lhs.toFloat
                val rhs = (lowResolution - waveletCoefficient) / normalisation
                val rhsIndex = 1 + lhsIndex
                mutableBufferForResultPixels(rhsIndex) = rhs.toFloat
              }
              for (bufferIndex <- 0 until lengthOfSectionInXDirectionBeingProcessed) {
                setImagePixel(band, startX + bufferIndex, y, mutableBufferForResultPixels(bufferIndex))
              }
            }
          }
        }
      }
    }

    class HorizontalProcessingStep(val startX: Int, startY: Int) extends ProcessingStep {
      val lengthOfSectionInXDirectionBeingProcessed: Int = image.getWidth() - startX
      val numberOfPixelsInYDirection: Int = image.getHeight()

      def getImagePixel(band: Int, x: Int, y: Int): Float = image.getBand(band).pixels(y)(x)
      def setImagePixel(band: Int, x: Int, y: Int, value: Float) {
        image.getBand(band).pixels(y)(x) = value
      }

      def subContext(): ProcessingStep = new VerticalProcessingStep(startY, gapBetweenWaveletCoefficientAndItsCorrespondingLowResolutionPixel + startX)
    }

    class VerticalProcessingStep(val startX: Int, startY: Int) extends ProcessingStep {
      val lengthOfSectionInXDirectionBeingProcessed: Int = image.getHeight() - startX
      val numberOfPixelsInYDirection: Int = image.getWidth()

      def getImagePixel(band: Int, x: Int, y: Int): Float = image.getBand(band).pixels(x)(y)
      def setImagePixel(band: Int, x: Int, y: Int, value: Float) {
        image.getBand(band).pixels(x)(y) = value
      }

      def subContext(): ProcessingStep = new HorizontalProcessingStep(startY, gapBetweenWaveletCoefficientAndItsCorrespondingLowResolutionPixel + startX)
    }
    new HorizontalProcessingStep(0, 0)
  }
}
