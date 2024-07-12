package org.music.parser

object VolumeMapper {

  private val midiMax = 127
  private val midiMin = 0

  private val mmlMax = 15
  private val mmlMin = 0

  def mapMidiPitch(volume: Int): Int = {
    val mmlVolume = volume >> 3

    if (mmlVolume > 0) {
      mmlVolume
    } else if (volume > 0) {
      1
    } else {
      0
    }

  }

}
