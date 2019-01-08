package com.mfvanek.perfect.blog.utils;

import com.mfvanek.perfect.blog.consts.Const;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

public final class ConnectionUtils {

    private ConnectionUtils() {}

    public static MongoDatabase getDatabase(final String host) {
        final CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(
                MongoClient.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        final MongoClient mongoClient = new MongoClient(
                new ServerAddress(host),
                MongoClientOptions.builder().codecRegistry(pojoCodecRegistry).build());
        return mongoClient.getDatabase(Const.DATABASE_NAME);
    }
}
