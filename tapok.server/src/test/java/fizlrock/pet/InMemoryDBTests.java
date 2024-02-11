package fizlrock.pet;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import fizlrock.pet.Database.*;
import fizlrock.pet.Database.TapokDatabase.Message;

import org.junit.jupiter.api.Test;

import com.google.protobuf.ByteString;

/**
 * InMemoryDBTests
 */
public class InMemoryDBTests {

  private TapokDatabase db = new InMemoryDatabase();

  private ByteString getRandomBytes(){
    var bytes = new byte[100];
    ThreadLocalRandom.current().nextBytes(bytes);
    return ByteString.copyFrom(bytes);
  }

  private Message getRandomMessage(){
    var recipient = db.registerUser();
    var sender = db.registerUser();
    var bytes = getRandomBytes();
    return new Message(bytes, sender, recipient);
  }

  @Test
  void createUserTest() {
    var id = db.registerUser();
    assertTrue(db.hasUser(id));
  }

  @Test
  void noContainsNotExistingUser() {
    assertFalse(db.hasUser(UUID.randomUUID()));
  }

  @Test
  void sendMessageToNotExistingUser() {
    var sender = db.registerUser();
    var recipient = UUID.randomUUID();
    var bytes = getRandomBytes();

    Message m = new Message(bytes, sender, recipient);

    assertThrows(IllegalArgumentException.class, () -> {
      db.sendMessage(m);
    });

  }

  @Test
  void sendMessageFromNotExistingUser() {
    var recipient = db.registerUser();
    var sender = UUID.randomUUID();
    var bytes = new byte[100];
    ThreadLocalRandom.current().nextBytes(bytes);

    Message m = new Message(ByteString.copyFrom(bytes), sender, recipient);

    assertThrows(IllegalArgumentException.class, () -> {
      db.sendMessage(m);
    });
  }

  @Test
  void SendAndRecieveOneMessage(){
    var m = getRandomMessage();

    assertEquals(0, db.countMessageInBucket(m.recipient_id()), "Ящик должен быть пуст до отправки");

    db.sendMessage(m);

    assertEquals(1, db.countMessageInBucket(m.recipient_id()), "Ящик должен должен содержать одно сообщение после отправки");

    var recieved = db.receiveMessage(m.recipient_id());

    assertEquals(m.bytes(), recieved.bytes());
    assertEquals(0, db.countMessageInBucket(m.recipient_id()), "Ящик должен быть пуст после получения сообщения");

    
  }

}
