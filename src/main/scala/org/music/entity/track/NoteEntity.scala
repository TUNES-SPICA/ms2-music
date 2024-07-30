package org.music.entity.track

/**
 * 解析后的 MIDI 音符实体
 *
 * @param noteType  音符类型
 * @param pianoKey  音高，表示音符的音调，如 C4
 * @param volume    音量，表示音符的响度，是一个介于0到127之间的整数
 * @param startTick tick 起始点，表示音符开始的时间点
 * @param endTick   tick 终止点，表示音符结束的时间点
 */
case class NoteEntity(noteType: NoteEnum, pianoKey: PianoKeyEntity, volume: Int, var startTick: Long, var endTick: Long)

/**
 * NoteEntity 伴生对象
 */
object NoteEntity {

  /**
   * 创建一个休止符实例
   *
   * 休止符表示没有声音的时间间隔
   * 此方法返回的 NoteEntity 实例是一个默认的休止符，起始和终止点均为 0，需要通过 copy 来修改默认的起始点和终止点
   *
   * @return 休止符
   */
  def rest(): NoteEntity = {
    NoteEntity(NoteEnum.NOTE, PianoKeyEntity("R", ""), 0, 0L, 0L)
  }

  /**
   * 创建一个 BPM
   *
   * BPM 表示音乐中每分钟的节拍数
   *
   * @return BPM
   */
  def bpm(): NoteEntity = {
    NoteEntity(NoteEnum.BPM, PianoKeyEntity("BPM", ""), 0, 0L, 0L)
  }

}