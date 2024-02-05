package bot.bot;

import bot.service.ProjectService;
import bot.model.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class Bot extends TelegramLongPollingBot {
    @Value("${bot.name}")
    private String botName;

    @Value("${bot.token}")
    private String botToken;
    private final ProjectService service;
    Map<Long, Integer> userUsage = new HashMap<>();
    private Integer counter;

    private Project project;

    @Autowired
    public Bot(ProjectService service) {
        this.service = service;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText();
        if (messageText == null) messageText = "";

        SendMessage sendMessage = new SendMessage();
        SendPhoto sendPhoto = new SendPhoto();
        sendMessage.setChatId(chatId);


        switch (messageText) {
            case ("/start") -> {
                sendMessage.setText("Привет, я Наст Рукаст");
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
            case ("/help") -> {
                sendMessage.setText("/what выведет все фотки с подписями\n" +
                        "/save предложит поэтапно сохранить");
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
            case ("/what") -> {
                sendMessage.setChatId(chatId);
                sendPhoto.setChatId(chatId);
                List<Project> resultList = service.getAll();
                try {
                    for (Project p : resultList) {
                        sendPhoto.setPhoto(new InputFile(p.getPhotoId()));
                        execute(sendPhoto);
                        String result = "Название: " + p.getName() + "\n" +
                               "Описание: " + p.getDescription() + "\n" +
                               "В наличии: " + p.getQuantity() + "\n" +
                               "Срок изготовления: " + p.getDeadLine() + "\n";
                        sendMessage.setText(result);
                        execute(sendMessage);
                    }
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
            case ("/save") -> {
                Long user = update.getMessage().getFrom().getId();
                System.out.println(user);
                if ((user != 460498710L) && (user != 408906445L) && (user != 537308122)) {
                    sendMessage.setText("Ты не админ");
                    try {
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                }
                project = new Project();
                sendMessage.setText("Напиши название");
                userUsage.put(chatId, 1);
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }

            }
            default -> {
                if (userUsage.get(chatId) > 0 && userUsage.get(chatId) < 6) {
                    switch (userUsage.get(chatId)) {
                        case (1) -> {
                            project.setName(update.getMessage().getText());
                            sendMessage.setText("Напиши описание");
                            userUsage.put(chatId, userUsage.get(chatId) + 1);
                            try {
                                execute(sendMessage);
                            } catch (TelegramApiException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        case (2) -> {
                            project.setDescription(update.getMessage().getText());
                            sendMessage.setText("Какие сроки?");
                            userUsage.put(chatId, userUsage.get(chatId) + 1);
                            try {
                                execute(sendMessage);
                            } catch (TelegramApiException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        case (3) -> {
                            project.setDeadLine(update.getMessage().getText());
                            sendMessage.setText("Кол-во в наличии?");
                            userUsage.put(chatId, userUsage.get(chatId) + 1);
                            try {
                                execute(sendMessage);
                            } catch (TelegramApiException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        case (4) -> {
                            project.setQuantity(update.getMessage().getText());
                            sendMessage.setText("Прикрепи фото");
                            userUsage.put(chatId, userUsage.get(chatId) + 1);
                            try {
                                execute(sendMessage);
                            } catch (TelegramApiException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        case (5) -> {
                            if (update.getMessage().hasPhoto()) {
                                String photoId = update.getMessage().getPhoto().get(0).getFileId();
                                project.setPhotoId(photoId);
                                userUsage.put(chatId, 0);
                                sendMessage.setText("Готово!");
                                service.saveProject(project);
                                try {
                                    execute(sendMessage);
                                } catch (TelegramApiException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    }
                }
            }
        }


    }

    @Override
    public void clearWebhook() {

    }

    @Override
    public String getBotUsername() {
        return this.botName;
    }

    @Override
    public String getBotToken() {
        return this.botToken;
    }
}
