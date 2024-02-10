package fizlrock.pet;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import fizlrock.pet.Database.*;
import org.junit.jupiter.api.Test;

/**
 * InMemoryDBTests
 */
public class InMemoryDBTests {

  private TapokDatabase db = new InMemoryDatabase();

  @Test
  void createUserTest(){
    var id = db.registerUser();
    assertTrue(db.hasUser(id));
  }

  @Test
  void noContainsNotExistingUser(){
    assertFalse(db.hasUser(UUID.randomUUID()));
  }

}
