package org.music

import org.music.entity.mml.MS2MMLEntity
import org.music.entity.track.TrackSplittingRule
import org.music.operator.PatternDrivenStringCompression
import org.music.parser.MidiParser
import org.springframework.web.multipart.MultipartFile

import java.io.InputStream
import java.util
import javax.sound.midi.MidiSystem
import scala.collection.mutable.ArrayBuffer

object Parser {

  def convertMIDToMML(is: InputStream): util.ArrayList[String] = {
    val sequence = MidiSystem.getSequence(is)
    val track = MidiParser.parseMidiData(sequence)

    val mml = MS2MMLEntity(ArrayBuffer())

    MidiParser.toMML(track, TrackSplittingRule(true, false)).foreach(line => {
      mml.add(PatternDrivenStringCompression.compression(line.toString()))
    })

    mml.toMML
  }

}
