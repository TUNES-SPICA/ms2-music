package org.music.entity.track

/**
 * 分轨规则
 *
 * @param reduceRest   去除休止符，音符和音符之间的空白将不再由休止符填充，而是延长上一个音符的长度
 * @param changeVolume 音量变化，如果不适用的场合，全局将使用默认的统一音量
 * @param sustain      启用延音
 * @param changeBPM    节奏变化
 */
case class TrackRule(reduceRest: Boolean, changeVolume: Boolean, sustain: Boolean, changeBPM: Boolean)

/**
 * 分轨规则伴生类
 */
object TrackRule {

  def init(reduceRest: Boolean, changeVolume: Boolean, sustain: Boolean, changeBPM: Boolean): TrackRule = {
    TrackRule(reduceRest, changeVolume, sustain, changeBPM)
  }

  /**
   * 获取默认音量
   *
   * @return default volume
   */
  def getDefaultVolume: String = "V13"

  /**
   * 获取默认延音配置
   *
   * @return default sustain
   */
  def getDefaultSustain: String = "S1"

}
