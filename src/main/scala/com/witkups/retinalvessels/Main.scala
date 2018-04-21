package com.witkups.retinalvessels

import java.io.IOException

import org.bytedeco.javacpp.opencv_core._
import org.bytedeco.javacpp.opencv_imgcodecs._
import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.image.ImageView
import scalafx.scene.paint.Color._
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scala.reflect.io.File
import com.witkups.retinalvessels.Utils._
import scalafx.application.JFXApp.PrimaryStage
import scalafxml.core.{FXMLView, NoDependencyResolver}


object Main extends JFXApp {

  val resource = getClass.getResource("Form.fxml")
  if (resource == null) {
    throw new IOException("Cannot load resource: AdoptionForm.fxml")
  }

  val rootPage = FXMLView(resource, NoDependencyResolver)

  stage = new JFXApp.PrimaryStage {
    title = "Retinal Vessels visualizer"
    scene = new Scene(rootPage)
  }
  //  private val file = File("./all/images/01_dr.JPG")
  //  println(file.exists)
  //  private val image: IplImage = cvLoadImage("./all/images/01_dr.JPG")
  //
  //  private val mat = image.asCvMat()
  //  stage = new JFXApp.PrimaryStage {
  //    title.value = "Hello Stage"
  //    val oldWidth = mat.width()
  //    val newWidth = math.min(oldWidth, 600)
  //    height = newWidth / oldWidth * mat.height()
  //    width = newWidth
  //    val view = new ImageView(cvarrToMat(mat).mat2Image()) {
  //      fitWidth = newWidth
  //      fitHeight = height()
  //    }
  //    scene = new Scene {
  //      fill = LightGreen
  //      content = view
  //    }
  //  }

}