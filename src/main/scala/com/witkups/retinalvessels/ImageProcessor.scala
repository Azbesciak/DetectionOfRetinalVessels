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

    implicit class NumUtils(val num: Double) {
      def between(a: Double, b: Double): Boolean = num <= b && num >= a
    }

    val th = 30

    def canny(th1: Int = 0, th2: Int = 256, apertureSize: Int = 5): Mat = {
      blur(mat, mat, (10, 10))
      negate()
      equalizeHist(mat, mat)
      Canny(mat, mat, th1, th2, apertureSize, true /*L2 gradient*/)
      negate()
      contours()
    }

    def filledMat(value: Int = 255)(implicit typ: Int = mat.`type`()): Mat = new Mat(mat.rows(), mat.cols(), typ, Scalar.all(value))


    def negate(): Unit = {
      val filled = filledMat()
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

    implicit def toVector(): MatVector = new MatVector(mat)

    def detect(): Mat = {
      val (b, r, g) = splitRGB()
      val clahe = createCLAHE(2.0, (8, 8))
      clahe.apply(g, g)
      val contrast_enhanced_green_fundus = g.clone()
      val transformed = new Mat()
      morphologyEx(g, transformed, MORPH_OPEN, getStructuringElement(MORPH_ELLIPSE, (5, 5)))
      morphologyEx(transformed, transformed, MORPH_CLOSE, getStructuringElement(MORPH_ELLIPSE, (5, 5)))
      morphologyEx(transformed, transformed, MORPH_OPEN, getStructuringElement(MORPH_ELLIPSE, (11, 11)))
      morphologyEx(transformed, transformed, MORPH_CLOSE, getStructuringElement(MORPH_ELLIPSE, (11, 11)))
      morphologyEx(transformed, transformed, MORPH_OPEN, getStructuringElement(MORPH_ELLIPSE, (23, 23)))
      morphologyEx(transformed, transformed, MORPH_CLOSE, getStructuringElement(MORPH_ELLIPSE, (23, 23)))
      subtract(transformed, contrast_enhanced_green_fundus, transformed)
      clahe.apply(transformed, transformed)
      val threshed = new Mat()
      threshold(transformed, threshed, 15, 255, THRESH_BINARY)
      equalizeHist(transformed, transformed)
      medianBlur(transformed, transformed, 11)
      morphologyEx(transformed, transformed, MORPH_OPEN, getStructuringElement(MORPH_ELLIPSE, (11, 11)))
      morphologyEx(transformed, transformed, MORPH_CLOSE, getStructuringElement(MORPH_ELLIPSE, (11, 11)))
      val cons = new MatVector()
      findContours(threshed.clone(), cons, RETR_LIST, CHAIN_APPROX_SIMPLE)
      val mask = filledMat()(0)
      var counter = 0
        (0L to cons.size()-1).toArray
          .map(i => cons.get(i))
          .withFilter(c => contourArea(c).between(20, 200))
          .map(_.toVector())
          .foreach { c =>
            drawContours(mask, c, -1, 0)
          }

      bitwise_and(transformed, transformed, transformed, mask)
      threshold(transformed, transformed, 15, 255, THRESH_BINARY_INV)
      erode(transformed, transformed, getStructuringElement(MORPH_ELLIPSE, (3, 3)))
      bitwise_not(transformed, transformed)
      findContours(transformed.clone(), cons, RETR_LIST, CHAIN_APPROX_SIMPLE)
      val xmask = filledMat()(0)
      (0L until cons.size()).toParArray
        .map(i => cons.get(i))
        .foreach(cnt => {
          val len = arcLength(cnt, true)
          val aprox = new Mat()
          approxPolyDP(cnt, aprox, 0.04 * len, false)
          if (aprox.total() > 4 && contourArea(cnt).between(100, 3000)) {
            drawContours(xmask, cnt.toVector(), -1, 0)
          }
        })
      bitwise_and(transformed, transformed, xmask)
      medianBlur(transformed, transformed, 23)
      transformed
    }
  }


  implicit def toSize(p: (Int, Int)): Size = new Size(p._1, p._2)

  implicit def toPoint(p: (Int, Int)): Point = new Point(p._1, p._2)

  implicit def toScalar(i: Int): Scalar = new Scalar(i)

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

  def process(): Image = {
    original toMat() detect() toImage()
  }
}
