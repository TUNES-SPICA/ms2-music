package org.music

import org.music.entity.TrackEntity
import org.music.parser.MidiParser

import javax.sound.midi.MidiSystem

object Main extends App {

  private val sequencer = MidiSystem.getSequencer

  sequencer.open()

  private val sequence = MidiSystem.getSequence(getClass.getClassLoader.getResourceAsStream("star.mid"))

  sequencer.setSequence(sequence)

  // 解析MIDI数据
  private val trackEntity: TrackEntity = MidiParser.parseMidiData(sequence)

  trackEntity.notes.foreach((channel, notes) => {
    notes.foreach(note => {
      println(s" Channel: ${channel} MessageType: ${note.messageType} Note: ${note.pianoKey} Volume: ${note.volume} Tick: ${note.tick}")
    })
  })

  sequencer.close()

}
