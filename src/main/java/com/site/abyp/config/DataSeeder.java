package com.site.abyp.config;

import com.site.abyp.model.PostType;
import com.site.abyp.model.PostTypeField;
import com.site.abyp.model.Role;
import com.site.abyp.model.User;
import com.site.abyp.repository.PostTypeFieldRepository;
import com.site.abyp.repository.PostTypeRepository;
import com.site.abyp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Configuration
public class DataSeeder {

    @Autowired
    private PostTypeRepository postTypeRepository;

    @Autowired
    private PostTypeFieldRepository postTypeFieldRepository;

    @Bean
    public CommandLineRunner seedData(UserRepository userRepository, PostTypeRepository postTypeRepository, PasswordEncoder passwordEncoder) {
      return args -> {
          if (postTypeRepository.count() > 0) {
              System.out.println("Post types already");
              return;
          }
          LocalDateTime now = LocalDateTime.now();
          System.out.println("Seeding PostTypes. . .");
          PostType article = new PostType("article", "Article Content Type");
          article.setCreatedAt(now);
          article.setUpdatedAt(now);
          postTypeRepository.save(article);

          PostTypeField title = new PostTypeField();
          title.setName("title");
          title.setType("string");
          title.setRequired(true);
          title.setPostType(article);

          PostTypeField content = new PostTypeField();
          content.setName("content");
          content.setType("text");
          content.setRequired(true);
          content.setPostType(article);

          PostTypeField seoGroup = new PostTypeField();
          seoGroup.setName("seo");
          seoGroup.setType("text");
          seoGroup.setRequired(false);
          seoGroup.setPostType(article);

          postTypeFieldRepository.save(seoGroup);

          PostTypeField metaTitle = new PostTypeField();
          metaTitle.setName("meta_title");
          metaTitle.setType("string");
          metaTitle.setRequired(false);
          metaTitle.setParentField(seoGroup);
          metaTitle.setPostType(article);

          PostTypeField metaDescription = new PostTypeField();
          metaDescription.setName("meta_description");
          metaDescription.setType("text");
          metaDescription.setRequired(false);
          metaDescription.setParentField(seoGroup);
          metaDescription.setPostType(article);

          postTypeFieldRepository.save(title);
          postTypeFieldRepository.save(content);
          postTypeFieldRepository.save(metaTitle);
          postTypeFieldRepository.save(metaDescription);

          System.out.println("PostType 'article' seeded successfully!");
      };
    }
}
