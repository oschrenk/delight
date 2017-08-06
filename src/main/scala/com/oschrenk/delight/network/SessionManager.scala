package com.oschrenk.delight.network

import better.files.File

import scala.collection.JavaConverters._

class SessionManager(network: Network) {

  private val SessionKey = "PHPSESSID"

  private def loadCookies(sessionPath: File): Option[Map[String, String]] = {
    if (sessionPath.isRegularFile) {
      val cookies = sessionPath.lines.map { line =>
        line.split("=") match {
          case Array(k,v, _*) => Map(k.trim -> v.trim)
        }
      }.reduce(_ ++ _)
      if (cookies.contains(SessionKey)) {
        Some(cookies)
      } else {
        None
      }
    } else {
      None
    }
  }

  private def storeCookies(sessionPath: File, cookies: Map[String, String]): Unit = {
    val lines = cookies.map{case (k,v) => s"$k=$v"}.toSeq
    sessionPath
      .createIfNotExists(asDirectory = false, createParents = true)
      .overwrite("")
      .appendLines(lines:_*)
  }

  def authorize(username: String, password: String, sessionPath: File): () => Map[String, String] = () => {
    loadCookies(sessionPath).getOrElse{
      val login = network.login(username, password)
      val cookies = login.cookies.asScala.toMap
      storeCookies(sessionPath, cookies)
      cookies
    }
  }
}

