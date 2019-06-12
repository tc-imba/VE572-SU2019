package com.ve572.p1.preprocess;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.ve572.p1.preprocess.avro.MillionSong;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.io.DatumReader;
import org.apache.avro.specific.SpecificDatumReader;

public class ExtractSmallFiles {

    public static void DeSerializeDirectory(String mainDirectory, String avroName) {
        try {

//            Path mainPath = Paths.get(mainDirectory);
//            mainPath.toFile().mkdirs();
            File avroFile = new File(avroName);

            DatumReader<MillionSong> datumReader = new SpecificDatumReader<>(MillionSong.class);
            DataFileReader<MillionSong> dataFileReader = new DataFileReader<>(avroFile, datumReader);

            MillionSong millionSong = null;
            while (dataFileReader.hasNext()) {
                millionSong = dataFileReader.next(millionSong);
                Path millionSongPath = Paths.get(mainDirectory, millionSong.getFilename().toString());
                millionSongPath.getParent().toFile().mkdirs();
                OutputStream outputStream = new FileOutputStream(millionSongPath.toFile());
                byte[] bytes = new byte[millionSong.getFilecontent().remaining()];
                millionSong.getFilecontent().get(bytes);
                outputStream.write(bytes);
                String checkSum = CheckSum.getSHA1(bytes);
                outputStream.close();
                System.out.println(millionSongPath.toString() + " " + checkSum + " " + millionSong.getChecksum());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws Exception {
        DeSerializeDirectory("data3", "A.A.avro");
    }


}
