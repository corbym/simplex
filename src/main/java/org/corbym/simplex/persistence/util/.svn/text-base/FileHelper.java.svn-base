package org.corbym.simplex.persistence.util;

import org.corbym.simplex.persistence.SimplexPersistenceException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileHelper {
    public static void closeFileWriter(FileWriter out) throws SimplexPersistenceException {
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                throw new SimplexPersistenceException("Simplex: could not close output stream", e);
            }
        }
    }

    public static void closeFileReader(FileReader ins) throws SimplexPersistenceException {
        if (ins != null) {
            try {
                ins.close();
            } catch (IOException e) {
                throw new SimplexPersistenceException("cannot close input stream", e);
            }
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        return dir.delete();
    }

}
