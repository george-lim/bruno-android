package com.cs446.group7.bruno.storage;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class FileStorage {
    public static class KEYS {
        public static final String FALLBACK_PLAYLIST = "fallback_playlist";
    }

    public static void writeSerializableToFile(Context context, String filename, Serializable obj) throws IOException {
        FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(obj);
        oos.close();
        fos.close();
    }

    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T readFileAsSerializable(Context context, String filename)
            throws IOException, ClassNotFoundException {
        FileInputStream fis = context.openFileInput(filename);
        ObjectInputStream ois = new ObjectInputStream(fis);
        T obj = (T) ois.readObject();
        ois.close();
        fis.close();
        return obj;
    }
}
