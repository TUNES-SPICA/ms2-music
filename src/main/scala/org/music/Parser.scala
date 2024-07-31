package org.music

import org.music.entity.mml.MS2MMLEntity
import org.music.entity.track.TrackRule
import org.music.operator.PatternDrivenStringCompression
import org.music.parser.MidiParser

import java.io.InputStream
import java.util
import javax.sound.midi.MidiSystem
import scala.collection.mutable.ArrayBuffer

object Parser {

  def convertMIDToMML(is: InputStream, rule: TrackRule): util.ArrayList[String] = {
    val sequence = MidiSystem.getSequence(is)
    val track = MidiParser.parseMidiData(sequence)

    val mml = MS2MMLEntity(ArrayBuffer())

    MidiParser.toMML(track, rule).foreach(line => {
      mml.add(PatternDrivenStringCompression.compression(line.toString()))
    })

    mml.toMML(rule.sustain)
  }

}
