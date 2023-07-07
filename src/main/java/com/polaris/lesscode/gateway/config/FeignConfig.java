package com.polaris.lesscode.gateway.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import feign.Logger;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;

@Configuration
public class FeignConfig {
	
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
    
//    @Bean
//    public Decoder feignDecoder() {
//        return new ResponseEntityDecoder(new SpringDecoder(feignHttpMessageConverter()));
//    }
//
//    public ObjectFactory<HttpMessageConverters> feignHttpMessageConverter() {
//        final HttpMessageConverters httpMessageConverters = new HttpMessageConverters(new GateWayMappingJackson2HttpMessageConverter());
//        return new ObjectFactory<HttpMessageConverters>() {
//            @Override
//            public HttpMessageConverters getObject() throws BeansException {
//                return httpMessageConverters;
//            }
//        };
//    }
//
//    public class GateWayMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {
//        GateWayMappingJackson2HttpMessageConverter(){
//            List<MediaType> mediaTypes = new ArrayList<>();
//            mediaTypes.add(MediaType.APPLICATION_FORM_URLENCODED);
//            setSupportedMediaTypes(mediaTypes);
//        }
//    }
    
    @Bean
    public Decoder decoder() {
        return new JacksonDecoder();
    }

    @Bean
    public Encoder encoder() {
        return new JacksonEncoder();
    }
}