import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("hello-world")
public class Starter {
    @GET
    public String run(){
        return "hello world";
    }
}
