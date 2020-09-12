/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.tncrazvan.quarkus.tools.system;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Map;
/**
 *
 * @author Razvan Tanase
 */
public class ServerFile extends File{
    private static final long serialVersionUID = 4567989494529454756L;

    public ServerFile(final File parent, final File file) {
        super(parent, file.getAbsolutePath());
    }

    public ServerFile(final File file) {
        super(file.getAbsolutePath());
    }

    public ServerFile(final URI filename) {
        super(filename);
    }

    public ServerFile(final String filename) {
        super(filename);
    }

    public ServerFile(final String parent, final String filename) {
        super(parent, filename);
    }

    public ServerFile(final File parent, final String filename) {
        super(parent, filename);
    }


    public final byte[] read() throws FileNotFoundException, IOException {
        return read(0,(int) this.length());
    }
    
    public final byte[] read(int offset, int length) throws FileNotFoundException, IOException {
        byte[] result;
        try (FileInputStream fis = new FileInputStream(this)) {
            fis.getChannel().position(offset);
            result = fis.readNBytes(length);
        }
        return result;
    }

    public final void write(final String contents, final String charset) throws UnsupportedEncodingException, IOException {
        write(contents.getBytes(charset));
    }

    public final void write(final byte[] contents) throws IOException {
        try(FileOutputStream fos = new FileOutputStream(this)){
            fos.write(contents);
        }
    }

    /**
     * Get all information attributes of this file.
     * @return file information map.
     * @throws IOException
     */
    public final Map<String, Object> info() throws IOException {
        return info("*");
    }

    /**
     * Get a specific information field regarding this file.
     * @param selection
     * @return
     * @throws IOException
     */
    public final Map<String, Object> info(final String selection) throws IOException {
        return Files.readAttributes(this.toPath(), selection);
    }

    /**
     * Read all contents of this file as a String.
     * @param charset charset to use when decoding the contents.
     * @return contents of the file.
     * @throws IOException
     */
    public final String readString(final String charset) throws IOException {
        return Files.readString(this.toPath(),Charset.forName(charset));
    }
}