package com.passengerservice.grpc;

import io.grpc.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GrpcExceptionInterceptor implements ServerInterceptor {
  @Override
  public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
          ServerCall<ReqT, RespT> serverCall,
          Metadata metadata,
          ServerCallHandler<ReqT, RespT> serverCallHandler) {

    ServerCall.Listener<ReqT> listener = serverCallHandler.startCall(serverCall, metadata);

    return new ForwardingServerCallListener
            .SimpleForwardingServerCallListener<>(listener){
      @Override
      public void onHalfClose() {
        try {
          super.onHalfClose();
        } catch (Exception ex) {
          log.error(ex.getMessage());
          serverCall.close(Status.NOT_FOUND.withDescription(ex.getMessage()), new Metadata());
        }
      }
    };
  }
}
