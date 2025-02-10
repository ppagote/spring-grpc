package com.pranav.springgrpc;

import com.pranav.springgrpc.proto.HelloReply;
import com.pranav.springgrpc.proto.HelloRequest;
import com.pranav.springgrpc.proto.HelloWorldGrpc;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GrpcServerService extends HelloWorldGrpc.HelloWorldImplBase {
    /**
     * <pre>
     * Sends a greeting
     * </pre>
     *
     * @param request InputRequest
     * @param responseObserver responseObserver
     */
    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        log.info("Hello {}", request.getName());
        if (request.getName().startsWith("error")) {
            throw new IllegalArgumentException("Bad name: " + request.getName());
        }
        if (request.getName().startsWith("internal")) {
            throw new RuntimeException();
        }
        HelloReply reply = HelloReply.newBuilder()
                .setMessage("Hello " + request.getName())
                .build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    /**
     * @param request InputRequest
     * @param responseObserver responseObserver
     */
    @Override
    public void streamHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        log.info("Hello {}", request.getName());
        int count = 0;
        while (count < 10) {
            HelloReply reply = HelloReply.newBuilder()
                    .setMessage("Hello(" + count + ") " + request.getName())
                    .build();
            responseObserver.onNext(reply);
            count++;
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                responseObserver.onError(e);
                return;
            }
        }
        responseObserver.onCompleted();
    }
}
