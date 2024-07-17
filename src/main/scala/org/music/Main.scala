package org.music

import org.music.entity.mml.MS2MMLEntity
import org.music.entity.track.TrackSplittingRule
import org.music.operator.PatternDrivenStringCompression
import org.music.parser.MidiParser

import javax.sound.midi.MidiSystem
import scala.collection.mutable.ArrayBuffer

/**
 * 主程序
 */
object Main extends App {

  private val sequencer = MidiSystem.getSequencer

  sequencer.open()

  val mml = MS2MMLEntity(ArrayBuffer())

  sequencer.setSequence(sequence)

  private val trackEntity = MidiParser.parseMidiData(sequence)
  val lines = MidiParser.toMML(trackEntity, TrackSplittingRule(true, false))

  sequencer.close()

  private val qqp = sequence.getResolution
  // 使用 GUI 选择文件
  private val sequence = MidiSystem.getSequence(getClass.getResource("/s3.mid"))
  lines.foreach(line => {
    println("line")
    println(line)
    mml.add(PatternDrivenStringCompression.compression(line.toString()))
  })

  mml.toMML.foreach(
    println(_)
  )


}
