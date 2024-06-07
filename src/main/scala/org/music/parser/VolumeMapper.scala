package org.music.parser

object VolumeMapper {

  val midiMax = 127
  val midiMin = 0

  val mmlMax = 15
  val mmlMin = 0

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
