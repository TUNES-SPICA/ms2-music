package org.music

import org.music.entity.track.TrackSplittingRule
import org.music.parser.MidiParser

import java.io.File
import javax.sound.midi.MidiSystem

object Main extends App {

  private val sequencer = MidiSystem.getSequencer

  sequencer.open()

  private val sequence = MidiSystem.getSequence(getClass.getClassLoader.getResourceAsStream("star.mid"))

  sequencer.setSequence(sequence)

  val lines = MidiParser.toMML(trackEntity, TrackSplittingRule(true, false))
  private val trackEntity = MidiParser.parseMidiData(sequence)

  sequencer.close()
  private val qqp = sequence.getResolution

  lines.foreach(line => {
    println("line")
    println(line)
  })


}
