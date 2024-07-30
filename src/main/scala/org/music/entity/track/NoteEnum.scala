package org.music.entity.track

/**
 * mml 音符类型枚举
 */
enum NoteEnum {

  /**
   * 音阶
   */
  case BPM

  /**
   * 纯音符
   */
  case NOTE

  /**
   * 音量
   */
  case VOLUME

  /**
   * 动作速率
   */
  case ACTION

  /**
   * 音阶
   */
  case OCTAVE

  /**
   * 音阶2
   */
  case OCTAVE2

  /**
   * 连接符
   */
  case LINK

  /**
   * 空白（跳过此音符）
   */
  case BLANK

  /**
   * 音符时值
   */
  case NOTE_TIME

  /**
   * 音符时值影响范围结束段
   */
  case NOTE_TIME_END

  /**
   * 其他
   */
  case OTHER

}