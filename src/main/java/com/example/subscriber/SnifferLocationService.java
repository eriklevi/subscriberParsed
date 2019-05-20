package com.example.subscriber;

import com.example.subscriber.entities.SnifferLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class SnifferLocationService {

    private Map<String, SnifferLocation> snifferLocationMap;
    @Autowired
    private DiscoveryClient discoveryClient;
    private static final Logger logger = LoggerFactory.getLogger(SnifferLocationService.class);

    public SnifferLocationService() {
        this.snifferLocationMap = new ConcurrentHashMap<String, SnifferLocation>();
    }

    public SnifferLocation getSnifferLocation(String mac){
        return this.snifferLocationMap.get(mac);
    }

    public void update() {
        logger.info("Received update request");
        boolean success = false;
        List<ServiceInstance> instances = null;
        while(!success){
            instances = this.discoveryClient.getInstances("snifferservice");
            if(instances.size() == 0){
                try{
                    Thread.sleep(2500);
                }
                catch(Exception e){
                    logger.error("Exception in thread sleep");
                }
                logger.info("Impossible to get snifferservice instance....trying...");
            } else{
                success = true;
            }
        }
        String snifferServiceUri = String.format("%s/sniffers/locations", instances.get(0).getUri().toString());
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List<SnifferLocation>> restExchange = restTemplate.exchange(
                snifferServiceUri,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<SnifferLocation>>(){}
        );
        snifferLocationMap = restExchange
                .getBody()
                .stream()
                .collect(Collectors.toMap(
                        SnifferLocation::getMac, Function.identity()
                ));
        logger.info("Update terminated");
    }
}
