package com.sageSerpent.throwaway.vision

import org.openimaj.image.MBFImage
import org.openimaj.image.processor.ImageProcessor

object HaarWaveletProcessor {
  def apply() = new ImageProcessor[MBFImage] {
    def processImage(image: MBFImage) {
      processingFor(image).hiveOffWaveletCoefficientsAlongSectionInXDirection()
    }
  }

  def processingFor(image: MBFImage) = {
    if (!BargainBasement.isPowerOfTwo(image.getHeight()))
      throw new Exception("Height must be a power of two")
    if (!BargainBasement.isPowerOfTwo(image.getWidth()))
      throw new Exception("Width must be a power of two")

    val normalisation = Math.sqrt(2)
    val mutableBufferForResultRowPixels = Array.ofDim[Float](image.getWidth() max image.getHeight())

    abstract class ProcessingStep
    {
      val startX: Int
      val lengthOfSectionInXDirectionBeingProcessed: Int
      val numberOfPixelsInYDirection: Int
      def gapBetweenWaveletCoefficientAndItsCorrespondingLowResolutionPixel = lengthOfSectionInXDirectionBeingProcessed / 2
      def getImagePixel(band: Int, x: Int, y: Int): Float
      def setImagePixel(band: Int, x: Int, y: Int, value: Float): Unit

      def subContext(): ProcessingStep // No need for F-bounds or 'this.type'.

      def hiveOffWaveletCoefficientsAlongSectionInXDirection() {
        if (1 < lengthOfSectionInXDirectionBeingProcessed) {
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
                mutableBufferForResultRowPixels(waveletCoefficientIndex) = waveletCoefficient.toFloat
                mutableBufferForResultRowPixels(lowResolutionIndex) = lowResolution.toFloat
              }
              for (bufferIndex <- 0 until lengthOfSectionInXDirectionBeingProcessed) {
                setImagePixel(band, startX + bufferIndex, y, mutableBufferForResultRowPixels(bufferIndex))
              }
            }
          }
          subContext().hiveOffWaveletCoefficientsAlongSectionInXDirection()
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