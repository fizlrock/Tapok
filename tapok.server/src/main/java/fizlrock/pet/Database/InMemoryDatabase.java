
package fizlrock.pet.Database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class InMemoryDatabase implements TapokDatabase {

  private Set<UUID> users = new HashSet<>();
  private Map<UUID, List<MessageDTO>> buckets = new HashMap<>();

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

  private void validateMessage(MessageDTO m) {
    if (!hasUser(m.sender_id()))
      throw new IllegalArgumentException("Отправитель с таким ID не зарегистирован");
    if (!hasUser(m.recipient_id()))
      throw new IllegalArgumentException("Получатель с таким ID не зарегистирован");

    if (m.bytes().size() > 100)
      throw new IllegalArgumentException("Слишком большой размер сообщения");

  }

  @Override
  public void sendMessage(MessageDTO m) {
    validateMessage(m);
    if (!buckets.containsKey(m.recipient_id())) {
      buckets.put(m.recipient_id(), new ArrayList<MessageDTO>());
    }
    buckets.get(m.recipient_id()).add(m);

  }

  @Override
  public int countMessageInBucket(UUID recipient_id) {
    int result = 0;
    if (buckets.containsKey(recipient_id))
      result = buckets.get(recipient_id).size();

    return result;
  }

  @Override
  public MessageDTO receiveMessage(UUID recipient_id) {
    MessageDTO m = null;

    if (buckets.containsKey(recipient_id)) {
      var bucket = buckets.get(recipient_id);
      if (bucket.size() > 0)
        m = buckets.get(recipient_id).removeLast();
    }
    return m;
  }

}
