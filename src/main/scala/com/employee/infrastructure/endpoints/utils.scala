package com.employee.infrastructure.endpoints

import spray.json._

import scala.io.Source

object Utils {
  def readJsonExample(fileName: String): JsValue = {
    val example = Source.fromResource(fileName).mkString
    example.parseJson
  }
}