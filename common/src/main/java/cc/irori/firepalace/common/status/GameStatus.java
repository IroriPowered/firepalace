package cc.irori.firepalace.common.status;

import cc.irori.firepalace.api.game.metadata.GameMetadata;
import java.util.List;
import java.util.UUID;
import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonString;

public record GameStatus(
    GameMetadata metadata,
    List<UUID> users
) {

  public BsonDocument serialize(BsonDocument root) {
    root.append("metadata", metadata.serialize(new BsonDocument()));

    BsonArray usersArray = new BsonArray();
    for (UUID uuid : users) {
      usersArray.add(new BsonString(uuid.toString()));
    }
    root.append("users", usersArray);
    return root;
  }

  public static GameStatus deserialize(BsonDocument root) {
    GameMetadata metadata = GameMetadata.deserialize(root.getDocument("metadata"));

    BsonArray usersArray = root.getArray("users");
    List<UUID> users = usersArray.stream()
        .map(bsonValue -> UUID.fromString(bsonValue.asString().getValue()))
        .toList();

    return new GameStatus(metadata, users);
  }
}
