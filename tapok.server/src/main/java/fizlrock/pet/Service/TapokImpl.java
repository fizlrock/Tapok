package fizlrock.pet.Service;

import java.util.UUID;

import com.fizlrock.messagebrocker.GetMessageRequest;
import com.fizlrock.messagebrocker.Message;
import com.fizlrock.messagebrocker.RegisterNewUserResponse;
import com.fizlrock.messagebrocker.SendMessageResponse;
import com.fizlrock.messagebrocker.BrockerGrpc.BrockerImplBase;
import com.fizlrock.messagebrocker.SendMessageResponse.MessageState;
import com.fizlrock.messagebrocker.SendMessageResponse.RejectReason;
import com.google.protobuf.Empty;

import fizlrock.pet.Database.InMemoryDatabase;
import fizlrock.pet.Database.TapokDatabase;
import fizlrock.pet.Database.TapokDatabase.MessageDTO;
import io.grpc.stub.StreamObserver;

public class TapokImpl extends BrockerImplBase {

  TapokDatabase db = new InMemoryDatabase();

  @Override
  public void registerNewUser(Empty request, StreamObserver<RegisterNewUserResponse> observer) {
    try {
      var response = RegisterNewUserResponse
          .newBuilder()
          .setUserId(db.registerUser().toString())
          .build();
      observer.onNext(response);
    } catch (Exception e) {
      observer.onError(e);
    } finally {
      observer.onCompleted();
    }
  }

  @Override
  public StreamObserver<Message> sendMessages(StreamObserver<SendMessageResponse> observer) {
    return new StreamObserver<Message>() {

      @Override
      public void onCompleted() {
        observer.onCompleted();
      }

      @Override
      public void onError(Throwable arg0) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onError'");
      }

      @Override
      public void onNext(Message m) {
        var recipient_id = UUID.fromString(m.getRecipientId());
        var sender_id = UUID.fromString(m.getSenderId());

        var response = SendMessageResponse.newBuilder();

        response.setState(MessageState.REJECTED);
        if (!db.hasUser(recipient_id)) {
          response.setRejectReason(RejectReason.UNKNOWN_RECIPIENT_ID);
        } else if (!db.hasUser(sender_id)) {
          response.setRejectReason(RejectReason.UNKNOWN_SENDER_ID);
        } else if (m.getData().size() > 200) {
          response.setRejectReason(RejectReason.ILLEGAL_MESSAGE_SIZE);
        } else {
          MessageDTO mdto = new MessageDTO(m.getData(), sender_id, recipient_id);
          db.sendMessage(mdto);
          response.setState(MessageState.ACCEPTED);
        }

        observer.onNext(response.build());

      }

    };
  }

  @Override
  public void getMessages(GetMessageRequest request, StreamObserver<Message> observer) {
    var recipient_uuid = UUID.fromString(request.getRecipientId());
    try {
      MessageDTO m = null;

      while ((m = db.receiveMessage(recipient_uuid)) != null) {
        Message response = Message.newBuilder()
            .setSenderId(m.sender_id().toString())
            .setRecipientId(m.recipient_id().toString())
            .setData(m.bytes())
            .build();

        observer.onNext(response);
      }

    } catch (Exception e) {
      observer.onError(e);
    } finally {
      observer.onCompleted();
    }

  }

}
