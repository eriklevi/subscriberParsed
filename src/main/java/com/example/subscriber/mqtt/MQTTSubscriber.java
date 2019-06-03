package com.example.subscriber.mqtt;

import com.example.subscriber.HelperMethods;
import com.example.subscriber.SnifferLocationService;
import com.example.subscriber.entities.LocalPacket;
import com.example.subscriber.entities.Packet;
import com.example.subscriber.entities.SnifferLocation;
import com.example.subscriber.repositories.PacketsRepository;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class MQTTSubscriber implements MqttCallback, DisposableBean, InitializingBean {
    @Autowired
    private PacketsRepository packetsRepository;
    @Autowired
    private DiscoveryClient discoveryClient;
    private MqttClient mqttClient;
    @Autowired
    private SnifferLocationService snifferLocationService;

    private static final Logger logger = LoggerFactory.getLogger(MQTTSubscriber.class);

    private String broker;
    @Value("${mqtt.client-id}")
    private String clientId;
    @Value("${mqtt.topic}")
    private String topic;
    @Value("${mqtt.qos}")
    private int qos;
    @Value("${mqtt.ssl}")
    private Boolean hasSSL;
    @Value("${mqtt.auto-reconnect}")
    private Boolean autoReconnect;
    @Value("${mqtt.port}")
    private Integer port;
    @Value("${mqtt.use-credentials}")
    private boolean useCredentials;
    @Value("${mqtt.username}")
    private String userName;
    @Value("${mqtt.password}")
    private String password;
    @Value("${mqtt.keep-alive-seconds}")
    private int keepAliveInterval;

    private void config() {
        String brokerUrl = "tcp://" + this.broker + ":" + this.port;
        MemoryPersistence persistence = new MemoryPersistence();
        MqttConnectOptions connectionOptions = new MqttConnectOptions();
        try {
            this.mqttClient = new MqttClient(brokerUrl, clientId, persistence);
            connectionOptions.setCleanSession(true);
            connectionOptions.setAutomaticReconnect(autoReconnect); //try to reconnect to server from 1 second after fail up to 2 minutes delay
            if(useCredentials){
                connectionOptions.setUserName(userName);
                connectionOptions.setPassword(password.toCharArray());
            }
            connectionOptions.setKeepAliveInterval(keepAliveInterval);
            connectionOptions.setConnectionTimeout(0); //wait until connection successful or fail
            this.mqttClient.setCallback(this);
            this.mqttClient.connect(connectionOptions);
        } catch (MqttException me) {
            logger.error("resason "+ me.getReasonCode());
            logger.error("message "+ me.getMessage());
            logger.error("cause "+ me.getCause());
            me.printStackTrace();
        }
    }

    private void getBrokerInstance(){
        boolean success = false;
        List<ServiceInstance> instances = null;
        while(!success){
            instances = this.discoveryClient.getInstances("moquette");
            if(instances.size() == 0){
                try{
                    Thread.sleep(2500);
                }
                catch(Exception e){
                    logger.error("Exception in thread sleep");
                }
                logger.info("Impossible to get moquette instance....trying...");
            } else{
                success = true;
            }
        }
        this.broker = instances.get(0).getHost();
    }

    @Override
    public void connectionLost(Throwable throwable) {
        logger.warn("Connection with broker lost!");
    }

    /**
     * Callback invoked when a new MQTT message is received
     * The payload structure is as follows considering bytes:
     * 6: Sniffer MAC
     * 6: Source MAC (that can be either global or locally administered)
     * 2: Sequence Number of packet
     * 1: SSID len
     * up to 32: SSID
     * 16: fp
     *
     * Since all the analysis needed can be made on text data we can serialize the byte into a corresponding string and
     * store it in the db.
     * @param topic
     * @param mqttMessage
     * @throws Exception
     */
    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        try {
            String time = new Timestamp(System.currentTimeMillis()).toString();
            String payload = HelperMethods.bytesToHex(mqttMessage.getPayload());
            //serializziamo i dati come string visto che le analisi fatte sono a livello dei caratteri
            String snifferMac = payload.substring(0, 2)+ ":" + payload.substring(2,4) +":"+payload.substring(4, 6)+ ":"+payload.substring(6, 8)+ ":"+payload.substring(8, 10)+ ":"+payload.substring(10, 12);
            String deviceMac = payload.substring(12, 14)+ ":" + payload.substring(14,16) +":"+payload.substring(16, 18)+ ":"+payload.substring(18, 20)+ ":"+payload.substring(20, 22)+ ":"+payload.substring(22, 24);
            char letter = deviceMac.charAt(1);
            int ssid_len = Integer.parseInt(payload.substring(30, 32), 16);
            String ssid;
            Packet p;
            if(ssid_len != 0){
                ssid = HelperMethods.hexToAscii(payload.substring(32, 32+ssid_len*2));
            }
            else{
                ssid = "";
            }
            int sequenceNumber = Integer.parseInt(payload.substring(26, 28)+payload.charAt(24),16);
            /*
            For those of you who need to convert hexadecimal representation of a signed byte from two-character String into byte (which in Java is always signed), there is an example.
            Parsing a hexadecimal string never gives negative number, which is faulty, because 0xFF is -1 from some point of view (two's complement coding).
            The principle is to parse the incoming String as int, which is larger than byte, and then wrap around negative numbers. I'm showing only bytes, so that example is short enough.
             */
            int rssi = -256 + Integer.parseInt(payload.substring(28,30), 16);
            //attenzione che il sequence number è formato da 12 bit di sequence number e 4 bit di fragment number, vanno parsati solo i primi 3 caratteri hex
            if(letter == '0' || letter == '1' || letter == '4' || letter == '5' || letter == '8' || letter == '9' || letter == 'c' || letter == 'd'){
                //global
                p = new Packet(Instant.now().toEpochMilli(), snifferMac, deviceMac, true, sequenceNumber,ssid, ssid_len);
            }
            else{
                p = new LocalPacket(Instant.now().toEpochMilli(), snifferMac, deviceMac, false, sequenceNumber, ssid, ssid_len, payload.substring(30+ssid_len*2));
            }
            p.setRssi(rssi);
            LocalDateTime t = Instant.ofEpochMilli(p.getTimestamp()).atZone(ZoneId.of("CET")).toLocalDateTime();
            SnifferLocation snifferLocation = this.snifferLocationService.getSnifferLocation(p.getSnifferMac());
            if(snifferLocation == null){
                logger.info("snifferLocation is null for mac {}", p.getSnifferMac());
                return;
            }
            p.setSnifferName(snifferLocation.getName());
            p.setSnifferBuilding(snifferLocation.getBuilding());
            p.setSnifferRoom(snifferLocation.getRoom());
            p.setYear(t.getYear());
            p.setMonth(t.getMonthValue());
            p.setWeekOfYear(t.get(WeekFields.ISO.weekOfYear()));
            p.setDayOfMonth(t.getDayOfMonth());
            p.setDayOfWeek(t.getDayOfWeek().getValue());
            p.setHour(t.getHour());
            p.setQuarter(t.getMinute()/15+1); //raggruppo su 15 minuti 1-4...0-14 15-29.....
            p.setFiveMinute(t.getMinute()/5+1); //raggruppo per 5 minuti 1-12...0-4 5-9.....
            p.setTenMinute(t.getMinute()/10+1);
            p.setMinute(t.getMinute());
            p.setSnifferId(snifferLocation.getId());
            p.setSnifferBuildingId(snifferLocation.getBuildingId());
            p.setSnifferRoomId(snifferLocation.getRoomId());
            packetsRepository.save(p);
        } catch (Exception e) {
            logger.error(e.getMessage());
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }

    @Override
    public void destroy() throws Exception {
        this.mqttClient.disconnect();
        logger.info("Shutting down service, disconnecting from the broker");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.getBrokerInstance(); //uses Discovery client so it must be called after eureka setup
        this.snifferLocationService.update();
        this.config();
        this.mqttClient.subscribe(topic, this.qos);
    }
}
