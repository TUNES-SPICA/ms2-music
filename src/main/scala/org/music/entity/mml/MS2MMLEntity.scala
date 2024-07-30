package org.music.entity.mml

import java.util
import scala.collection.mutable.ArrayBuffer

/**
 * mml 音轨内容
 */
case class MS2MMLEntity(lines: ArrayBuffer[String]) {

  def add(line: String): Unit = {
    lines += line
  }

  def toMML: util.ArrayList[String] = {
    val mml = util.ArrayList[String]()

    mml.add("<?xml version=\"1.0\" encoding=\"utf-8\"?>")

    mml.add("<ms2>")

    for (i <- lines.indices) {
      mml.add("<melody>")
      if (i > 0) mml.add(s"<chord index=\"${i}\">")
      mml.add("<![CDATA[")
      mml.add(lines(i))
      mml.add("]]>")
      if (i > 0) mml.add("</chord>")
      mml.add("</melody>")
    }

    mml
  }
}
