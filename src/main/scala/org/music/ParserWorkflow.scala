package org.music

import org.music.entity.mml.MS2MMLEntity
import org.music.entity.track.TrackSplittingRule
import org.music.operator.PatternDrivenStringCompression
import org.music.parser.MidiParser

import java.io.{File, PrintWriter}
import java.nio.file.Path
import javax.sound.midi.MidiSystem
import scala.collection.mutable.ArrayBuffer

object ParserWorkflow {

  def convertMIDToMML(midPath: Path, mmlPath: Path, rule: TrackSplittingRule): Unit = {
    val sequencer = MidiSystem.getSequencer
    sequencer.open()

    val file = new File(midPath.toString)

    if (file.exists()) {
      if (!isMidFile(file)) throw new RuntimeException("请选择 mid 文件")
    } else {
      throw new RuntimeException("文件不存在")
    }

    val sequence = MidiSystem.getSequence(file)

    sequencer.setSequence(sequence)

    val track = MidiParser.parseMidiData(sequence)

    val lines = MidiParser.toMML(track, rule)

    sequencer.close()

    val qqp = sequence.getResolution

    val mml = MS2MMLEntity(ArrayBuffer())

    lines.foreach(line => {
      mml.add(PatternDrivenStringCompression.compression(line.toString()))
    })

    writeLinesToFile(mmlPath, mml.toMML)
  }

  private def writeLinesToFile(path: Path, lines: ArrayBuffer[String]): Unit = {
    val writer = PrintWriter(new File(path.toString + ".ms2mml"))
    try lines.foreach(line => writer.println(line))
    finally writer.close()
  }

  private def isMidFile(file: File): Boolean = {
    val fileName = file.getName
    fileName.endsWith(".mid") || fileName.endsWith(".MID")
  }

}
