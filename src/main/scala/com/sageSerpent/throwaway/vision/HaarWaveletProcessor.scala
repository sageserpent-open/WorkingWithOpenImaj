package com.sageSerpent.throwaway.vision

import org.openimaj.image.MBFImage
import org.openimaj.image.processor.ImageProcessor

object HaarWaveletProcessor {
  def isPowerOfTwo(x: Int) = {
    def isEvenAfterAllHalvingSteps(x: Int): Boolean =
      if (1 == x)
        true
      else if (0 != (1 & x))
        false
      else
        isEvenAfterAllHalvingSteps(x >> 1)
    0 < x && isEvenAfterAllHalvingSteps(x)
  }

  def apply() = new ImageProcessor[MBFImage] {
    def processImage(image: MBFImage) {
      if (!isPowerOfTwo(image.getHeight()))
        throw new Exception("Height must be a power of two")
      if (!isPowerOfTwo(image.getWidth()))
        throw new Exception("Width must be a power of two")
      val normalisation = Math.sqrt(2)
      val mutableBufferForResultRowPixels = Array.ofDim[Float](image.getWidth() max image.getHeight())

      for (band <- 0 until image.numBands()) {
        trait TranspositionContext {
          val startColumn: Int
          val row: Int
          val widthOfSectionBeingProcessed: Int
          def gapBetweenWaveletCoefficientAndItsCorrespondingLowResolutionPixel = widthOfSectionBeingProcessed / 2
          def getImagePixel(column: Int): Float
          def setImagePixel(column: Int, value: Float): Unit

          def subContext(): TranspositionContext // No need for F-bounds or 'this.type'.

          def hiveOffWaveletCoefficientsAcrossWidth() {
            //println(startColumn, widthOfSectionBeingProcessed, gapBetweenWaveletCoefficientAndItsCorrespondingLowResolutionPixel)
            if (128 < widthOfSectionBeingProcessed) {
              for (waveletCoefficientIndex <- 0 until gapBetweenWaveletCoefficientAndItsCorrespondingLowResolutionPixel) {
                val lhsOfPairIndex = startColumn + 2 * waveletCoefficientIndex
                val rhsOfPairIndex = 1 + lhsOfPairIndex
                val lhs = getImagePixel(lhsOfPairIndex)
                val rhs = getImagePixel(rhsOfPairIndex)
                val waveletCoefficient = normalisation * 0.5 * (lhs - rhs)
                val lowResolution = normalisation * 0.5 * (lhs + rhs)
                val lowResolutionIndex = gapBetweenWaveletCoefficientAndItsCorrespondingLowResolutionPixel + waveletCoefficientIndex
                mutableBufferForResultRowPixels(waveletCoefficientIndex) = waveletCoefficient.toFloat
                mutableBufferForResultRowPixels(lowResolutionIndex) = lowResolution.toFloat
              }
              for (bufferIndex <- 0 until widthOfSectionBeingProcessed) {
                setImagePixel(startColumn + bufferIndex, mutableBufferForResultRowPixels(bufferIndex))
              }
              subContext().hiveOffWaveletCoefficientsAcrossWidth()
            }
          }
        }

        class NoTransposition(val row: Int, val startColumn: Int) extends TranspositionContext {
          val widthOfSectionBeingProcessed: Int = image.getWidth() - startColumn

          def getImagePixel(column: Int): Float = image.getBand(band).pixels(row)(column)
          def setImagePixel(column: Int, value: Float) {
            image.getBand(band).pixels(row)(column) = value
          }

          def subContext(): TranspositionContext = new NoTransposition(row,
            gapBetweenWaveletCoefficientAndItsCorrespondingLowResolutionPixel + startColumn)
        }

        for (row <- 0 until image.getHeight()) {
          new NoTransposition(row, 0).hiveOffWaveletCoefficientsAcrossWidth()
        }
      }
    }
  }
}