package com.example.demo.stream;

import com.example.demo.model.MarketData;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.core.MessageSource;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;

import java.util.Random;

import static com.example.demo.constants.Constants.MARKET_DATA_SOURCE;

@SpringBootApplication
@EnableBinding(MarketDataProducer.Source.class)
public class MarketDataProducer {

    public static void main(String[] args) {
        SpringApplication.run(MarketDataProducer.class, args);
    }

    @Bean
    @InboundChannelAdapter(channel = MARKET_DATA_SOURCE, poller = @Poller(fixedDelay = "3000"))
    public MessageSource<MarketData> producer() {
        return () -> new GenericMessage<>(new MarketData(System.currentTimeMillis(), "ES",
                new Random().nextGaussian()));
    }

    public interface Source {
        @Output(MARKET_DATA_SOURCE)
        MessageChannel source();
    }

}
