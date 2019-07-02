package spring.boot;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class Application {
    static Bot bot;
    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        System.out.println("contunie");
        try//
        {
             bot=new Bot();
            telegramBotsApi.registerBot(bot);


        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
        SpringApplication.run(Application.class, args);
        final ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        ses.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {

                try {


                }catch (Exception e)
                {
                    e.printStackTrace();
                }
                System.out.println("1111111111111111111111111111");
            }
        }, 0, 20, TimeUnit.MINUTES);


    }


}