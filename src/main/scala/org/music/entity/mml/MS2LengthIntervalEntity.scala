package org.music.entity.mml

import scala.collection.mutable.ArrayBuffer

/**
 * mml 音轨 L 段落区间
 *
 * @param line 音轨内容，数组内每一个数组代表一条音轨
 */
case class MS2LengthIntervalEntity(line: ArrayBuffer[ArrayBuffer[MS2NoteEntity]])