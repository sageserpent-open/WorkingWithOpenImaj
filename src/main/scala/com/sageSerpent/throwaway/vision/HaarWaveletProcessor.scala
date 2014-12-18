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
          val startX: Int
          val y: Int
          val lengthOfSectionInXDirectionBeingProcessed: Int
          def gapBetweenWaveletCoefficientAndItsCorrespondingLowResolutionPixel = lengthOfSectionInXDirectionBeingProcessed / 2
          def getImagePixel(x: Int): Float
          def setImagePixel(x: Int, value: Float): Unit

          def subContext(): TranspositionContext // No need for F-bounds or 'this.type'.

          def hiveOffWaveletCoefficientsAcrossWidth() {
            if (1 < lengthOfSectionInXDirectionBeingProcessed) {
              for (waveletCoefficientIndex <- 0 until gapBetweenWaveletCoefficientAndItsCorrespondingLowResolutionPixel) {
                val lhsOfPairIndex = startX + 2 * waveletCoefficientIndex
                val rhsOfPairIndex = 1 + lhsOfPairIndex
                val lhs = getImagePixel(lhsOfPairIndex)
                val rhs = getImagePixel(rhsOfPairIndex)
                val waveletCoefficient = normalisation * 0.5 * (lhs - rhs)
                val lowResolution = normalisation * 0.5 * (lhs + rhs)
                val lowResolutionIndex = gapBetweenWaveletCoefficientAndItsCorrespondingLowResolutionPixel + waveletCoefficientIndex
                mutableBufferForResultRowPixels(waveletCoefficientIndex) = waveletCoefficient.toFloat
                mutableBufferForResultRowPixels(lowResolutionIndex) = lowResolution.toFloat
              }
              for (bufferIndex <- 0 until lengthOfSectionInXDirectionBeingProcessed) {
                setImagePixel(startX + bufferIndex, mutableBufferForResultRowPixels(bufferIndex))
              }
              subContext().hiveOffWaveletCoefficientsAcrossWidth()
            }
          }
        }

        class NoTransposition(val y: Int, val startX: Int) extends TranspositionContext {
          val lengthOfSectionInXDirectionBeingProcessed: Int = image.getWidth() - startX

          def getImagePixel(x: Int): Float = image.getBand(band).pixels(y)(x)
          def setImagePixel(x: Int, value: Float) {
            image.getBand(band).pixels(y)(x) = value
          }

          def subContext(): TranspositionContext = new NoTransposition(y,
            gapBetweenWaveletCoefficientAndItsCorrespondingLowResolutionPixel + startX)
        }
        
        class Transposition(val y: Int, val startX: Int) extends TranspositionContext {
          val lengthOfSectionInXDirectionBeingProcessed: Int = image.getHeight() - startX

          def getImagePixel(x: Int): Float = image.getBand(band).pixels(x)(y)
          def setImagePixel(x: Int, value: Float) {
            image.getBand(band).pixels(x)(y) = value
          }

          def subContext(): TranspositionContext = new Transposition(y,
            gapBetweenWaveletCoefficientAndItsCorrespondingLowResolutionPixel + startX)
        }        

        for (row <- 0 until image.getHeight()) {
          new NoTransposition(row, 0).hiveOffWaveletCoefficientsAcrossWidth()
        }
        
        for (column <- 0 until image.getWidth()) {
          new Transposition(column, 0).hiveOffWaveletCoefficientsAcrossWidth()
        }        
      }
    }
  }
}