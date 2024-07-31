package org.music.entity.mml

import org.music.entity.track.TrackRule

import java.util
import scala.collection.mutable.ArrayBuffer

/**
 * mml 音轨内容
 */
case class MS2MMLEntity(lines: ArrayBuffer[String]) {

  def add(line: String): Unit = {
    lines += line
  }

  def toMML(sustain: Boolean): util.ArrayList[String] = {
    val mml = util.ArrayList[String]()

    mml.add("<?xml version=\"1.0\" encoding=\"utf-8\"?>")

    mml.add("<ms2>")

    for (i <- lines.indices) {

      if (i > 0) mml.add(s"<chord index=\"${i}\">")
      else mml.add("<melody>")

      mml.add("<![CDATA[")

      if (i == 0 && sustain) mml.add(TrackRule.getDefaultSustain + lines(i))
      else mml.add(lines(i))

      mml.add("]]>")

      if (i > 0) mml.add("</chord>")
      else mml.add("</melody>")
    }

    mml.add("</ms2>")

    mml
  }
}
