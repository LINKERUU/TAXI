package com.driverservice.grpc;


import com.driverservice.exception.custom.DriverNotFoundException;
import com.driverservice.exception.custom.DuplicateEmailException;
import com.driverservice.exception.custom.DuplicateLicensePlateException;
import io.grpc.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GrpcExceptionInterceptor implements ServerInterceptor{

  @Override
  public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
          ServerCall<ReqT, RespT> serverCall,
          Metadata metadata,
          ServerCallHandler<ReqT, RespT> serverCallHandler) {
    ServerCall.Listener<ReqT> listener = serverCallHandler.startCall(serverCall, metadata);

    return new ForwardingServerCallListener.SimpleForwardingServerCallListener<>(listener) {
      @Override
      public void onHalfClose() {
        try {
          super.onHalfClose();
        } catch (Exception ex) {
          log.error(ex.getMessage());
          serverCall.close(mapToStatus(ex), new Metadata());
        }
      }
    };
  }

  Status mapToStatus(Throwable ex) {
    if (ex instanceof DuplicateEmailException || ex instanceof DuplicateLicensePlateException) {
      return Status.ALREADY_EXISTS.withDescription(ex.getMessage());
    }
    if(ex instanceof DriverNotFoundException)
      return Status.NOT_FOUND.withDescription(ex.getMessage());
    if(ex instanceof IllegalArgumentException)
      return Status.INVALID_ARGUMENT.withDescription(ex.getMessage());
    if(ex instanceof IllegalStateException)
      return Status.FAILED_PRECONDITION.withDescription(ex.getMessage());

    return Status.INTERNAL.withDescription(ex.getMessage());
  }

}
