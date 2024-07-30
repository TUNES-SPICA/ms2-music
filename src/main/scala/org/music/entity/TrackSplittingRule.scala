package org.music.web

/**
 * 分轨规则
 *
 * @param reduceRest   去除休止符，音符和音符之间的空白将不再由休止符填充，而是延长上一个音符的长度
 * @param changeVolume 音量变化，如果不适用的场合，全局将使用默认的统一音量
 */
case class TrackSplittingRule(reduceRest: Boolean, changeVolume: Boolean)

/**
 * 分轨规则伴生类
 */
object TrackSplittingRule {

  /**
   * 获取默认音量
   *
   * @return default volume
   */
  def getDefaultVolume: String = "V13"

}
