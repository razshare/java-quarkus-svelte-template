/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.tncrazvan.quarkus.tools.zip;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import io.github.tncrazvan.quarkus.tools.system.ServerFile;

import static io.github.tncrazvan.quarkus.tools.SharedObject.LOGGER;


/**
 *
 * @author Razvan Tanase
 */
 public class ZipArchive{
    private final String filename;
    private ServerFile file;
    private final ArrayList<ZipEntryData> entries;
    
    public ZipArchive(final String filename) throws FileNotFoundException {
        this.filename = filename;
        entries = new ArrayList<>();
    }

    private class ZipEntryData {
        public ZipEntry entry;
        public byte[] data;

        public ZipEntryData(final ZipEntry entry, final byte[] data) {
            this.entry = entry;
            this.data = data;
        }

    }

    public final void addEntry(final String filename, final String contents, final String charset) throws IOException {
        addEntry(filename, contents.getBytes(charset));
    }

    public final void addEntry(final String filename, final ServerFile file) throws IOException {
        addEntry(filename, file.read());
    }

    public final void addEntry(final String filename, final byte[] data) throws IOException {
        final ZipEntry e = new ZipEntry(filename);
        entries.add(new ZipEntryData(e, data));
    }

    public final void make() throws IOException {
        file = new ServerFile(filename);
        try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(file))) {
            entries.forEach((e) -> {
                try {
                    out.putNextEntry(e.entry);
                    out.write(e.data, 0, e.data.length);
                    out.closeEntry();
                } catch (final IOException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
            });
        }
    }

    public final ServerFile getFile(){
        return file;
    }
}