package org.music.entity

import lombok.Data

@Data
class NoteEntity(var messageType: Int, var pianoKey: String, var volume: Int, var tick: List[Long])

