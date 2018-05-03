package com.witkups.retinalvessels


import java.io.File

import com.witkups.retinalvessels.Utils._
import javafx.stage.FileChooser.ExtensionFilter
import scalafx.Includes._
import scalafx.scene.control.{Button, Label}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.Pane
import scalafx.stage.FileChooser

import scala.language.postfixOps


case class ImageProxy(
                       btn: Button,
                       fileType: String,
                       var labelInfo: Label,
                       imageView: ImageView,
                       var root: Pane,
                       var file: File = null,
                       var image: Image = null,
                     ) {
  file ? (f => {
    image = f.img
    imageView.image = image
  })
  btn setOnMouseClicked { _ =>
    file = getImage(fileType)
    labelInfo.text = file ? (_.getName)
    image = file.img
    imageView.image = image
  }

  private def getImage(fileType: String): File =
    new FileChooser {
      extensionFilters add new ExtensionFilter("Image files", "*.jpg", "*.jpeg", "*.png", "*.tif")
      title = s"Select $fileType"
      initialDirectory = new File(".")
    }.showOpenDialog(root scene() getWindow)
}
