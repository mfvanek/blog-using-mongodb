/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.perfect.blog.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.collections4.CollectionUtils;
import org.bson.Document;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class BlogPost {

    @BsonId
    private ObjectId id;

    private String title;
    private String author;
    private String body;
    private String permalink;

    private List<String> tags = new ArrayList<>();
    private List<Comment> comments = new ArrayList<>();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMM d, yyyy h:mm:ss a")
    private Date date;

    public Map<String, Object> toMap() {
        final ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(this, new TypeReference<>() {});
    }

    public Document toDocument() {
        return new Document(toMap());
    }

    public BlogPost(final Document document) {
        this(document.getObjectId("_id"),
                document.getString("title"),
                document.getString("author"),
                document.getString("body"),
                document.getString("permalink"),
                extractTags(document),
                extractComments(document),
                document.getDate("date"));
    }

    private static List<String> extractTags(final Document document) {
        @SuppressWarnings("unchecked") final List<String> tags = document.get("tags", List.class);
        return tags;
    }

    private static List<Comment> extractComments(final Document document) {
        @SuppressWarnings("unchecked") final List<Document> rawComments = document.get("comments", List.class);
        if (CollectionUtils.isNotEmpty(rawComments)) {
            return rawComments.stream().map(Comment::new).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public static String makePermalink(final String title) {
        String permalink = title.replaceAll("\\s", "_"); // whitespace becomes _
        permalink = permalink.replaceAll("\\W", ""); // get rid of non alphanumeric
        return permalink.toLowerCase(Locale.ROOT);
    }
}
