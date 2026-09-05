package com.bookverse.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Per the spec (section 25): "Prefer local storage for the college
// project" rather than introducing Cloudinary/AWS. This maps the URL
// path /media/** directly to a folder on disk, so an audio file saved at
// {audioRoot}/stories/haunted-village/episode-1.mp3 becomes reachable at
// http://localhost:8080/media/stories/haunted-village/episode-1.mp3 -
// no separate file-serving endpoint/controller needed, Spring handles it.
@Configuration
public class MediaConfig implements WebMvcConfigurer {

    @Value("${bookverse.storage.audio-root}")
    private String audioRoot;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // The trailing "file:" + "/" is required syntax for Spring to
        // treat this as a filesystem path rather than a classpath one.
        registry.addResourceHandler("/media/**")
                .addResourceLocations("file:" + audioRoot + "/");
    }
}
