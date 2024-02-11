package fizlrock.pet;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import fizlrock.pet.Database.*;
import fizlrock.pet.Database.TapokDatabase.MessageDTO;

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

  private MessageDTO getRandomMessage(){
    var recipient = db.registerUser();
    var sender = db.registerUser();
    var bytes = getRandomBytes();
    return new MessageDTO(bytes, sender, recipient);
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

    MessageDTO m = new MessageDTO(bytes, sender, recipient);

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

    MessageDTO m = new MessageDTO(ByteString.copyFrom(bytes), sender, recipient);

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
