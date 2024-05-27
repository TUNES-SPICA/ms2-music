package org.maplestory2;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiFileFormat;
import javax.sound.midi.Sequence;
import java.io.File;
import java.io.IOException;

public class MidiParser {

    public static void main(String[] args) {
        try {
            File midiFile = new File("C:\\temp\\CG.mid");
            Sequence sequence = MidiSystem.getSequence(midiFile);

            MidiFileFormat midiFileFormat = MidiSystem.getMidiFileFormat(midiFile);
            System.out.println("MIDI File Type: " + midiFileFormat.getType());
            System.out.println("MIDI File Subtype: " + midiFileFormat.getSubtype());
            System.out.println("Resolution: " + sequence.getResolution());
            System.out.println("Tracks: " + sequence.getTracks().length);

            for (int i = 0; i < sequence.getTracks().length; i++) {
                System.out.println("Track " + (i + 1) + ":");
                printTrack(sequence.getTracks()[i]);
            }

        } catch (InvalidMidiDataException | IOException e) {
            e.printStackTrace();
        }
    }

    private static void printTrack(Track track) {
        for (int i = 0; i < track.size(); i++) {
            MidiEvent event = track.get(i);
            MidiMessage message = event.getMessage();
            long tick = event.getTick();
            System.out.println("Tick: " + tick + ", Message: " + message);
        }
    }
}
