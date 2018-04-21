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

  object MatUtils {
    val java2DFrameConverter = new Java2DFrameConverter()
    val converter = new OpenCVFrameConverter.ToIplImage
  }

  implicit class MatUtils(val mat: Mat) {
    def toGray(): Mat = {
      val res = new Mat()
      cvtColor(mat, res, CV_BGR2GRAY, 1)
      res
    }

    def mat2Image() = {
      val frame = MatUtils.converter.convert(mat)
      val image = MatUtils.java2DFrameConverter.convert(frame)
      toFXImage(image, null)
    }
  }

  implicit class IplImageUtils(val img: IplImage) {
    def scaleToHeight(mat: Mat, height: Double): Unit = {

    }
  }

  implicit class Elvis[T >: Null](val t: T) {
    def ?[X >: Null](f: (T) => X): X = if (t != null) f(t) else null
  }

  implicit class FileUtil(var file: File) {
    def img: Image = file ? (f => toFXImage(read(f), null))
  }

}
