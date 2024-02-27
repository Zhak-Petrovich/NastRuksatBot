package bot.bot;

import bot.keyboards.Keyboard;
import bot.service.SupService;
import bot.service.ProjectService;
import bot.model.Project;
import bot.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.UUID;

import static bot.util.Util.isAdmin;

@Component
public class Bot extends TelegramLongPollingBot {

    public static final String PART_OF_PATH = "/home/data/";
    //public static final String PART_OF_PATH = "E:\\data\\";
    public static final Long ADMIN_ID = 460498710L; //408906445 Nast, 537308122 Anton, 460498710 Me
    public static final String START_MESSAGE = "Привет, я Наст Рукаст!";
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
    private ProjectService projectService;
    private SupService supService;
    private final SendMessage sendMessage = new SendMessage();
    private final SendPhoto sendPhoto = new SendPhoto();
    private Boolean isIndividual = false;

    @Autowired
    public Bot(@Value("${bot.token}") String botToken) {
        super(botToken);
    }

    @Autowired
    public void setProjectService(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Autowired
    public void setSupService(SupService supService) {
        this.supService = supService;
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
                        isIndividual = false;
                        keyboardMarkup = Keyboard.startKeyboard();
                        sendMessage.setReplyMarkup(keyboardMarkup);
                        sendMessage(START_MESSAGE, sendMessage);
                    }
                    case ("/save") -> sendMessage(SAVE_MESSAGE, sendMessage);
                    case ("/order") -> {
                        Long chat = update.getMessage().getChatId();
                        sendMessage.setChatId(ADMIN_ID);
                        sendMessage.setReplyMarkup(null);
                        sendMessage("@" + update.getMessage().getFrom().getUserName() + " хочет что-то заказать", sendMessage);
                        sendMessage.setChatId(chat);
                        sendMessage(ORDER_MESSAGE, sendMessage);
                    }
                }
            }
            if (update.getMessage().hasPhoto()) {
                try {
                    sendMessage(saveNewProject(update), sendMessage);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
        } else if (update.hasCallbackQuery()) {
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            sendPhoto.setChatId(chatId);
            sendMessage.setChatId(chatId);
            switch (update.getCallbackQuery().getData()) {
                case ("/start") -> {
                    isIndividual = false;
                    keyboardMarkup = Keyboard.startKeyboard();
                    sendMessage.setReplyMarkup(keyboardMarkup);
                    sendMessage(START_MESSAGE, sendMessage);
                }
                case ("/category") -> {
                    isIndividual = false;
                    sendMessage.setReplyMarkup(Keyboard.categoriesKeyboard(supService.getSupportById(1)));
                    sendMessage("Выберите категорию:", sendMessage);
                }
                case ("/about") -> sendMessage(supService.getSupportById(2).getValue(), sendMessage);
                case ("/boards") -> sendFilteredPhoto(projectService.getAll(), "доски");
                case ("/stairs") -> sendFilteredPhoto(projectService.getAll(), "лестницы");

                case ("/furniture") -> sendFilteredPhoto(projectService.getAll(), "мебель");
                case ("/phones") -> sendFilteredPhoto(projectService.getAll(), "полки");
                case ("/other") -> sendFilteredPhoto(projectService.getAll(), "другое");
                case ("/season") ->
                        sendFilteredPhoto(projectService.getAll(), supService.getSupportById(1).getValue());
                case ("/individual") -> {
                    isIndividual = true;
                    sendMessage.setReplyMarkup(Keyboard.categoriesKeyboard(supService.getSupportById(1)));
                    sendMessage("Выберите категорию:", sendMessage);
                }
                case ("/order") -> {
                    Long chat = update.getCallbackQuery().getMessage().getChatId();
                    sendMessage.setChatId(ADMIN_ID);
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

    public void sendPhotoByPath(String photoPath, SendPhoto sendPhoto) {
        sendPhoto.setPhoto(new InputFile(new java.io.File(photoPath)));
        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public String saveNewProject(Update update) throws TelegramApiException {
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
            String fileName = UUID.randomUUID() + ".jpg";
            String savePath = PART_OF_PATH + fileName;
            project.setName(newProject[0]);
            project.setDescription(newProject[1]);
            project.setPrice(newProject[2]);
            project.setQuantity(newProject[3]);
            project.setDeadLine(newProject[4]);
            project.setCategory(newProject[5]);
            project.setFileName(fileName);
            getFile(update, savePath);
            projectService.saveProject(project);
            return "Запись \"" + newProject[0] + "\" успешно сохранена!";
        }
        return "Нужно добавить описание";
    }

    private void sendFilteredPhoto(List<Project> projects, String filter) {
        List<Project> resultList = Util.getFilteredProjects(projects, filter, isIndividual);
        for (Project p : resultList) {
            sendPhoto.setCaption(p.toString());
            sendPhotoByPath(PART_OF_PATH + p.getFileName(), sendPhoto);
        }
        sendMessage.setReplyMarkup(Keyboard.categoriesKeyboard(supService.getSupportById(1)));
        sendMessage("Выберите категорию:", sendMessage);
    }

    private void getFile(Update update, String savePath) throws TelegramApiException {
        GetFile getFile = new GetFile(update.getMessage().getPhoto().get(3).getFileId());
        File file = execute(getFile);
        downloadFile(file, new java.io.File(savePath));
    }
}
