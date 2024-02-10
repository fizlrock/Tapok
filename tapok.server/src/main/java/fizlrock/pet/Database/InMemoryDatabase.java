
package fizlrock.pet.Database;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class InMemoryDatabase implements TapokDatabase {

  private Set<UUID> users = new HashSet<>();
  private Map<UUID, Message> buckets = new HashMap<>();

  @Override
  public UUID registerUser() {
    UUID id = UUID.randomUUID();
    users.add(id);
    return id;
  }

  @Override
  public boolean hasUser(UUID id) {
    return users.contains(id);
  }

  @Override
  public void sendMessage(Message m) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'sendMessage'");
  }

  @Override
  public int countMessageInBucket(UUID recipient_id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'countMessageInBucket'");
  }

  @Override
  public Message receiveMessage(UUID recipient_id, int limit) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'receiveMessage'");
  }

}
