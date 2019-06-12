package com.ve572.p1.preprocess;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.ve572.p1.preprocess.avro.MillionSong;
import com.ve572.p1.preprocess.CheckSum;
import org.apache.avro.file.CodecFactory;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumWriter;

public class CompactSmallFiles {


    public static void SerializeDirectory(String mainDirectory, String subDirectory, String avroName) {

        try {
            DatumWriter<MillionSong> datumWriter = new SpecificDatumWriter<>(MillionSong.class);
            DataFileWriter<MillionSong> dataFileWriter = new DataFileWriter<>(datumWriter);
            dataFileWriter.setCodec(CodecFactory.snappyCodec());
            dataFileWriter.create(new MillionSong().getSchema(), new File(avroName));

            Path directory = Paths.get(mainDirectory, subDirectory);
            Path prefixPath = Paths.get(subDirectory);

            Files.walk(directory).filter(Files::isRegularFile).forEach(filePath -> {
                try {
                    File fileEntry = filePath.toFile();
                    MillionSong millionSong = new MillionSong();
                    // set filename
                    String filename = prefixPath.resolve(directory.relativize(filePath)).toString();
                    millionSong.setFilename(filename);
                    // read and set filecontent
                    InputStream inputStream = new FileInputStream(fileEntry);
                    byte[] bytes = inputStream.readAllBytes();
                    ByteBuffer buffer = ByteBuffer.wrap(bytes);
                    millionSong.setFilecontent(buffer);
                    // set sha-1
                    String checksum = CheckSum.getSHA1(bytes);
                    millionSong.setChecksum(checksum);
                    System.out.println(filename + ' ' + checksum);
                    dataFileWriter.append(millionSong);
                    // close
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            dataFileWriter.close();

        } catch (NullPointerException | IOException  e) {
            e.printStackTrace();
        }


    }

    public static void main(String[] args) throws Exception {
        SerializeDirectory("data2", "/A/A", "A.A.avro");
    }
}
