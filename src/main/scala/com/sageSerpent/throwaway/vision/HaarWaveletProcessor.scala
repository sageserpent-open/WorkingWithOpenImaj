package com.sageSerpent.throwaway.vision


import org.openimaj.image.processor.ImageProcessor
import org.openimaj.image.MBFImage


object HaarWaveletProcessor extends HaarWaveletImplementationFactoring {
  def apply() = new ImageProcessor[MBFImage] {
    def processImage(image: MBFImage) {
      processingFor(image).hiveOffWaveletCoefficientsAlongSectionInXDirection()
    }
  }
}



