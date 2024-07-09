package org.music

import org.music.parser.{BeatMapper, MidiParser}

import java.io.File
import javax.sound.midi.MidiSystem

object Main extends App {

  private val sequencer = MidiSystem.getSequencer

  sequencer.open()

  private val sequence = MidiSystem.getSequence(getClass.getClassLoader.getResourceAsStream("star.mid"))

  sequencer.setSequence(sequence)

  val lines = MidiParser.toMML(trackEntity)

  sequencer.close()

  //  trackEntity.foreach(note => {
  //    println(s"Note: ${note.pianoKey} Volume: ${note.volume} Tick: ${BeatMapper.mapBeat(note.endTick - note.startTick, sequence.getResolution)}")
  //  })
  // 解析MIDI数据
  private val trackEntity = MidiParser.parseMidiData(sequence)

  lines.foreach(line => {
    println("line")
    line.foreach(note => {
      //      println(s"Note: ${note.pianoKey} Volume: ${note.volume} Tick: ${BeatMapper.mapBeat(note.endTick - note.startTick, sequence.getResolution)}")
      println(s"Note: ${note.pianoKey} Volume: ${note.volume} Tick: ${BeatMapper.mapBeat(note.endTick - note.startTick, sequence.getResolution)} Start: ${note.startTick} End:${note.endTick}")
      //      println(s"Note: ${note.pianoKey} Volume: ${note.volume} Tick: ${BeatMapper.mapBeat(note.endTick - note.startTick, sequence.getResolution)} Start: ${note.startTick / 6} End:${note.endTick / 6}")
    })
  })


}
