package org.music.entity

import lombok.Data

@Data
class Note(var pianoKey: Int, var volume: Int, var octave: Int, val tick: Int)

