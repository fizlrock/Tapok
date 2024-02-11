package fizlrock.pet;

import com.google.common.util.concurrent.ServiceManager;

import fizlrock.pet.Service.TapokImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;

/**
 * Hello world!
 *
 */
public class App {
  public static void main(String[] args) throws Exception {

    Server server = ServerBuilder
        .forPort(5050)
        .addService(new TapokImpl()).build();
    server.start();
    System.out.println("Lottery server at localhost:9090");
    server.awaitTermination();
    System.out.println("Hello World!");
  }
}
