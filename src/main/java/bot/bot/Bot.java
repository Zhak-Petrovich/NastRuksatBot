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

import java.util.List;

import static bot.util.Util.isAdmin;

@Component
public class Bot extends TelegramLongPollingBot {
    public static final String START_MESSAGE = "Привет, я Наст Рукаст!";
    public static final String HELP_MESSAGE = """
            /what выведет все фотки с подписями
            /save чтобы сохранить новую запись надо быть админом""";
    public static final String ADMIN_ERROR = "Ты не админ";
    public static final String SAVE_MESSAGE = """
            Прикрепи фото, затем в описании:
            Введи название
            Введи описание
            Введи цену
            Введи количество
            Введи сроки
            Введи категорию""";
    @Value("${bot.name}")
    private String botName;

    @Value("${bot.token}")
    private String botToken;
    private final ProjectService service;
    private final SendMessage sendMessage = new SendMessage();
    private final SendPhoto sendPhoto = new SendPhoto();

    @Autowired
    public Bot(ProjectService service) {
        this.service = service;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Long chatId = update.getMessage().getChatId();
        sendMessage.setChatId(chatId);
        String messageText = update.getMessage().getText();
        if (messageText == null) messageText = "";
        switch (messageText) {
            case ("/start") -> sendMessage(START_MESSAGE, sendMessage);
            case ("/help") -> sendMessage(HELP_MESSAGE, sendMessage);
            case ("/what") -> {
                sendPhoto.setChatId(chatId);
                List<Project> resultList = service.getAll();
                for (Project p : resultList) {
                    sendPhoto.setCaption(p.toString());
                    sendPhoto(p.getPhotoId(), sendPhoto);
                }

            }
            case ("/save") -> sendMessage(SAVE_MESSAGE, sendMessage);

        }
        if (update.getMessage().hasPhoto()) {
            sendMessage(saveNewProject(update), sendMessage);
        }
    }


    @Override
    public void clearWebhook() {
    }

    @Override
    public String getBotToken() {
        return this.botToken;
    }

    @Override
    public String getBotUsername() {
        return this.botName;
    }

    private void sendMessage(String text, SendMessage sendMessage) {
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendPhoto(String photoId, SendPhoto sendPhoto) {
        sendPhoto.setPhoto(new InputFile(photoId));
        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private String saveNewProject(Update update) {
        long user = update.getMessage().getFrom().getId();
        if (!isAdmin(user)) {
            return ADMIN_ERROR;
        }
        if (update.getMessage().getCaption() != null) {
            Project project = new Project();
            String[] newProject = update.getMessage().getCaption().split("\n");
            if (newProject.length != 6) {
                return "Ошибка в описании";
            }
            project.setName(newProject[0]);
            project.setDescription(newProject[1]);
            project.setPrice(newProject[2]);
            project.setQuantity(newProject[3]);
            project.setDeadLine(newProject[4]);
            project.setCategory(newProject[5]);
            project.setPhotoId(update.getMessage().getPhoto().get(0).getFileId());
            service.saveProject(project);
            return "Success";
        }
        return "Something was wrong";
    }
}
