package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.dto.ItemDTO;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> addItem(int userID, ItemDTO dto) {
        return post("", userID, dto);
    }

    public ResponseEntity<Object> addCommentToItemByUser(int itemID, int userID, CommentDTO dto) {
        String path = String.format("/%d/comment", itemID);
        return post(path, userID, dto);
    }

    public ResponseEntity<Object> patchItem(int userID, int itemID, ItemDTO dto) {
        return patch("/" + itemID, userID, dto);
    }

    public ResponseEntity<Object> getItemsOfUserByID(long userID, int from, int size) {
        Map<String, Object> params = Map.of("from", from, "size", size);
        return get("?from={from}&size={size}", userID, params);
    }

    public ResponseEntity<Object> getItemByID(int userID, int itemID) {
        return get("/" + itemID, userID);
    }

    public ResponseEntity<Object> searchItemsByText(long userID, String text, int from, int size) {
        Map<String, Object> params = Map.of("text", text, "from", from, "size", size);
        return get("/search?text={text}&from={from}&size={size}", userID, params);
    }
}
