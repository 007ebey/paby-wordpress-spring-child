package com.site.abyp.service;

import com.site.abyp.model.PostType;
import lombok.Data;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;



public class PostTypeRegistryService {

    private Map<String, PostType> registry = new HashMap<>();
    public void registerPostType(PostType postType) {
        if (postType == null || postType.getId() == null) {
            throw new IllegalArgumentException("PostType and its id must not be null");
        }
        String id = postType.getId().toString();
        if (registry.containsKey(id)) {
            throw new IllegalArgumentException("Post type already registered: " + id);
        }
        registry.put(id, postType);
    }
    public PostType getPostType(String id) {
        if (id == null) {
            return null;
        }
        return registry.get(id);
    }
    public boolean unregisterPostType(String id) {
        if (id == null) {
            return false;
        }
        return registry.remove(id) != null;
    }
    public Collection<PostType> getAllPostTypes() {
        return Collections.unmodifiableCollection(registry.values());
    }
    public boolean isRegistered(String id) {
        return id != null && registry.containsKey(id);
    }
}
