package cc.irori.firepalace.common.redis;

import cc.irori.firepalace.common.redis.protocol.Packet;
import org.bson.BsonDocument;

public interface PacketFactory<T extends PacketHandler<?>> {

  Packet<T> create(BsonDocument root);
}
