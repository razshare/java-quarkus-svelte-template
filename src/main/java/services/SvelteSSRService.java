package services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.inject.Singleton;

import io.vertx.core.json.JsonObject;
@Singleton
public class SvelteSSRService {
    private ProcessBuilder pb = new ProcessBuilder();

    public SvelteSSRService(){
        pb.directory(new File(System.getProperty("user.dir")));
    }

    public String page(String filename) throws IOException {
        return page(filename, "", new HashMap<>());
    }

    public String page(String filename, String title) throws IOException {
        return page(filename, title, new HashMap<>());
    }
    
    public String page(String filename, String title, HashMap<String,Object> props) throws IOException {
        File wd = new File(System.getProperty("user.dir")).getParentFile();
        pb.directory(wd);
        pb.command("node","ssr.js",filename,JsonObject.mapFrom(props).toString(),title);
        Process p = pb.start();
        InputStream is = p.getInputStream();
        String result = new String(is.readAllBytes());
        is.close();
        p.destroy();
        return result;
    }
}
