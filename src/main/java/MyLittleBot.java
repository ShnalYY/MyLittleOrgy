
//import jdk.jfr.Description;
import org.telegram.telegrambots.facilities.TelegramHttpClientBuilder;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.bots.DefaultBotOptions;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


import java.util.*;
import java.util.function.IntToLongFunction;
import java.util.logging.Level;

import static java.lang.Math.toIntExact;

public class MyLittleBot extends TelegramLongPollingBot {


    // Запис таблиць - переробити в БД або xml
    private HashMap<Long, ArrayList<PlayingDay>> mainMap = new HashMap<>();
    //ArrayList<PlayingDay> playingDays = new ArrayList<>();
    private int tableMessage = 0;
    private int commandMessage = 0;

    /**
     * Метод для приема сообщений.
     *
     * @param update Содержит сообщение от пользователя.
     */
    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage()) {
            //  mainMap.forEach((key, value)->{ System.out.println("Key1 : " + key + " Value : " + value);});
            String message = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();
            //sendMsg(update.getMessage().getChatId().toString(), message);


            // Якщо приходить команда на створення таблиці

            if (update.getMessage().getText().equals("/help")) {

                SendMessage sendMessage = new SendMessage();
                sendMessage.enableHtml(true);
                sendMessage.setChatId(update.getMessage().getChatId());
                sendMessage.setText("<b>В цей швидкий, буремний час хочеться інколи зупинитись, " +
                        "відволіктись від проблем на роботі, сварок в сім'ї і пивного животика, " +
                        "та влаштувати свою маленьку стару добру оргію. І в цьому вам допоможе мій бот)\n" +
                        "P.S. Також його можна використовувати для збору ігор DnD.</b>\n" +
                        "<i> /Let the orgy begins!  </i>");

                try {
                    execute(sendMessage).getMessageId();

                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }


            }


            if (update.getMessage().getText().equals("/lettheorgybegins@MyLittleOrgyBot") ||
                    update.getMessage().getText().equals("/ltob@MyLittleOrgyBot") ||
                    update.getMessage().getText().equals("/ltob") ||
                    update.getMessage().getText().equals("/lettheorgybegins")) {
                //System.out.println(update.getMessage().getMessageId());
                // Якщо чат не має таблиці, створюєм її
                if (!mainMap.containsKey(chat_id))
                    mainMap.putIfAbsent(chat_id, new ArrayList<>());

                // Запамятовуєм ID повідомлення команди. При надходженні повторної, стару затираєм
                if (commandMessage == 0)
                    commandMessage = update.getMessage().getMessageId();
                else {
                    DeleteMessage del = new DeleteMessage();
                    del.setChatId(update.getMessage().getChatId());
                    del.setMessageId(commandMessage);
                    try {
                        execute(del);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    commandMessage = update.getMessage().getMessageId();
                }

                // Провіряєм чи є в чаті активна таблиця, якщо ні - створюєм
                if (tableMessage != 0) {

                    DeleteMessage del = new DeleteMessage();
                    del.setChatId(update.getMessage().getChatId());
                    del.setMessageId(tableMessage);
                    try {
                        execute(del);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }

                    SendMessage sendMessage = new SendMessage();
                    sendMessage.enableMarkdown(true);
                    sendMessage.setChatId(update.getMessage().getChatId());
                    sendMessage.setText("Ну? Коли влаштовуєм нашу маленьку оргію?");
                    sendMessage.setReplyMarkup(setInlineButtons(mainMap.get(chat_id)));


                    try {
                        tableMessage = execute(sendMessage).getMessageId();

                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }

                } else {

                    commandMessage = update.getMessage().getMessageId();
                    ///////////////////////////////////////////////////////////////////
                    //Обробка параметрів
                    //////////////////////////////////////////////////////////////////
                    ArrayList<PlayingDay> playingDays = PlayingDay.getPlayingDays("default");
                    mainMap.replace(chat_id, playingDays);

                    SendMessage sendMessage = new SendMessage();
                    sendMessage.enableMarkdown(true);
                    sendMessage.setChatId(update.getMessage().getChatId());
                    sendMessage.setText("Коли влаштовуєм нашу маленьку оргію?");
                    //sendMessage.setReplyMarkup(this.setButtons());
                    //setButtons(sendMessage);

                    sendMessage.setReplyMarkup(setInlineButtons(playingDays));

                    try {
                        tableMessage = execute(sendMessage).getMessageId();
                    } catch (TelegramApiException e) {
                        //  log.log(Level.SEVERE, "Exception: ", e.toString());
                    }
                }
            }


            if (update.getMessage().getText().contains("/vsyo@MyLittleOrgyBot")) {

                if (mainMap.containsKey(chat_id)) {
                    //  System.out.println( chat_id +" "+ mainMap.containsKey(chat_id) );
                    mainMap.remove(chat_id);
                    SendMessage sendMessage = new SendMessage();
                    //sendMessage.enableMarkdown(true);
                    sendMessage.setChatId(update.getMessage().getChatId());
                    sendMessage.enableHtml(true);
                    sendMessage.setText("<i>Пора в скушний реальний світ</i>");


                    //sendMessage.setText("*Deleted*");
                    //sendMessage.enableMarkdown(true);
                    try {
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        //  log.log(Level.SEVERE, "Exception: ", e.toString());
                        e.printStackTrace();
                    }

                    DeleteMessage del = new DeleteMessage();
                    del.setChatId(update.getMessage().getChatId());
                    del.setMessageId(tableMessage);
                    try {
                        execute(del);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }

                    del.setMessageId(commandMessage);
                    try {
                        execute(del);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }

                    tableMessage = 0;
                    commandMessage = 0;
                } else {

                    SendMessage sendMessage = new SendMessage();
                    //sendMessage.enableMarkdown(true);
                    sendMessage.setChatId(chat_id);
                    sendMessage.enableHtml(true);
                    sendMessage.setText("<i> Та і не було нічого</i>");


                    //sendMessage.setText("*Deleted*");
                    //sendMessage.enableMarkdown(true);
                    try {
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        //  log.log(Level.SEVERE, "Exception: ", e.toString());
                        e.printStackTrace();
                    }


                }

            }
        } else if (update.hasCallbackQuery()) {
            // update.getCallbackQuery().
            //answerCallbackQuery(update.getCallbackQuery().getId(), "OK");
            //if ( update.getCallbackQuery().getData().equals("update_msg_text"))

            ///   System.out.println(update.getCallbackQuery().getData());
            System.out.println(
                    "getId() " + update.getCallbackQuery().getFrom().getId() + "\n" +
                            "getChatId() " + update.getCallbackQuery().getMessage().getChatId() + "\n" +
                            "getMessageId() " + update.getCallbackQuery().getMessage().getMessageId() + "\n" +
                            "getText() " + update.getCallbackQuery().getMessage().getText() + "\n" +
                            "getData() " + update.getCallbackQuery().getData() + "\n" +
                            "getInlineMessageId() " + update.getCallbackQuery().getInlineMessageId());
            //update.getCallbackQuery().getMessage().getCaption();


            String call_data = update.getCallbackQuery().getData();
            long message_id = update.getCallbackQuery().getMessage().getMessageId();
            long chat_id = update.getCallbackQuery().getMessage().getChatId();
            long user_id = update.getCallbackQuery().getFrom().getId();
            String inline_message_id = update.getCallbackQuery().getInlineMessageId();
            String[] getCallData = call_data.split("/");
            //System.out.println(getCallData[0]+"_"+getCallData[1] );

            EditMessageReplyMarkup new_message;
            new_message = new EditMessageReplyMarkup()
                    .setChatId(chat_id).setMessageId(toIntExact(message_id))
                    .setInlineMessageId(inline_message_id);

            /*SendMessage new_message = new SendMessage();
            new_message.enableMarkdown(true);
            new_message.setChatId(chat_id);
            new_message.setText("updated");*/

            if (mainMap.containsKey(chat_id))
                for (PlayingDay day : mainMap.get(chat_id)
                        ) {
                    System.out.println("day.getName()" + day.getName());
                    if (getCallData[0].equals(day.getName())) {
                        //System.out.println(getCallData[1]);
                        switch (getCallData[1]) {

                            case "OnDay": {

                                if (!day.listOfUsersOnMorning.contains(user_id) &&
                                        !day.listOfUsersOnAfternoon.contains(user_id) &&
                                        !day.listOfUsersOnEvening.contains(user_id)) {
                                    day.listOfUsersOnMorning.add(user_id);
                                    day.listOfUsersOnAfternoon.add(user_id);
                                    day.listOfUsersOnEvening.add(user_id);
                                } else {
                                    day.listOfUsersOnMorning.remove(user_id);
                                    day.listOfUsersOnAfternoon.remove(user_id);
                                    day.listOfUsersOnEvening.remove(user_id);

                                }
                                day.setOnMorning(Integer.toString(day.listOfUsersOnMorning.size()));
                                day.setOnAfternoon(Integer.toString(day.listOfUsersOnAfternoon.size()));
                                day.setOnEvening(Integer.toString(day.listOfUsersOnEvening.size()));
                                new_message.setReplyMarkup(setInlineButtons(mainMap.get(chat_id)));
                                //setInlineButtons(new_message,mainMap.get(chat_id));

                                try {
                                    //editMessageReplyMarkup(new_message);
                                    execute(new_message);
                                } catch (TelegramApiException e) {
                                    e.printStackTrace();
                                }


                                break;
                            }


                            case "OnMorning": {

                                if (!day.listOfUsersOnMorning.contains(user_id))
                                    day.listOfUsersOnMorning.add(user_id);

                                else
                                    day.listOfUsersOnMorning.remove(user_id);

                                day.setOnMorning(Integer.toString(day.listOfUsersOnMorning.size()));
                                new_message.setReplyMarkup(setInlineButtons(mainMap.get(chat_id)));
                                try {
                                    //editMessageReplyMarkup(new_message);
                                    execute(new_message);
                                } catch (TelegramApiException e) {
                                    e.printStackTrace();
                                }


                                break;
                            }

                            case "OnAfternoon": {

                                if (!day.listOfUsersOnAfternoon.contains(user_id))

                                    day.listOfUsersOnAfternoon.add(user_id);
                                else
                                    day.listOfUsersOnAfternoon.remove(user_id);


                                day.setOnAfternoon(Integer.toString(day.listOfUsersOnAfternoon.size()));
                                new_message.setReplyMarkup(setInlineButtons(mainMap.get(chat_id)));
                                try {
                                    //editMessageReplyMarkup(new_message);
                                    execute(new_message);
                                } catch (TelegramApiException e) {
                                    e.printStackTrace();
                                }


                                break;
                            }
                            case "OnEvening": {

                                if (!day.listOfUsersOnEvening.contains(user_id))
                                    day.listOfUsersOnEvening.add(user_id);

                                else
                                    day.listOfUsersOnEvening.remove(user_id);

                                day.setOnEvening(Integer.toString(day.listOfUsersOnEvening.size()));
                                new_message.setReplyMarkup(setInlineButtons(mainMap.get(chat_id)));
                                try {
                                    //editMessageReplyMarkup(new_message);
                                    execute(new_message);
                                } catch (TelegramApiException e) {
                                    e.printStackTrace();
                                }


                                break;
                            }
                            default:
                                System.out.println("Error in case");
                        }
                    }


                }
            else {

                SendMessage sendMessage = new SendMessage();

                sendMessage.setChatId(chat_id);
                sendMessage.enableHtml(true);
                sendMessage.setText("<i>То є стара оргія)</i>");


                //sendMessage.setText("*Deleted*");
                //sendMessage.enableMarkdown(true);
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    //  log.log(Level.SEVERE, "Exception: ", e.toString());
                    e.printStackTrace();
                }


                DeleteMessage del = new DeleteMessage();
                del.setChatId(chat_id);
                del.setMessageId(((int) message_id));

                try {
                    execute(del);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

            }

           /* if (call_data.equals("1update_msg_text")) {
                String answer = "Updated message text";
                EditMessageReplyMarkup new_message;
                new_message = new EditMessageReplyMarkup()
                        .setChatId(chat_id).setMessageId(toIntExact(message_id))
                        .setInlineMessageId(inline_message_id);

                InlineKeyboardButton dk1 = new InlineKeyboardButton();
                dk1.setText("label1");
                dk1.setCallbackData("change_the_label");
                InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                List<InlineKeyboardButton> rowInline = new ArrayList<>();

                rowInline.add(dk1);

                rowsInline.add(rowInline);

                markupInline.setKeyboard(rowsInline);
                new_message.setReplyMarkup(markupInline);
                //setInlineButtons(new_message,)
                try {
                    //editMessageReplyMarkup(new_message);
                    execute(new_message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

            }*/

        }

    }


    /**
     * Метод возвращает имя бота, указанное при регистрации.
     *
     * @return имя бота
     */
    //@Override
    public String getBotUsername() {
        return "MyLittleOrgyBot";
    }

    /**
     * Метод возвращает token бота для связи с сервером Telegram
     *
     * @return token для бота
     */
    @Override
    public String getBotToken() {
        return "584877888:AAEXXpmMxQb3pRCeSmYkUPco1R5eOZ9oESo";
    }


    //@Description("Кнопки  в вікні")
    public synchronized ReplyKeyboardMarkup setButtons() {
        // Создаем клавиуатуру
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        // sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(false);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        // Создаем список строк клавиатуры
        List<KeyboardRow> keyboard = new ArrayList<>();

        // Первая строчка клавиатуры
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        // Добавляем кнопки в первую строчку клавиатуры
        keyboardFirstRow.add(new KeyboardButton("Конєшно Ліля!"));

        // Вторая строчка клавиатуры
        KeyboardRow keyboardSecondRow = new KeyboardRow();
        // Добавляем кнопки во вторую строчку клавиатуры
        keyboardSecondRow.add(new KeyboardButton("А шо такоє?"));

        KeyboardRow keyboardThirdRow = new KeyboardRow();
        // Добавляем кнопки во вторую строчку клавиатуры
        keyboardThirdRow.add(new KeyboardButton("Якісь перетензії?"));

        // Добавляем все строчки клавиатуры в список
        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);
        keyboard.add(keyboardThirdRow);
        // и устанваливаем этот список нашей клавиатуре
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }


    private synchronized InlineKeyboardMarkup setInlineButtons(ArrayList<PlayingDay> listOfDays) {

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        //Map buttons =new  HashMap <String,  List<InlineKeyboardButton>>();
        List<InlineKeyboardButton> rowOfDaysName = new ArrayList<>();
        List<InlineKeyboardButton> rowOnMorning = new ArrayList<>();
        List<InlineKeyboardButton> rowOnAfternoon = new ArrayList<>();
        List<InlineKeyboardButton> rowOnEvening = new ArrayList<>();

        for (PlayingDay day : listOfDays
                ) {
            rowOfDaysName.add(new InlineKeyboardButton().setText(day.getName())
                    .setCallbackData(day.getName() + "/" + "OnDay"));
            rowOnMorning.add(new InlineKeyboardButton().setText(day.getOnMorning())
                    .setCallbackData(day.getName() + "/" + "OnMorning"));
            rowOnAfternoon.add(new InlineKeyboardButton().setText(day.getOnAfternoon())
                    .setCallbackData(day.getName() + "/" + "OnAfternoon"));
            rowOnEvening.add(new InlineKeyboardButton().setText(day.getOnEvening())
                    .setCallbackData(day.getName() + "/" + "OnEvening"));


            /* buttons.put(day.getName(), new ArrayList<InlineKeyboardButton>());
            ((List<InlineKeyboardButton>) buttons.get(day.getName()))
                    .add((new InlineKeyboardButton().setText(day.getName()).setCallbackData(day.getName())));*/
            // Set the keyboard to the markup
        }
        rowsInline.add(rowOfDaysName);
        rowsInline.add(rowOnMorning);
        rowsInline.add(rowOnAfternoon);
        rowsInline.add(rowOnEvening);

        // Add it to the message
        markupInline.setKeyboard(rowsInline);


        return markupInline.setKeyboard(rowsInline);
        //sendMessage.setReplyMarkup(markupInline);

    }

    // @Description("Вспливаюче вікно у відповідь на вибір")
    public synchronized void answerCallbackQuery(String callbackId, String message) {
        AnswerCallbackQuery answer = new AnswerCallbackQuery();
        answer.setCallbackQueryId(callbackId);
        answer.setText(message);
        answer.setShowAlert(true);
        try {
            execute(answer);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
