package com.witkups.retinalvessels

import java.io.File

import com.witkups.retinalvessels.Utils._
import javafx.stage.FileChooser.ExtensionFilter
import scalafx.Includes._
import scalafx.scene.control.{Button, Label}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.Pane
import scalafx.stage.FileChooser
import scalafxml.core.macros.sfxml

import scala.language.postfixOps

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
  var originalImgFile: File = _
  var mapImageFile: File = _
  var fovImageFile: File = _
  var originalImg: Image = _
  var mapImg: Image = _
  var fovImg: Image = _

  List(originalImgView, mapImgView, resultProcImgView, resultMLImgView, fovImgView) foreach (_.fitWidth bind (root.width / 3.07))

  mainFileBtn setOnMouseClicked { _ =>
    originalImgFile = getFile("Original image")
    mainFileLabel.text = originalImgFile ? (_.getName)
    originalImg = originalImgFile.img
    originalImgView.image = originalImg
  }

  mapFileBtn setOnMouseClicked { _ =>
    mapImageFile = getFile("Map file")
    mapFileLabel.text = mapImageFile ? (_.getName)
    mapImg = mapImageFile.img
    mapImgView.image = mapImg
  }

  fovFileBtn setOnMouseClicked { _ =>
    fovImageFile = getFile("Fov file")
    fovFileLabel.text = fovImageFile ? (_.getName)
    fovImg = fovImageFile.img
    fovImgView.image = fovImg
  }

  startBtn setOnMouseClicked { _ =>
    val processor = new ImageProcessor(originalImg, mapImg, fovImg)
    processor
  }

  private def getFile(fileType: String): File =
    new FileChooser {
      extensionFilters add new ExtensionFilter("Image files", "*.jpg", "*.jpeg", "*.png", "*.tif")
      title = s"Select $fileType"
      initialDirectory = new File(".")
    }.showOpenDialog(root scene() getWindow)

}