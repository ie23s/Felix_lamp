package ua.ie23s.app.felix;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.List;
import java.util.zip.CRC32;

public class Key {

    private String key;

    private String randomKey() {

        ///this is user supplied string
        String a = "ABCDEFGHI";
        int mySaltSizeInBytes = 32;
        //SecureRandom class provides strong random numbers
        SecureRandom random = new SecureRandom();

        //salt mitigates dictionary/rainbow attacks
        byte[] salt = new byte[mySaltSizeInBytes];

        //random fill salt buffer with random bytes
        random.nextBytes(salt);

        //concatenates string a  and salt
        //into one big bytebuffer ready to be digested
        //this is just one way to do it
        //there might be better ways

        ByteBuffer bbuffer = ByteBuffer.allocate(mySaltSizeInBytes+a.length());
        bbuffer.put(salt);
        bbuffer.put(a.getBytes());

        //your crc class
        CRC32 crc = new CRC32();
        crc.update(bbuffer.array());
        return Long.toHexString(crc.getValue());
    }

    private String getUserFolder() {
        String fileFolder = System.getenv("APPDATA") + "\\" + ".felix_lamp\\";

        String os = System.getProperty("os.name").toUpperCase();
        if (os.contains("MAC")) {
            fileFolder = System.getProperty("user.home") + "/Library/Application " + "Support/"
                    + ".felix_lamp/";
        }
        if (os.contains("NUX")) {
            fileFolder = System.getProperty("user.dir") + ".felix_lamp/";
        }

        System.out.println("Searching for resource folder");
        File directory = new File(fileFolder);

        if (!directory.exists()) {
            directory.mkdir();
        }
        return fileFolder;
    }

    private String getKey() {
        if(key != null)
            return key ;
        Path file = Paths.get(getUserFolder() + ".key");
        try {
            key = Files.readString(file);
        } catch (IOException e) {
            setKey(randomKey());
        }
        return key;
    }

    private void setKey(String key) {
        this.key = key;
        Path file = Paths.get(getUserFolder() + ".key");
        try {
            Files.write(file, List.of(key), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
