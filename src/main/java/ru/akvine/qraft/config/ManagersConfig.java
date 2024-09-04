package ru.akvine.qraft.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.akvine.qraft.enums.ImageType;
import ru.akvine.qraft.managers.ImageConverterManager;
import ru.akvine.qraft.services.image.ImageConverterService;

import java.util.List;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@Configuration
public class ManagersConfig {
    @Bean
    public ImageConverterManager imageConverterManager(List<ImageConverterService> converterServices) {
        Map<ImageType, ImageConverterService> imageFacades = converterServices
                .stream()
                .collect(toMap(ImageConverterService::getType, identity()));
        return new ImageConverterManager(imageFacades);
    }
}
