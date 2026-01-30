package cc.irori.firepalace.api.game.metadata;

import java.util.List;
import org.bson.BsonArray;
import org.bson.BsonBoolean;
import org.bson.BsonDocument;
import org.bson.BsonString;

public record GameMetadata(
    String id,
    String name,
    String description,
    List<GameTag> tags,
    boolean available
) {

  public BsonDocument serialize(BsonDocument root) {
    root.append("id", new BsonString(id));
    root.append("name", new BsonString(name));
    root.append("description", new BsonString(description));
    root.append("available", new BsonBoolean(available));

    BsonArray tagsArray = new BsonArray();
    for (GameTag tag : tags) {
      tagsArray.add(new BsonString(tag.name()));
    }
    root.append("tags", tagsArray);
    return root;
  }

  public static GameMetadata deserialize(BsonDocument root) {
    String id = root.getString("id").getValue();
    String name = root.getString("name").getValue();
    String description = root.getString("description").getValue();
    boolean available = root.getBoolean("available").getValue();

    BsonArray tagsArray = root.getArray("tags");
    List<GameTag> tags = tagsArray.stream()
        .map(bsonValue -> GameTag.valueOf(bsonValue.asString().getValue()))
        .toList();

    return new GameMetadata(id, name, description, tags, available);
  }
}
