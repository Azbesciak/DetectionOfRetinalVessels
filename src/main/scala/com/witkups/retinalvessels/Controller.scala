package com.witkups.retinalvessels

import java.io.File

import scalafx.application.Platform
import scalafx.scene.control.{Button, Label}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.Pane
import scalafxml.core.macros.sfxml

import scala.concurrent.Future
import scala.language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global

@sfxml
class Controller(
                  val originalImgView: ImageView,
                  val mapImgView: ImageView,
                  val resultProcImgView: ImageView,
                  val resultMLImgView: ImageView,
                  val fovImgView: ImageView,
                  val mainFileBtn: Button,
                  val mapFileBtn: Button,
                  val fovFileBtn: Button,
                  val startBtn: Button,
                  val root: Pane,
                  val mainFileLabel: Label,
                  val mapFileLabel: Label,
                  val fovFileLabel: Label,
                ) {
  List(originalImgView, mapImgView, resultProcImgView, resultMLImgView, fovImgView).
    foreach(_.fitWidth bind (root.width / 3.07))
  val originalProxy = ImageProxy(mainFileBtn, "Original image", mainFileLabel, originalImgView, root, new File("./all/images/01_dr.JPG"))
  val mapProxy = ImageProxy(mapFileBtn, "Map image", mapFileLabel, mapImgView, root)
  val fovProxy = ImageProxy(fovFileBtn, "FOV image", fovFileLabel, fovImgView, root)

  startBtn setOnMouseClicked { _ =>
    val processor = new ImageProcessor(originalProxy, mapProxy, fovProxy)
    Future {
      resultMLImgView.image = processor process()
    }
  }

  implicit def getImage(imageProxy: ImageProxy): Image = imageProxy.image
}