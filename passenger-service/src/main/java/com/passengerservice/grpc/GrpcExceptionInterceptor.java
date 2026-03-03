package com.passengerservice.grpc;

import com.passengerservice.exception.custom.DuplicateEmailException;
import com.passengerservice.exception.custom.PassengerNotFoundException;
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
        try{
          super.onHalfClose();
        }
        catch (Exception e) {
          log.error("gRPC exception intercepted", e);
          serverCall.close(mapToStatus(e), new Metadata());
        }

      }
    };
  }

  private Status mapToStatus(Exception e) {
    if(e instanceof DuplicateEmailException){
      return Status.ALREADY_EXISTS.withDescription(e.getMessage());
    }
    if(e instanceof PassengerNotFoundException){
      return Status.NOT_FOUND.withDescription(e.getMessage());
    }
    if(e instanceof IllegalArgumentException){
      return Status.INVALID_ARGUMENT.withDescription(e.getMessage());
    }
    if (e instanceof IllegalStateException){
      return Status.FAILED_PRECONDITION.withDescription(e.getMessage());
    }
    return Status.INTERNAL.withDescription("Internal server error");
  }
}