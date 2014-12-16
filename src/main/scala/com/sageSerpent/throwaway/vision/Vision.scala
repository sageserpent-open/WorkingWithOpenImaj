package com.sageSerpent.throwaway.vision

import java.io.File

import scala.collection.JavaConversions.asScalaIterator
import scala.collection.JavaConversions.iterableAsScalaIterable

import org.openimaj.image.ImageUtilities
import org.openimaj.video.capture.VideoCapture

import javax.imageio.ImageIO

object Vision extends App {

  val inputFilename = "input.mov"
  val outputFilenameTemplate = "output_%d.jpg"

  for (thingie <- ImageIO.getImageWritersByFormatName("JPEG")) {
    println(thingie)
  }

  println("We're off ....")

  var videoDevices = VideoCapture.getVideoDevices()

  val videoCapture = new VideoCapture(256, 256, 1, videoDevices.get(0))

  val images = for (image <- videoCapture.toStream) yield image.clone

  for ((image, index) <- images.take(80).zipWithIndex) {
    println("Got a frame, height: %s, width: %s".format(image.getHeight(), image.getWidth()))

    val imageName = outputFilenameTemplate.format(index)

    ImageUtilities.write(image, new File(imageName))
  }
}