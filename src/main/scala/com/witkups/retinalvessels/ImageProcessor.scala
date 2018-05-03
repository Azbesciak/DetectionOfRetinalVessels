package com.witkups.retinalvessels

import org.bytedeco.javacpp.opencv_core.{IplImage, Mat}
import org.bytedeco.javacpp.opencv_imgproc.{CV_BGR2GRAY, cvtColor}
import org.bytedeco.javacv.{Java2DFrameConverter, OpenCVFrameConverter}
import scalafx.embed.swing.SwingFXUtils._
import scalafx.scene.image.{Image, WritableImage}
import org.bytedeco.javacpp.opencv_core._
import org.bytedeco.javacpp.opencv_imgcodecs._
import org.bytedeco.javacpp.opencv_core._
import org.bytedeco.javacpp.opencv_imgproc._
import OpenCVUtils._
import org.bytedeco.javacpp.opencv_imgproc

import scala.language.postfixOps

object ImageProcessor {

  val java2DFrameConverter = new Java2DFrameConverter()
  val converter = new OpenCVFrameConverter.ToIplImage

  implicit class MatUtils(val mat: Mat) {
    def toGray(): Mat = {
      val res = new Mat()
      cvtColor(mat, res, CV_BGR2GRAY)
      res
    }

    val th = 30
    def canny(th1: Int = 0, th2: Int = 256, apertureSize: Int = 5): Mat = {
      blur(mat, mat, (10,10))
      negate()
      equalizeHist(mat, mat)
      Canny(mat, mat, th1, th2, apertureSize, true /*L2 gradient*/)
      negate()
      contours()
//      mat
    }

    def negate(): Unit = {
      val filled = new Mat(mat.rows(), mat.cols(), mat.`type`(), Scalar.all(255))
      subtract(filled, mat, mat)
    }

    def contours(): Mat = {
      val contours = new MatVector()
      findContours(mat, contours, CV_RETR_TREE, CHAIN_APPROX_SIMPLE)

      val result = new Mat(mat.size(), CV_8UC3, 0)
      drawContours(result, contours,
        -1, // draw all contours
        new Scalar(0, 0, 255, 0))
      result
    }

    def splitRGB(): (Mat, Mat, Mat) = {
      val res = new MatVector()
      split(mat, res)
      (res.get(0), res.get(1), res.get(2))
    }

    def morp(src: Mat, op: Int, strEl: Mat): Mat = {
      val res = new Mat()
      morphologyEx(src, res, op, strEl)
      res
    }

    def toImage(): WritableImage = {
      val frame = converter.convert(mat)
      val image = java2DFrameConverter.convert(frame)
      toFXImage(image, null)
    }
  }

  implicit def toSize(p: (Int, Int)): Size = new Size(p._1, p._2)

  implicit def toPoint(p: (Int, Int)): Point = new Point(p._1, p._2)

  implicit def toScalar(i: Int): Scalar = new Scalar(i)

  implicit class IplImageUtils(val img: IplImage) {
    def scaleToHeight(mat: Mat, height: Double): Unit = {

    }
  }

  implicit class ImgUtils(val img: Image) {
    def toMat(): Mat = {
      val buffImg = fromFXImage(img, null)
      val frame = java2DFrameConverter.convert(buffImg)
      converter.convertToMat(frame)
    }
  }

}

class ImageProcessor(val original: Image, val map: Image, val fov: Image) {

  import ImageProcessor._

  def process():Image = {
    val grayMat = original toMat() splitRGB() _3

    grayMat canny() toImage()
  }
}
