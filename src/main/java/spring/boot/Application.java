package spring.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        System.out.println("contunie");
        try//
        {
            Bot bot=new Bot();
            telegramBotsApi.registerBot(bot);


        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
        SpringApplication.run(Application.class, args);

    }

}