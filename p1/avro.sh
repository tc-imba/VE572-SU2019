#!/bin/bash
for m in {A..Z}; do
    for n in {A..Z}; do
        echo $m/$n
        java -cp target/ve572p1-1.0-SNAPSHOT-jar-with-dependencies.jar com.ve572.p1.preprocess.MillionSong --data /home/million_song_dataset --output /home/avro --subdirectory /$m/$n
    done
done
