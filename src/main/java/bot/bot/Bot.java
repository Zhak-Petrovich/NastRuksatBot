package bot.bot;

import bot.keyboards.Keyboard;
import bot.service.CategoryService;
import bot.service.ProjectService;
import bot.model.Project;
import bot.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
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
    public static final String ORDER_MESSAGE = "Спасибо за проявленный интерес, с вами скоро свяжутся))";
    @Value("${bot.name}")
    private String botName;

    @Value("${bot.token}")
    private String botToken;
    private final ProjectService projectService;
    private final CategoryService categoryService;
    private final SendMessage sendMessage = new SendMessage();
    private final SendPhoto sendPhoto = new SendPhoto();

    @Autowired
    public Bot(ProjectService projectService, CategoryService categoryService) {
        this.projectService = projectService;
        this.categoryService = categoryService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        InlineKeyboardMarkup keyboardMarkup;
        if (update.hasMessage()) {
            Long chatId = update.getMessage().getChatId();
            sendMessage.setChatId(chatId);
            if (update.getMessage().hasText()) {
                String messageText = update.getMessage().getText();
                switch (messageText) {
                    case ("/start") -> {
                        keyboardMarkup = Keyboard.startKeyboard();
                        sendMessage.setReplyMarkup(keyboardMarkup);
                        sendMessage(START_MESSAGE, sendMessage);
                    }
                    case ("/help") -> sendMessage(HELP_MESSAGE, sendMessage);
                    case ("/save") -> sendMessage(SAVE_MESSAGE, sendMessage);
                    case ("/order") -> {
                        Long chat = update.getMessage().getChatId();
                        sendMessage.setChatId(460498710L);
                        sendMessage.setReplyMarkup(null);
                        sendMessage("@" + update.getMessage().getFrom().getUserName() + " хочет что-то заказать", sendMessage);
                        sendMessage.setChatId(chat);
                        sendMessage(ORDER_MESSAGE, sendMessage);
                    }
                }
            }
            if (update.getMessage().hasPhoto()) {
                sendMessage(saveNewProject(update), sendMessage);
            }
        } else if (update.hasCallbackQuery()) {
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            sendPhoto.setChatId(chatId);
            sendMessage.setChatId(chatId);
            switch (update.getCallbackQuery().getData()) {
                case ("/start") -> {
                    keyboardMarkup = Keyboard.startKeyboard();
                    sendMessage.setReplyMarkup(keyboardMarkup);
                    sendMessage(START_MESSAGE, sendMessage);
                }
                case ("/category") -> {
                    keyboardMarkup = Keyboard.categoriesKeyboard(categoryService.getCategoryById(1));
                    sendMessage.setReplyMarkup(keyboardMarkup);
                    sendMessage("Выберите категорию:", sendMessage);
                }
                case ("/about") -> sendMessage("blah-blah-blah", sendMessage);
                case ("/callme") -> sendMessage("call me!", sendMessage);
                case ("/boards") -> sendFilteredPhoto(projectService.getAll(), "доски");
                case ("/stairs") -> sendFilteredPhoto(projectService.getAll(), "лестницы");

                case ("/furniture") -> sendFilteredPhoto(projectService.getAll(), "мебель");
                case ("/phones") -> sendFilteredPhoto(projectService.getAll(), "полки");
                case ("/other") -> sendFilteredPhoto(projectService.getAll(), "другое");
                case ("/season") ->
                        sendFilteredPhoto(projectService.getAll(), categoryService.getCategoryById(1).getName());
                case ("/order") -> {
                    Long chat = update.getCallbackQuery().getMessage().getChatId();
                    sendMessage.setChatId(408906445L);
                    sendMessage.setReplyMarkup(null);
                    sendMessage("@" + update.getCallbackQuery().getFrom().getUserName() + " хочет что-то заказать", sendMessage);
                    sendMessage.setChatId(chat);
                    sendMessage(ORDER_MESSAGE, sendMessage);
                }
            }
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

    public void sendMessage(String text, SendMessage sendMessage) {
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendPhoto(String photoId, SendPhoto sendPhoto) {
        sendPhoto.setPhoto(new InputFile(photoId));
        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public String saveNewProject(Update update) {
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
            projectService.saveProject(project);
            return "Запись \"" + newProject[0] + "\" успешно сохранена!";
        }
        return "Нужно добавить описание";
    }

    private void sendFilteredPhoto(List<Project> projects, String filter) {
        List<Project> resultList = Util.getFilteredProjects(projects, filter);
        for (Project p : resultList) {
            sendPhoto.setCaption(p.toString());
            sendPhoto(p.getPhotoId(), sendPhoto);
        }
        sendMessage.setReplyMarkup(Keyboard.categoriesKeyboard(categoryService.getCategoryById(1)));
        sendMessage("Выберите категорию:", sendMessage);

    }
}
