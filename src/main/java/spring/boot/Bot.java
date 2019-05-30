package spring.boot;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import io.github.biezhi.webp.WebpIO;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.api.objects.stickers.Sticker;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.apache.commons.io.FileUtils;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;

import javax.xml.crypto.Data;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Time;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Bot extends TelegramLongPollingBot {

    int count_zakazov;
    int positive_=0;
    int negative_=0;


    //799964941

    TreeMap<Long,Users> user=new TreeMap<>();


    static long chatid =799964941;

    static String info_for_start="Привет! \n" +
            "Я распечатаю и отправлю тебе твои любимые стикеры из Телеграмма!\n" +
            "\n" +
            "\uD83D\uDC4C 12 стикеров на листе А5\n" +
            "\uD83D\uDCB3 Стоимость одного набора - 50 грн\n" +
            "\uD83D\uDCE6 Доставка по всей Украине, удобным для вас способом (оплачивается отедльно)\n" +
            "\n" +
            "Нажми Создать StickerPack  чтобы начать!";



    public ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

    Users usere;

    @Override
    public void onUpdateReceived(Update update) {


        if(update.hasMessage()) {


            if(user.containsKey(update.getMessage().getChatId()))
                usere=user.get(update.getMessage().getChatId());
            else {
                user.put(update.getMessage().getChatId(), new Users(update.getMessage().getChatId(), new ArrayList<Sticker>(), update.getMessage().getFrom().getFirstName()));
                usere=user.get(update.getMessage().getChatId());
            }

            System.out.println(update.getMessage().getFrom().getFirstName());
            Message message = update.getMessage();
            if (message != null && message.hasText()) {

                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(usere.getChatid());

                System.out.println(message.getChatId());

                replyKeyboardMarkup.setResizeKeyboard(true);
                replyKeyboardMarkup.setOneTimeKeyboard(true);
                replyKeyboardMarkup.setSelective(true);

                switch (message.getText()) {
                    case "/inf":
                        for (Users us:user.values())
                        {
                            try {
                                sendApiMethod(new SendMessage().setText(us.toString()).setChatId(chatid));
                            } catch (TelegramApiException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    case "/start":

                        try {
                            sendApiMethod(new SendMessage(usere.getChatid(),"Идет обработка запроса, подождите пожайлуста...."));
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }


                        try {
                            System.out.println("start");
                            execute( (new SendPhoto().setChatId(usere.getChatid()).setPhoto(new File("src/main/resources/start.jpg")).setCaption(info_for_start))
                                    .setReplyMarkup(remakeButtons(usere,message.getText(), replyKeyboardMarkup, usere.getStickers().size())));
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                        usere.Rebuild();
                        break;
                    case "О боте":
                        System.out.println("about bot");
                        try {
                            sendApiMethod(sendMessage.setText("\n" +
                                    "С помощью данного бота - стало возможным создать StickerPack из Ваших любимых наборов стикеров!\n" +
                                    "Все просто, для начала работы с ботом - отправьте любой стикер.\n" +
                                    "\n" +
                                    "По всем вопросам касательно работы данного бота, пишите - @stickers_kiev").setReplyMarkup(remakeButtons(usere,message.getText(), replyKeyboardMarkup, usere.getStickers().size())));
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                        break;
                        case "Создать StickerPack":

                        System.out.println("about sdelat maket");
                        try {
                            usere.setStickers( new ArrayList<Sticker>());
                            usere.setZakaz();
                            sendApiMethod(sendMessage.setText("Отправьте первый стикер").setReplyMarkup(remakeButtons(usere,"hide", replyKeyboardMarkup, usere.getStickers().size())));
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "/alex":
                        Message mes1=update.getMessage();
                        SendMessage message1=new SendMessage();
                        message1.setText("Общее количество заказов: "+ String.valueOf(count_zakazov)+"\n"+"Позитивние отзыви: "+positive_+"\n"+"Негативние отзыви: "+negative_+"\n"+"Не оставили отзыв: "+(count_zakazov-positive_-negative_));
                        message1.setChatId(usere.getChatid());
                        try {
                            sendApiMethod(message1);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "/res":
                        count_zakazov=0;
                        positive_=0;
                        negative_=0;
                        break;

                    default:


                        try {
                            if (message.getText()!=null) {
                                sendMessage.setChatId(chatid);
                                sendMessage.setText("Данные пользователя :"+usere.getName()+", по заказу №:" +usere.getChatid() +"\n"+message.getText());
                                execute(sendMessage);
                                execute((sendInlineKeyBoardMessage(usere.getChatid(),3)
                                        .setText("Выбеите способ оплаты")));
                                user.get(usere.getChatid()).setZakaz();
                            } else {
                                sendMessage.setText("введите запрос /start ещё раз и повторите создание макета");
                                sendApiMethod(sendMessage.setReplyMarkup(remakeButtons(usere,message.getText(), replyKeyboardMarkup, usere.getStickers().size())));
                            }
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }//To worker make primary message
            if (message != null && message.hasSticker()) {
                if(!user.containsKey(update.getMessage().getChatId()))
                {
                    user.put(update.getMessage().getChatId(), new Users(update.getMessage().getChatId(), new ArrayList<Sticker>(),message.getFrom().getFirstName()));
                }
                if (user.containsKey(message.getChatId()))
                {

                    usere=user.get(update.getMessage().getChatId());
                }

                System.out.println(message.getChatId());
                if (usere.getStickers().size() < 12) {
                    usere.getStickers().add(message.getSticker());
                    SendPhoto sendMessage = new SendPhoto();
                    sendMessage.setChatId(usere.getChatid());


                    try {
                        getSticker(message, usere.getStickers().size(),usere);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    try {
                        usere.setCount(usere.getCount()+1);
                        DecodedWebP(usere,usere.getCount());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    try {
                        sendApiMethod(sendInlineKeyBoardMessage(usere.getChatid(),1).setText("Свободных мест на листе:  "
                                + (12 - usere.getCount()) ));
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
                if (usere.getStickers().size() >= 12) {

                    try {
                        SendPhoto send =usere.getPreview();
                        SendMessage sendMessage = new SendMessage();
                        sendMessage.setChatId(usere.getChatid());
//                       sendApiMethod(sendInlineKeyBoardMessage(usere.getChatid(),2).setText("Теперь давайте оформим заказ"));
                        sendApiMethod(sendMessage.setText("1.Для оформления заказа - \"Оформить\".\n" +
                                "2.Для предосмотра StickerPack - \"Превью\".\n" +
                                "3.Для создания нового StickerPack - \"Начать сначала\"."));

                        send.setChatId(chatid);

                        System.out.println("my photo");
                        execute(send);


                    } catch (IOException e) {
                        e.printStackTrace();
                    }  catch (TelegramApiException e) {
                        e.printStackTrace();
                    }



                }

            } //To worker make stickers messege
        }
        if(update.hasCallbackQuery()){
            if(user.containsKey(update.getCallbackQuery().getMessage().getChatId()))
                usere=user.get(update.getCallbackQuery().getMessage().getChatId());
            else {
                user.put(update.getCallbackQuery().getMessage().getChatId(), new Users(update.getCallbackQuery().getMessage().getChatId(), new ArrayList<Sticker>(), update.getCallbackQuery().getMessage().getFrom().getFirstName()));
                usere=user.get(update.getCallbackQuery().getMessage().getChatId());
            }


            switch (update.getCallbackQuery().getData())
            {

                case "preview":


                    System.out.println("about preview"+usere.getCount());
                    try {
                        usere.AddPhotoToTemplate();
                        execute(usere.getPreview().setChatId(usere.getChatid()));
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(usere.getStickers().size()<12){
                        try {
                            sendApiMethod(new SendMessage(usere.getChatid(),"Для продолжения оформления StickerPack, отправьте стикер!!"));
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }

                    break;
                case "enter":
                    count_zakazov+=1;
                    try {
                        execute(sends("Имя Пользователя : "+usere.getName()+"\n"+"Заказ №"+usere.getChatid(),+chatid));
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    usere.setZakaz(usere.getZakaz()+1);

                    System.out.println("about enter");

                    SendMessage sendMessage =new SendMessage();
                    sendMessage.setChatId(usere.getChatid());
                    sendMessage.setText("Для оформления заказа укажите:\n" +
                            "1) ФИО\n" +
                            "2) Город доставки\n" +
                            "3) Номер телефона(Обязательно, иначе мы не сможем с вами связаться..)");
                    try {
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }

                    try {

                        System.out.println("worker photo");
                        usere.AddPhotoToTemplate();
                        execute(new SendDocument().setDocument(new File(usere.getScreenName()+"stickerpack.png")).setChatId(chatid));
                        execute(new SendMessage().setText("Макет под номером заказа:"+usere.getChatid()).setChatId(chatid));
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    System.out.println("rebuild photo");
                    usere.Rebuild();


                    break;
                case "otmena":

                    System.out.println("about otmena");
                    try {
                        usere.Rebuild();
                        execute(new SendMessage().setChatId(usere.getChatid()).setText("Нажмите /start для повторого создания набора!"));
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    break;

                case "naloz":
                    SendMessage sendMessa=new SendMessage();
                    sendMessa.setChatId(chatid);
                    sendMessa.setText("Способ оплаты по заказу №:" + usere.getChatid()+"\n"+"Вид оплаты: При получении");
                    try {
                        execute(sendMessa);
                        execute(sendInlineKeyBoardMessage(usere.getChatid(),4).setText("Вам понравился наш сервис \uD83D\uDE80?"));
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    usere.Rebuild();


                    break;
                case "na_carty":
                    SendMessage sendMessa1=new SendMessage();
                    sendMessa1.setChatId(chatid);
                    sendMessa1.setText("Способ оплаты по заказу №:" + usere.getChatid()+"\n"+"Вид оплаты: На карту");

                    try {
                        execute(sendInlineKeyBoardMessage(usere.getChatid(),5).setText("Выбирите банк :"));
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }


                    break;
                case "privat":
                    SendMessage sendMessas1=new SendMessage();
                    sendMessas1.setChatId(chatid);
                    sendMessas1.setText("Способ оплаты на карту Приват Банка");
                    try {
                        execute(sendMessas1);
                    }catch (Exception e)
                    {
                        System.out.println(e.getStackTrace());
                    }

                    sendMessas1.setChatId(usere.getChatid());
                    sendMessas1.setText("Номер карты ПриватБанк: \n 5169360005626969\n"+"https://privatbank.ua/ru/sendmoney");

                    try {
                        execute(sendMessas1);
                        execute(sendInlineKeyBoardMessage(usere.getChatid(),4).setText("Вам понравился наш сервис?"));
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }



                    usere.Rebuild();
                    break;
                case "mono":
                    SendMessage sendMessas2=new SendMessage();
                    sendMessas2.setChatId(chatid);
                    sendMessas2.setText("Способ оплаты на карту МоноБанка");
                    try {
                        execute(sendMessas2);
                    }catch (Exception e)
                    {
                        System.out.println(e.getStackTrace());
                    }

                    sendMessas2.setChatId(usere.getChatid());
                    sendMessas2.setText("Номер карты Моно Банк: \n 5375414103174180\n");

                    try {
                        execute(sendMessas2);
                        execute(sendInlineKeyBoardMessage(usere.getChatid(),4).setText("Вам понравился наш сервис?"));
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    usere.Rebuild();
                    break;
                case "yes":
                    try {
                        sendApiMethod(new SendMessage(usere.getChatid(),"Спасибо за Ваш отзыв!\n" +
                                "Мы свяжемся с Вами в ближайшее время, для создания еще одного набора - нажимте /start"));
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    positive_+=1;

                    try {
                        execute(sends(usere.getName()+": "+usere.getChatid()+" Этому пользователю понравился сервис ",chatid ));
                        execute( (new SendPhoto().setChatId(usere.getChatid()).setPhoto(new File("src/main/resources/start.jpg")).setCaption(info_for_start))
                                .setReplyMarkup(remakeButtons(usere,"/start", replyKeyboardMarkup, usere.getStickers().size())));
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    break;
                case"no":
                    try {
                        sendApiMethod(new SendMessage(usere.getChatid(),"Спасибо за Ваш отзыв!\n" +
                                "Мы свяжемся с Вами в ближайшее время, для создания еще одного набора - нажимте /start"));
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    negative_+=1;
                    try {
                        execute(sends( usere.getChatid()+" Этому пользователю не понравился сервис ",chatid));
                        execute( (new SendPhoto().setChatId(usere.getChatid()).setPhoto(new File("src/main/resources/start.jpg")).setCaption(info_for_start))
                                .setReplyMarkup(remakeButtons(usere,"/start", replyKeyboardMarkup, usere.getStickers().size())));
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    break;
            }

        } //Call inlineButton
    }

    public static boolean openWebpage(URI uri){
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {

            try {

                desktop.browse(uri);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean openWebpage(URL url) {
        try {
            return openWebpage(url.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return false;
    }
    //Send primary message
    public SendMessage sends(String s,Long chat){
        SendMessage ser=new SendMessage();
        ser.setChatId(chat);
        ser.setText(s);
        return ser;


    }
    //Rebuild all pictures to new

    //Remake inlineButtons
    public static SendMessage sendInlineKeyBoardMessage(long chatId,int n) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        if(n==1) {

            InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
            InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
            InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();

            inlineKeyboardButton1.setText("Превью \uD83D\uDDBC");
            inlineKeyboardButton1.setCallbackData("preview");
            inlineKeyboardButton2.setText("Оформить ✅");
            inlineKeyboardButton2.setCallbackData("enter");
            inlineKeyboardButton3.setText("Начать сначала ↩️");
            inlineKeyboardButton3.setCallbackData("otmena");
            List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
            List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
            keyboardButtonsRow1.add(inlineKeyboardButton1);
            keyboardButtonsRow1.add(inlineKeyboardButton2);
            keyboardButtonsRow2.add(inlineKeyboardButton3);
            List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
            rowList.add(keyboardButtonsRow1);
            rowList.add(keyboardButtonsRow2);
            inlineKeyboardMarkup.setKeyboard(rowList);
        }
        if(n==2)
        {
            InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
            inlineKeyboardButton1.setText("Оформить ✅");
            inlineKeyboardButton1.setCallbackData("enter");
            List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
            keyboardButtonsRow1.add(inlineKeyboardButton1);
            List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
            rowList.add(keyboardButtonsRow1);
            inlineKeyboardMarkup.setKeyboard(rowList);
        }
        if(n==3)
        {
            InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
            InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
            inlineKeyboardButton1.setText("При получении \uD83D\uDECD");
            inlineKeyboardButton1.setCallbackData("naloz");
            inlineKeyboardButton2.setText("На карту \uD83D\uDCB3 ");
            inlineKeyboardButton2.setCallbackData("na_carty");

            List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
            keyboardButtonsRow1.add(inlineKeyboardButton1);
            keyboardButtonsRow1.add(inlineKeyboardButton2);

            List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
            rowList.add(keyboardButtonsRow1);
            inlineKeyboardMarkup.setKeyboard(rowList);
        }
        if(n==4)
        {
            InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
            InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
            inlineKeyboardButton1.setText("Да\uD83D\uDC4D");
            inlineKeyboardButton1.setCallbackData("yes");
            inlineKeyboardButton2.setText("Нет\uD83D\uDC4E");
            inlineKeyboardButton2.setCallbackData("no");

            List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
            keyboardButtonsRow1.add(inlineKeyboardButton1);
            keyboardButtonsRow1.add(inlineKeyboardButton2);

            List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
            rowList.add(keyboardButtonsRow1);
            inlineKeyboardMarkup.setKeyboard(rowList);

        }
        if(n==5)
        {
            InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
            InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
            inlineKeyboardButton1.setText("Приват банк");
            inlineKeyboardButton1.setCallbackData("privat");
            inlineKeyboardButton2.setText("Моно банк");
            inlineKeyboardButton2.setCallbackData("mono");

            List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
            keyboardButtonsRow1.add(inlineKeyboardButton1);
            keyboardButtonsRow1.add(inlineKeyboardButton2);

            List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
            rowList.add(keyboardButtonsRow1);
            inlineKeyboardMarkup.setKeyboard(rowList);

        }

        return new SendMessage().setChatId(chatId).setText("Пример").setReplyMarkup(inlineKeyboardMarkup);
    }
    //Getter stickers from message
    public void getSticker(Message message, int numbers, Users user) throws MalformedURLException {
        GetFile getFile = new GetFile();
        getFile.setFileId(message.getSticker().getFileId());

        org.telegram.telegrambots.meta.api.objects.File file = null;
        try {
            file = execute(getFile);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        InputStream fileUrl = null;
        try {
            fileUrl = new URL(file.getFileUrl(getBotToken())).openStream();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File localFile = new File(user.getScreenName()+"sticker" + numbers + ".webp");

        try {
            FileUtils.copyInputStreamToFile(fileUrl, localFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //Remake replyButtons
    public synchronized ReplyKeyboardMarkup remakeButtons(Users user,String s, ReplyKeyboardMarkup replyKeyboardMarkup, int n) {
        KeyboardRow keyboardRow1 = new KeyboardRow();
        KeyboardButton keyboardButton1 = new KeyboardButton();
        KeyboardButton keyboardButton2 = new KeyboardButton();
        keyboardButton1.setText("О боте");
        keyboardButton2.setText("Создать StickerPack");
        keyboardRow1.add(keyboardButton1);
        keyboardRow1.add(keyboardButton2);
        List<KeyboardRow> klava = new ArrayList<KeyboardRow>();
        klava.add(keyboardRow1);
        replyKeyboardMarkup.setKeyboard(klava);
        switch (s) {
            case"hide":
                keyboardRow1.clear();
                klava = new ArrayList<KeyboardRow>();
                klava.add(keyboardRow1);
                replyKeyboardMarkup.setKeyboard(klava);
                return replyKeyboardMarkup;
            case "да":
                return replyKeyboardMarkup;
            case "нет":
                return replyKeyboardMarkup;
            case "/start":
                return replyKeyboardMarkup;
            case "О боте":
                keyboardRow1.clear();
                keyboardRow1.add(keyboardButton1.setText("Создать StickerPack"));
                return replyKeyboardMarkup;
            case "/all":
                keyboardRow1.clear();
                klava.add(keyboardRow1);
                replyKeyboardMarkup.setKeyboard(klava);
                return replyKeyboardMarkup;
            case "/yes/no":
                keyboardRow1.clear();
                keyboardButton1.setText("да");
                keyboardButton2.setText("нет");
                keyboardRow1.add(keyboardButton1);
                keyboardRow1.add(keyboardButton2);
                return replyKeyboardMarkup;
            case "Создать макет":
                keyboardRow1.clear();
                keyboardRow1.add(keyboardButton2.setText("Осталось " + n + "/12"));
                return replyKeyboardMarkup;
            case "Оформить":
                keyboardRow1.clear();
                keyboardButton1.setText("О боте");
                keyboardButton2.setText("Создать StickerPack");
                keyboardRow1.add(keyboardButton1);
                keyboardRow1.add(keyboardButton2);
                return replyKeyboardMarkup;
            default:
                return replyKeyboardMarkup;

        }


    }
    //Convert webp to png
    public void DecodedWebP(Users user,int number) throws IOException {
        String inputWebpPath = user.getScreenName()+"sticker" + number + ".webp";

        String outputPngPath = user.getScreenName()+"sticker" + number + ".png";

        WebpIO.create().toNormalImage(inputWebpPath, outputPngPath);


    }

    //UserName
    @Override
    public String getBotUsername() {
        return "@StickersPackBot";
    }

    //Tocken
    @Override
    public String getBotToken() {
        return "874387005:AAF2YNgabQejDE5NArh3iw6elIdR62sDyZE";
    }
}