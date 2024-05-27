package org.music

import javax.sound.midi.MidiSystem

object Main extends App {

  private val sequencer = MidiSystem.getSequencer

  sequencer.open()

  private val sequence = MidiSystem.getSequence(getClass.getResourceAsStream("star.mid"))

  sequencer.setSequence(sequence)

  // 解析MIDI数据
  MidiParser.parseMidiData(sequence)

  sequencer.close()

}
