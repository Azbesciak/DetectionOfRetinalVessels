package com.witkups.retinalvessels

import java.io.File

import javax.imageio.ImageIO.read
import org.bytedeco.javacpp.opencv_core.{IplImage, Mat}
import org.bytedeco.javacpp.opencv_imgproc.{CV_BGR2GRAY, cvtColor}
import org.bytedeco.javacv.{Java2DFrameConverter, OpenCVFrameConverter}
import scalafx.embed.swing.SwingFXUtils._
import scalafx.scene.image.Image

import scala.language.postfixOps

object Utils {
  implicit class Elvis[T >: Null](val t: T) {
    def ?[X >: Null](f: (T) => X): X = if (t != null) f(t) else null
  }

  implicit class FileUtil(var file: File) {
    def img: Image = file ? (f => toFXImage(read(f), null))
  }

}
