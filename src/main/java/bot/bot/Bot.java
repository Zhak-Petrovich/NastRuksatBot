package bot.bot;

import bot.service.ProjectService;
import bot.model.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component
public class Bot extends TelegramLongPollingBot {
    @Value("${bot.name}")
    private String botName;

    @Value("${bot.token}")
    private String botToken;
    private final ProjectService service;

    @Autowired
    public Bot(ProjectService service) {
        this.service = service;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText();
        SendMessage sendMessage = new SendMessage();
        switch (messageText) {
            case ("/start") -> {
                sendMessage.setChatId(chatId);
                sendMessage.setText("Привет, я Наст Рукаст");
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
            case ("/help") -> {
                sendMessage.setChatId(chatId);
                sendMessage.setText("Скоро все будет.");
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
                break;
            }
            case ("/what") -> {
                sendMessage.setChatId(chatId);
                List<Project> resultList = service.getAll();
                try {
                    for (Project p : resultList) {
                        String result = p.getName() + "\n";
                        result = result + p.getDescription() + "\n";
                        sendMessage.setText(result);
                        execute(sendMessage);
                    }
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
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
