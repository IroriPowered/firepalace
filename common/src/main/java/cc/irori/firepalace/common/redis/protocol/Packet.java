package cc.irori.firepalace.common.redis.protocol;

import cc.irori.firepalace.common.redis.PacketHandler;
import cc.irori.firepalace.common.redis.Recipient;
import org.bson.BsonDocument;

public interface Packet<T extends PacketHandler<?>> {

  String getId();

  void handle(T handler);

  void serialize(BsonDocument root);

  Recipient getRecipient();
}
