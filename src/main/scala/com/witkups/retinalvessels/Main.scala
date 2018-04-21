package com.witkups.retinalvessels

import java.io.IOException

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafxml.core.{FXMLView, NoDependencyResolver}


object Main extends JFXApp {

  private val form = getClass.getResource("form.fxml")
  if (form == null) {
    throw new IOException("Cannot load resource: form.fxml")
  }

  val rootPage = FXMLView(form, NoDependencyResolver)

  stage = new JFXApp.PrimaryStage {
    title = "Retinal Vessels visualizer"
    scene = new Scene(rootPage)
  }
}