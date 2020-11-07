package api;
import java.io.IOException;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import services.SvelteSSRService;

@Path("/")
public class Home {

    @Inject
    SvelteSSRService ssr;

    @GET
    public String get() throws IOException {
        return ssr.page("./src/main/svelte/App.svelte");
    }
}
