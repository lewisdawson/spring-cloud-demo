package com.example.demo.stream;

import com.example.demo.constants.Constants;
import com.example.demo.model.MarketData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.SubscribableChannel;

@Slf4j
@SpringBootApplication
public class MarketDataConsumer {

    public static void main(String[] args) {
        SpringApplication.run(MarketDataConsumer.class, args);
    }

    @EnableBinding(MarketDataConsumer.Sink.class)
    static class Consumer {

        @StreamListener(Constants.MARKET_DATA_SINK)
        public void consumer(MarketData marketData) {
            log.info(marketData.toString());
        }

    }

    public interface Sink {

        @Input(Constants.MARKET_DATA_SINK)
        SubscribableChannel sink();

    }

}
