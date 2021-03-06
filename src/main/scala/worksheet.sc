import java.io.File
import java.nio.file.Paths
import org.openimaj.image.ImageUtilities
import org.openimaj.image.DisplayUtilities
import org.openimaj.image.processing.edges.CannyEdgeDetector
import org.openimaj.image.processor.ImageProcessor
import org.openimaj.image.FImage
import org.openimaj.image.MBFImage
import com.sageSerpent.throwaway.vision._
import Stream._


object worksheet {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet

  Paths.get(".").toAbsolutePath()                 //> res0: java.nio.file.Path = /Applications/eclipse/Eclipse.app/Contents/MacOS/
                                                  //| .

  val image = ImageUtilities.readMBF(new File("/Users/gerardMurphy/Documents/workspace/vision/output_4.jpg"))
                                                  //> image  : org.openimaj.image.MBFImage = org.openimaj.image.MBFImage@1d8bd0de
  																						// This bit is obviously user-specific - ideally the worksheet could just pick up
  																						// the files from the project directory; this is an oustanding Scala IDE bug at
  																						// time of writing.

  image.colourSpace                               //> res1: org.openimaj.image.colour.ColourSpace = RGB

  val blackAndWhiteImage = image.flatten()        //> blackAndWhiteImage  : org.openimaj.image.FImage = +0.618 +0.614 +0.617 +0.61
                                                  //| 3 +0.613 +0.617 +0.620 +0.624 +0.616 +0.617 +0.622 +0.624 +0.617 +0.605 +0.5
                                                  //| 97 +0.593 +0.604 ... +0.511 +0.510 +0.498 +0.506 +0.516 +0.515 +0.495 +0.495
                                                  //|  +0.518 +0.533 +0.519 +0.497 +0.485 +0.501 +0.519 
                                                  //| +0.614 +0.616 +0.613 +0.613 +0.613 +0.613 +0.620 +0.620 +0.622 +0.617 +0.618
                                                  //|  +0.618 +0.613 +0.608 +0.597 +0.600 +0.604 ... +0.511 +0.506 +0.490 +0.502 +
                                                  //| 0.512 +0.514 +0.499 +0.499 +0.510 +0.520 +0.511 +0.501 +0.489 +0.501 +0.511 
                                                  //| 
                                                  //| +0.610 +0.610 +0.609 +0.612 +0.612 +0.612 +0.616 +0.616 +0.626 +0.624 +0.617
                                                  //|  +0.610 +0.608 +0.605 +0.604 +0.605 +0.603 ... +0.511 +0.498 +0.482 +0.490 +
                                                  //| 0.508 +0.510 +0.498 +0.502 +0.502 +0.501 +0.499 +0.501 +0.497 +0.497 +0.499 
                                                  //| 
                                                  //| +0.605 +0.609 +0.612 +0.612 +0.612 +0.612 +0.612 +0.608 +0.630 +0.626 +0.613
                                                  //|  +0.604 +0.603 +0.604 +0.605 +0.612 +0.600 ... +0.508 +0.494 +0.475 +0.485 +
                                                  //| 0.505 +0.510 +0.498 +0.502 +0.493 +0.485
                                                  //| Output exceeds cutoff limit.
                                                  
	DisplayUtilities.display(image)           //> res2: javax.swing.JFrame = javax.swing.JFrame[frame0,0,23,1024x1046,layout=j
                                                  //| ava.awt.BorderLayout,title=Image: 0,maximized,defaultCloseOperation=HIDE_ON_
                                                  //| CLOSE,rootPane=javax.swing.JRootPane[,0,22,1024x1024,layout=javax.swing.JRoo
                                                  //| tPane$RootLayout,alignmentX=0.0,alignmentY=0.0,border=,flags=16777675,maximu
                                                  //| mSize=,minimumSize=,preferredSize=],rootPaneCheckingEnabled=true]
                                                  
  val backupImage = image.clone()                 //> backupImage  : org.openimaj.image.MBFImage = org.openimaj.image.MBFImage@10
                                                  //| 959ece

  val imageProcessor = HaarWaveletProcessor()     //> imageProcessor  : org.openimaj.image.processor.ImageProcessor[org.openimaj.
                                                  //| image.MBFImage] = com.sageSerpent.throwaway.vision.HaarWaveletProcessor$$an
                                                  //| on$1@2e55dd0c

  image.processInplace(imageProcessor)            //> res3: org.openimaj.image.MBFImage = org.openimaj.image.MBFImage@1d8bd0de

  DisplayUtilities.display(image)                 //> res4: javax.swing.JFrame = javax.swing.JFrame[frame1,0,23,1024x1046,layout=
                                                  //| java.awt.BorderLayout,title=Image: 1,maximized,defaultCloseOperation=HIDE_O
                                                  //| N_CLOSE,rootPane=javax.swing.JRootPane[,0,22,1024x1024,layout=javax.swing.J
                                                  //| RootPane$RootLayout,alignmentX=0.0,alignmentY=0.0,border=,flags=16777673,ma
                                                  //| ximumSize=,minimumSize=,preferredSize=],rootPaneCheckingEnabled=true]
  
	val imageInverseProcessor = InverseHaarWaveletProcessor()
                                                  //> imageInverseProcessor  : org.openimaj.image.processor.ImageProcessor[org.op
                                                  //| enimaj.image.MBFImage] = com.sageSerpent.throwaway.vision.InverseHaarWavele
                                                  //| tProcessor$$anon$1@53976f5c
	
	image.processInplace(imageInverseProcessor)
                                                  //> res5: org.openimaj.image.MBFImage = org.openimaj.image.MBFImage@1d8bd0de
	
	DisplayUtilities.display(image)           //> res6: javax.swing.JFrame = javax.swing.JFrame[frame2,0,23,1024x1046,layout=
                                                  //| java.awt.BorderLayout,title=Image: 2,maximized,defaultCloseOperation=HIDE_O
                                                  //| N_CLOSE,rootPane=javax.swing.JRootPane[,0,22,1024x1024,layout=javax.swing.J
                                                  //| RootPane$RootLayout,alignmentX=0.0,alignmentY=0.0,border=,flags=16777673,ma
                                                  //| ximumSize=,minimumSize=,preferredSize=],rootPaneCheckingEnabled=true]
                                                  
  image.subtractInplace(backupImage)              //> res7: org.openimaj.image.MBFImage = org.openimaj.image.MBFImage@1d8bd0de
  
  DisplayUtilities.display(image)                 //> res8: javax.swing.JFrame = javax.swing.JFrame[frame3,0,23,1024x1046,layout=
                                                  //| java.awt.BorderLayout,title=Image: 3,maximized,defaultCloseOperation=HIDE_O
                                                  //| N_CLOSE,rootPane=javax.swing.JRootPane[,0,22,1024x1024,layout=javax.swing.J
                                                  //| RootPane$RootLayout,alignmentX=0.0,alignmentY=0.0,border=,flags=16777673,ma
                                                  //| ximumSize=,minimumSize=,preferredSize=],rootPaneCheckingEnabled=true]/
}