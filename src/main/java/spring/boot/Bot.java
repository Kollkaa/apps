package spring.boot;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import io.github.biezhi.webp.WebpIO;
import org.springframework.boot.SpringApplication;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
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
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

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

String art="";

    int count_zakazov;
    int positive_=0;
    int negative_=0;
    String val="..";


    //799964941

    TreeMap<Long,Users> user=new TreeMap<>();


    static long chatid =799964941;

    static String info_for_start="Привіт! \n" +
            "Я надрукую та доставлю твої улюблені стікери з Telegram!\n" +
            "\n" +
            "\uD83D\uDC4C 12 стікерів на аркуші А5\n" +
            "\uD83D\uDCB3 Вартість набору - 75 грн.\n" +
            "\uD83D\uDCE6 Доставка по всій Україні, зручним тобі способом (оплачується окремо)\n" +
            "\n" +
            "\n" +"Натисни Створити StickerPack, щоб розпочати!";



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
                            System.out.println("start");
                            execute( (new SendPhoto().setChatId(usere.getChatid()).setPhoto(new File("src/main/resources/start.jpg")).setCaption(info_for_start))
                                    .setReplyMarkup(remakeButtons(usere,message.getText(), replyKeyboardMarkup, usere.getStickers().size())));
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                        usere.Rebuild();
                        break;
                    case "Про бота":
                        System.out.println("about bot");
                        try {
                            sendApiMethod(sendMessage.setText("\n" +
                                    "За допомогою даного бота - стало можливим створити StickerPack з твоїх улюблених наборів стікерів!\n" +
                                    "Все просто, для початку роботи з ботом - надішли будь-який стікер.\n" +
                                    "\n Інстаграм https://instagram.com/stickerjoker?igshid=rhg24p742oag" +
                                    "\n З усіх питань щодо роботи даного бота, писати сюди - @stickers_kiev").setReplyMarkup(remakeButtons(usere,message.getText(), replyKeyboardMarkup, usere.getStickers().size())));
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                        break;
                        case "Створити StickerPack":

                        System.out.println("about sdelat maket");
                        try {
                            usere.setStickers( new ArrayList<Sticker>());
                            usere.setZakaz();
                            sendApiMethod(sendMessage.setText("Відправте перший стікер").setReplyMarkup(remakeButtons(usere,"hide", replyKeyboardMarkup, usere.getStickers().size())));
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "/alex":
                        Message mes1=update.getMessage();
                        SendMessage message1=new SendMessage();
                        message1.setText("Загальна кількість замовлень : "+ String.valueOf(count_zakazov)+"\n"+"Позитивні відгуки: "+positive_+"\n"+"Негативні відгуки: "+negative_+"\n"+"Не залишили відгук: "+(count_zakazov-positive_-negative_));
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
                    case "Інструкція по використанню":
                        try {
                            execute(new SendMessage().setText("1.Для створення StickerPack необхідно натиснути:\n" +
                                    "Створити StickerPack\n" +
                                    "===============================================\n" +
                                    "2.Заповнюєте StickerPack необхідною кількістю \n" +
                                    "стикерів(Максимально-12)\n" +
                                    "===============================================\n" +
                                    "3.Оформляєте замовлення, для цього вводите особисті дані,\n" +
                                    "після цього ви отримаєте повідомлення про отримання замовлення\n" +
                                    "=============ДОДАТКОВА ІНФОРМАЦІЯ==============\n" +
                                    "1.Для попереднього перегляду StickerPack  натисніть-Превью\n" +
                                    "2.Для створення ще одного StickerPack закінчіть офромлення попереднього\n" +
                                    "та повторіть ваші дії заново.\n" +
                                    "===============================================\n").setChatId(usere.getChatid()));
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                        break;

                    default:


                        try {
                            if (message.getText()!=null) {
                                sendMessage.setChatId(chatid);
                                sendMessage.setText("Данні користувача :"+usere.getName()+"(@"+update.getMessage().getChat().getUserName()+")"+", по замовленню №:" +usere.getChatid() +"\n"+message.getText());
                                execute(sendMessage);
                                execute((sendInlineKeyBoardMessage(usere.getChatid(),3)
                                        .setText("Виберіть спосіб оплати")));
                                user.get(usere.getChatid()).setZakaz();
                            } else {
                                sendMessage.setText("Введіть запит /start ще раз и повторіть створення StickerPack");
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
                        System.out.println("!111!!");
                    }
                    try {

                        DecodedWebP(usere,usere.getStickers().size());

                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("!!213!");
                    }


                    try {
                        sendApiMethod(sendInlineKeyBoardMessage(usere.getChatid(),1).setText("Вільних місць на листку:  "
                                + (12 - usere.getStickers().size()) ));
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                        System.out.println("4312412");
                    }
                }
                if (usere.getStickers().size() >= 12) {

                    try {
                        usere.AddPhotoToTemplate();
                        SendPhoto send =usere.getPreview();
                        SendMessage sendMessage = new SendMessage();
                        sendMessage.setChatId(usere.getChatid());
//                       sendApiMethod(sendInlineKeyBoardMessage(usere.getChatid(),2).setText("Теперь давайте оформим заказ"));
                        sendApiMethod(sendMessage.setText("1.Для офрмлення замовлення - \"Оформити\".\n" +
                                "2.Для перегляду StickerPack - \"Прев'ю\".\n" +
                                "3.Для створення нового StickerPack - \"Почати спочатку\"."));

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
            val=update.getCallbackQuery().getMessage().getChat().getUserName();
            switch (update.getCallbackQuery().getData())
            {

                case "preview":



                    try {
                        usere.AddPhotoToTemplate();

                        execute(usere.getPreview().setChatId(usere.getChatid()));
                    } catch (TelegramApiException e) {

                        e.printStackTrace();
                    } catch (IOException e) {

                        e.printStackTrace();
                    }


                    break;
                case "enter":
                    count_zakazov+=1;
                 try {

                    sendApiMethod(new SendMessage().setText("Ім'я Користувача : "+usere.getName()+"(@"+update.getCallbackQuery().getMessage().getChat().getUserName()+")"+"\n"+"Замовлення №"+usere.getChatid()).setChatId(chatid));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

                    usere.setZakaz(usere.getZakaz()+1);

                    System.out.println("about enter");

                    SendMessage sendMessage =new SendMessage();
                    SendMessage infosend=new SendMessage();
                    sendMessage.setChatId(usere.getChatid());
                    sendMessage.setText("Для оформлення замовлення вкажіть:\n" +
                            "1) ПІБ\n" +
                            "2) Місто доставки\n" +
                            "3) Номер телефону‼️(Інакше ми не зможемо зв’язатися з вами)");
//                    infosend.setChatId(usere.getChatid());
//                    infosend.setText("Для створення ще одного макету-нажміть Оформити.\n" +
//                            "Вкажіть власні данні, та почніть створювати спочатку.\n");
                    try {
                        execute(sendMessage);
//                        execute(infosend);

                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }

                    try {

                        System.out.println("worker photo");
                        usere.AddPhotoToTemplate();
                        System.out.println("maket ready");
                        execute(new SendDocument().setDocument(new File(usere.getScreenName()+"stickerpack.png")).setChatId(chatid));
                        System.out.println("this all");
                        execute(new SendMessage().setText("Макет під замовлення користувача:"+usere.getName()+"(@"+val+")").setChatId(chatid));
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
                        execute(new SendMessage().setChatId(usere.getChatid()).setText("Нажміть /start для повторого створення набору!"));
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    break;

                case "naloz":
                    SendMessage sendMessa=new SendMessage();
                    sendMessa.setChatId(chatid);
                    sendMessa.setText("Спосіб оплати по замовленню №:" + usere.getChatid()+"("+update.getCallbackQuery().getMessage().getChat().getUserName()+")"+"\n"+"Вид оплати: При отриманні");
                    try {
                        execute(sendMessa);
                        execute(sendInlineKeyBoardMessage(usere.getChatid(),4).setText("Вам сподобався наш серіс \uD83D\uDE80?"));
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    usere.Rebuild();


                    break;
                case "na_carty":
                    SendMessage sendMessa1=new SendMessage();
                    sendMessa1.setChatId(chatid);
                    sendMessa1.setText("Спосіб оплати по замовленню №::" + usere.getChatid()+"\n"+"Вид оплати: На карту");

                    try {
                        execute(sendInlineKeyBoardMessage(usere.getChatid(),5).setText("Виберіть банк :"));
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }


                    break;
                case "privat":
                    SendMessage sendMessas1=new SendMessage();
                    sendMessas1.setChatId(chatid);
                    sendMessas1.setText("Спосіб оплати на карту ПриватБанка");
                    try {
                        execute(sendMessas1);
                    }catch (Exception e)
                    {
                        System.out.println(e.getStackTrace());
                    }

                    sendMessas1.setChatId(usere.getChatid());
                    sendMessas1.setText("Номер карти ПриватБанк: \n 5169360005626969\n"+"https://privatbank.ua/ru/sendmoney");

                    try {
                        execute(sendMessas1);
                        execute(sendInlineKeyBoardMessage(usere.getChatid(),4).setText("Вам сподобався наш сервіс?"));
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }



                    usere.Rebuild();
                    break;
                case "mono":
                    SendMessage sendMessas2=new SendMessage();
                    sendMessas2.setChatId(chatid);
                    sendMessas2.setText("Спосіб оплати на карту МоноБанка");
                    try {
                        execute(sendMessas2);
                    }catch (Exception e)
                    {
                        System.out.println(e.getStackTrace());
                    }

                    sendMessas2.setChatId(usere.getChatid());
                    sendMessas2.setText("Номер карти Моно Банк: \n 5375414103174180\n");

                    try {
                        execute(sendMessas2);
                        execute(sendInlineKeyBoardMessage(usere.getChatid(),4).setText("Вам сподобався наш сервіс?"));
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    usere.Rebuild();
                    break;
                case "yes":
                    try {
                        sendApiMethod(new SendMessage(usere.getChatid(),"Дякуємо за відгук!\n" +
                                "Ми зв'яжемося з тобою найближчим часом. Для створення ще одного набору - натисни /start"));
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    positive_+=1;

                    try {
                        execute(new SendMessage().setChatId(chatid).setText(usere.getName()+"("+update.getCallbackQuery().getMessage().getChat().getUserName()+")"+": "+usere.getChatid()+" Цьому користувачу сподобався сервіс "+chatid ));
                        execute( (new SendPhoto().setChatId(usere.getChatid()).setPhoto(new File("src/main/resources/start.jpg")).setCaption(info_for_start))
                                .setReplyMarkup(remakeButtons(usere,"/start", replyKeyboardMarkup, usere.getStickers().size())));
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    break;
                case"no":
                    try {
                        sendApiMethod(new SendMessage(usere.getChatid(),"Дякуємо за відгук!\n" +
                                "Ми зв'яжемося з тобою найближчим часом. Для створення ще одного набору - натисни /start"));
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    negative_+=1;
                    try {
                        execute(new SendMessage().setChatId(chatid).setText(" Этому пользователю не понравился сервис "+chatid));
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

    //Rebuild all pictures to new

    //Remake inlineButtons
    public static SendMessage sendInlineKeyBoardMessage(long chatId,int n) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        if(n==1) {

            InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
            InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
            InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();

            inlineKeyboardButton1.setText("Прев'ю \uD83D\uDDBC");
            inlineKeyboardButton1.setCallbackData("preview");
            inlineKeyboardButton2.setText("Оформити ✅");
            inlineKeyboardButton2.setCallbackData("enter");
            inlineKeyboardButton3.setText("Почати спочатку ↩️");
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
            inlineKeyboardButton1.setText("Оформити ✅");
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
            inlineKeyboardButton1.setText("При отриманні \uD83D\uDECD");
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
            inlineKeyboardButton1.setText("Так\uD83D\uDC4D");
            inlineKeyboardButton1.setCallbackData("yes");
            inlineKeyboardButton2.setText("Ні\uD83D\uDC4E");
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
            inlineKeyboardButton1.setText("ПриватБанк");
            inlineKeyboardButton1.setCallbackData("privat");
            inlineKeyboardButton2.setText("МоноБанк");
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
        KeyboardButton keyboardButton3 = new KeyboardButton();
        keyboardButton1.setText("Про бота");
        keyboardButton2.setText("Створити StickerPack");
        keyboardButton3.setText("Інструкція по використанню");
        keyboardRow1.add(keyboardButton1);
        keyboardRow1.add(keyboardButton2);
        keyboardRow1.add(keyboardButton3);
        List<KeyboardRow> klava = new ArrayList<KeyboardRow>();
        klava.add(keyboardRow1);
        replyKeyboardMarkup.setKeyboard(klava);
        switch (s) {
            case"Інструкція по використанню":
                return replyKeyboardMarkup;
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
            case "Про бота":
                keyboardRow1.clear();
                keyboardRow1.add(keyboardButton1.setText("Створити StickerPack"));
                return replyKeyboardMarkup;
            case "/all":
                keyboardRow1.clear();
                klava.add(keyboardRow1);
                replyKeyboardMarkup.setKeyboard(klava);
                return replyKeyboardMarkup;
            case "/yes/no":
                keyboardRow1.clear();
                keyboardButton1.setText("Так");
                keyboardButton2.setText("Ні");
                keyboardRow1.add(keyboardButton1);
                keyboardRow1.add(keyboardButton2);
                return replyKeyboardMarkup;
            case "Создать макет":
                keyboardRow1.clear();
                keyboardRow1.add(keyboardButton2.setText("Осталось " + n + "/12"));
                return replyKeyboardMarkup;
            case "Оформити":
                keyboardRow1.clear();
                keyboardButton1.setText("Про бота");
                keyboardButton2.setText("Створити StickerPack");
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