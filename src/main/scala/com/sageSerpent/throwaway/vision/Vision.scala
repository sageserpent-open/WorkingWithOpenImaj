package com.sageSerpent.throwaway.vision

import org.opencv.highgui.VideoCapture
import org.opencv.core.Mat
import org.opencv.highgui.Highgui
import org.opencv.imgproc.Imgproc
import org.opencv.core.Size

object Vision extends App {

  val inputFilename = "input.mov"
  val outputFilenameTemplate = "output_%d.jpg"
  
  nu.pattern.OpenCV.loadShared()
  System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME)
  
  println ("We're off ....")
  
  val videoCapture = new VideoCapture(inputFilename)
  
  val frame = new Mat();
  
  var imageIndex = 0;
  
  while (videoCapture.read(frame))
  {
    println ("Got a frame, height: %s, width: %s".format(frame.height, frame.width))
    
    val resizedFrame = new Mat();
    
    Imgproc.resize(frame, resizedFrame, new Size(0, 0), 1, 3, Imgproc.INTER_CUBIC)
    
    val imageName = outputFilenameTemplate.format(imageIndex)
    
    Highgui.imwrite(imageName, resizedFrame)
    
    imageIndex += 1
  }
}