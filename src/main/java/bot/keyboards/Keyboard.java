package bot.keyboards;

import bot.model.Category;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class Keyboard {

    public static InlineKeyboardMarkup categoriesKeyboard(Category category) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(InlineKeyboardButton.builder().text("Раздлочные доски").callbackData("/boards").build());
        row1.add(InlineKeyboardButton.builder().text("Лестницы").callbackData("/stairs").build());

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(InlineKeyboardButton.builder().text("Мебель").callbackData("/furniture").build());
        row2.add(InlineKeyboardButton.builder().text("Полки для телефона").callbackData("/phones").build());

        List<InlineKeyboardButton> row3 = new ArrayList<>();
        row3.add(InlineKeyboardButton.builder().text("Другое").callbackData("/other").build());
        row3.add(InlineKeyboardButton.builder().text(category.getName()).callbackData("/season").build());

        List<InlineKeyboardButton> row4 = new ArrayList<>();
        row4.add(InlineKeyboardButton.builder().text("Назад").callbackData("/start").build());

        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);
        keyboard.add(row4);
        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }

    public static InlineKeyboardMarkup startKeyboard() {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(InlineKeyboardButton.builder().text("Обо мне").callbackData("/about").build());
        row1.add(InlineKeyboardButton.builder().text("Заказать").callbackData("/order").build());

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(InlineKeyboardButton.builder().text("В наличии...").callbackData("/category").build());
        row2.add(InlineKeyboardButton.builder().text("Под заказ").callbackData("/individual").build());

        keyboard.add(row1);
        keyboard.add(row2);
        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }
}
