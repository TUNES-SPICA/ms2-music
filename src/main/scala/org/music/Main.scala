package org.music

import org.music.entity.track.TrackSplittingRule
import org.music.operator.PatternDrivenStringCompression
import org.music.parser.MidiParser

import java.io.File
import javax.sound.midi.MidiSystem

object Main extends App {

  private val sequencer = MidiSystem.getSequencer

  sequencer.open()

  private val sequence = MidiSystem.getSequence(new File("C:\\Users\\11393\\Desktop\\file_project\\web\\ms2-music\\src\\main\\resources\\s3.mid"))

  sequencer.setSequence(sequence)

  private val trackEntity = MidiParser.parseMidiData(sequence)
  val lines = MidiParser.toMML(trackEntity, TrackSplittingRule(true, false))

  sequencer.close()
  private val qqp = sequence.getResolution

  lines.foreach(line => {
    println("line")
    println(line)
    println(PatternDrivenStringCompression.compression(line.toString()))
  })


}
