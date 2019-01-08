package com.mfvanek.perfect.blog.entities;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bson.Document;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Comment implements Serializable {

    private String author;
    private String body;
    private String email;

    @BsonProperty(value = "num_likes")
    private Integer likes;

    public Map<String, Object> toMap() {
        final ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(this, new TypeReference<Map<String, Object>>() {});
    }

    public Document toDocument() {
        return new Document(toMap());
    }

    Comment(final Document document) {
        this(document.getString("author"),
                document.getString("body"),
                document.getString("email"),
                document.getInteger("num_likes", 0));
    }
}
