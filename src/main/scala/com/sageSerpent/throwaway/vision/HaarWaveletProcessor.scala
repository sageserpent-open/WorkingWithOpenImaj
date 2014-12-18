package com.sageSerpent.throwaway.vision

import org.openimaj.image.MBFImage
import org.openimaj.image.processor.ImageProcessor

object HaarWaveletProcessor {
  def apply() = new ImageProcessor[MBFImage] {
    def processImage(image: MBFImage) {
      if (!BargainBasement.isPowerOfTwo(image.getHeight()))
        throw new Exception("Height must be a power of two")
      if (!BargainBasement.isPowerOfTwo(image.getWidth()))
        throw new Exception("Width must be a power of two")
      val normalisation = Math.sqrt(2)
      val mutableBufferForResultRowPixels = Array.ofDim[Float](image.getWidth() max image.getHeight())

      for (band <- 0 until image.numBands()) {
        trait TranspositionContext {
          val startX: Int
          val lengthOfSectionInXDirectionBeingProcessed: Int
          val numberOfPixelsInYDirection: Int
          def gapBetweenWaveletCoefficientAndItsCorrespondingLowResolutionPixel = lengthOfSectionInXDirectionBeingProcessed / 2
          def getImagePixel(x: Int, y: Int): Float
          def setImagePixel(x: Int, y: Int, value: Float): Unit

          def subContext(): TranspositionContext // No need for F-bounds or 'this.type'.

          def hiveOffWaveletCoefficientsAlongSectionInXDirection() {
            if (1 < lengthOfSectionInXDirectionBeingProcessed) {
              for (y <- 0 until numberOfPixelsInYDirection) {
                for (waveletCoefficientIndex <- 0 until gapBetweenWaveletCoefficientAndItsCorrespondingLowResolutionPixel) {
                  val lhsOfPairIndex = startX + 2 * waveletCoefficientIndex
                  val rhsOfPairIndex = 1 + lhsOfPairIndex
                  val lhs = getImagePixel(lhsOfPairIndex, y)
                  val rhs = getImagePixel(rhsOfPairIndex, y)
                  val waveletCoefficient = normalisation * 0.5 * (lhs - rhs)
                  val lowResolution = normalisation * 0.5 * (lhs + rhs)
                  val lowResolutionIndex = gapBetweenWaveletCoefficientAndItsCorrespondingLowResolutionPixel + waveletCoefficientIndex
                  mutableBufferForResultRowPixels(waveletCoefficientIndex) = waveletCoefficient.toFloat
                  mutableBufferForResultRowPixels(lowResolutionIndex) = lowResolution.toFloat
                }
                for (bufferIndex <- 0 until lengthOfSectionInXDirectionBeingProcessed) {
                  setImagePixel(startX + bufferIndex, y, mutableBufferForResultRowPixels(bufferIndex))
                }
              }
              subContext().hiveOffWaveletCoefficientsAlongSectionInXDirection()
            }
          }
        }

        class NoTransposition(val startX: Int) extends TranspositionContext {
          val lengthOfSectionInXDirectionBeingProcessed: Int = image.getWidth() - startX
          val numberOfPixelsInYDirection: Int = image.getHeight()

          def getImagePixel(x: Int, y: Int): Float = image.getBand(band).pixels(y)(x)
          def setImagePixel(x: Int, y: Int, value: Float) {
            image.getBand(band).pixels(y)(x) = value
          }

          def subContext(): TranspositionContext = new NoTransposition(gapBetweenWaveletCoefficientAndItsCorrespondingLowResolutionPixel + startX)
        }

        class Transposition(val startX: Int) extends TranspositionContext {
          val lengthOfSectionInXDirectionBeingProcessed: Int = image.getHeight() - startX
          val numberOfPixelsInYDirection: Int = image.getWidth()

          def getImagePixel(x: Int, y: Int): Float = image.getBand(band).pixels(x)(y)
          def setImagePixel(x: Int, y: Int, value: Float) {
            image.getBand(band).pixels(x)(y) = value
          }

          def subContext(): TranspositionContext = new Transposition(gapBetweenWaveletCoefficientAndItsCorrespondingLowResolutionPixel + startX)
        }

        new NoTransposition(0).hiveOffWaveletCoefficientsAlongSectionInXDirection()

        new Transposition(0).hiveOffWaveletCoefficientsAlongSectionInXDirection()
      }
    }
  }
}