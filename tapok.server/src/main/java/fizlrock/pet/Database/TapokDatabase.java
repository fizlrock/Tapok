
package fizlrock.pet.Database;

import java.util.UUID;

import com.google.protobuf.ByteString;

/**
 * Интерфейс описывающий базы данных для брокера посылок
 * Должны быть реализованы следующие методы:
 * 1. Регистрация пользователя
 * 2. Отправка сообщений
 * 3. Получение сообщенийй
 */
public interface TapokDatabase {

  public record Message(ByteString message, UUID sender_id, UUID recipient_id) {
  }

  /**
   * Метод регистрирует нового пользователя, и возращает его ID
   * 
   * @return
   */
  public UUID registerUser();

  public boolean hasUser(UUID id);

  /**
   * Заносит в базу данных сообщение.
   * 
   * @param m - сообщение {@link Message}
   */
  public void sendMessage(Message m);

  /**
   * Проверяет ящик пользователя
   * 
   * @param recipient_id - ID пользователя для проверки
   * @return количество сообщений для пользователя
   */
  public int countMessageInBucket(UUID recipient_id);

  /**
   * Метод возвращает список сообщений для пользователя. При список имеет длину не
   * более limit
   * 
   * @param recipient_id - ID получателя
   * @param limit        - максимальное число сообщений
   * @return List<Message>
   */
  public Message receiveMessage(UUID recipient_id, int limit);

}
