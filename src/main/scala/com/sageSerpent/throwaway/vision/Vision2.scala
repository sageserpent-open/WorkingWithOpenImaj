package com.sageSerpent.throwaway.vision

import org.openimaj.video._
import org.openimaj.video.capture._
import org.openimaj.image.ImageUtilities

import scala.collection.JavaConversions._
import java.io.File

import javax.imageio.ImageIO

object Vision2 extends App {

  val inputFilename = "input.mov"
  val outputFilenameTemplate = "output_%d.jpg"
  
  for (thingie <- ImageIO.getImageWritersByFormatName("JPEG"))
  {
    println(thingie)
  }
  
  println ("We're off ....")
  
  var videoDevices = VideoCapture.getVideoDevices()
  
  val videoCapture = new VideoCapture(256, 256, videoDevices.get(0))
  
  val image = videoCapture.getNextFrame()
  
    val imageName = outputFilenameTemplate.format(0)
    
    ImageUtilities.write(image, new File(imageName))  
  
  for ((image, index) <- videoCapture.zipWithIndex.take(1)){
    println ("Got a frame, height: %s, width: %s".format(image.getHeight(), image.getWidth()))
    
    val imageName = outputFilenameTemplate.format(index)
    
    ImageUtilities.write(image, new File(imageName))
  }
}