package com.sageSerpent.throwaway.vision

object BargainBasement {
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
}