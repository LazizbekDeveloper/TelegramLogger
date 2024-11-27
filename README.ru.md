<div align="center">
  
[English](README.md) | [Русский](README.ru.md) | [O'zbekcha](README.uz.md)

</div>

# TelegramLogger 🚀
[![Java](https://img.shields.io/badge/Java-8%2B-orange)](https://www.java.com)
[![Spigot](https://img.shields.io/badge/Spigot-1.16--1.21-yellow)](https://www.spigotmc.org)
[![Version](https://img.shields.io/badge/version-4.0.0-brightgreen)](https://github.com/LazizbekDeveloper/TelegramLogger/releases)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Downloads](https://img.shields.io/badge/downloads-1K%2B-blue)](https://www.spigotmc.org/resources/120590)

<div align="center">
  <img src="https://minecraft.wiki/images/Mojang_logo.svg" width="96px" />
   ---> 
  <img src="https://telegram.org/img/t_logo.svg" width="96px" />
</div>

Подключите ваш Minecraft сервер к Telegram! Отслеживайте все действия на сервере в реальном времени через вашу группу или канал Telegram.

## 🌟 Обзор возможностей

### 📡 Отправка событий в реальном времени
- **Активность игроков**: Сообщения о входе/выходе с количеством онлайн игроков 
- **Мониторинг чата**: Все сообщения в игре с отметками администраторов
- **Отслеживание смертей**: Подробные сообщения о смерти с информацией об убийце
- **Система достижений**: Достижения игроков с форматированием
- **Мониторинг миров**: Отслеживание перемещений игроков между измерениями
- **Логирование команд**: Мониторинг выполнения команд с фильтрацией
- **Поддержка тем**: Организация сообщений в темах форума
- **Автообновление**: Автоматическая проверка версий и уведомления об обновлениях

### 🛡️ Продвинутая система чата и команд
- **Фильтрация чата**: Блокировка нежелательных слов с уведомлениями администраторов
- **Отслеживание команд**: Мониторинг определенных команд со статистикой
- **Поддержка тем**: Организация различных событий в отдельных темах
- **Sudo команды**: Выполнение серверных команд напрямую через Telegram
- **Разделение каналов**: Разные каналы для разных типов событий
- **Форматирование**: HTML форматирование с эмодзи и стилями 

### 🎮 Функции администратора
- **Удаленное управление**: Запуск/Остановка/Перезагрузка плагина через Telegram
- **Система администраторов**: Регистрация и управление администраторами Telegram
- **Статистика**: Подробное отслеживание сообщений и событий
- **Режим отладки**: Расширенное устранение неполадок с подробными логами
- **Статус в реальном времени**: Актуальный статус сервера и количество игроков
- **Уведомления**: Уведомления о важных событиях

## 📋 Требования
- Java 8 или новее
- Spigot/Paper 1.16 - 1.21.x
- Токен бота Telegram
- ID группы/канала Telegram
- ID тем (при использовании форума)

## ⚡ Подробное руководство по установке

### Базовая настройка
1. Скачайте последний .jar файл из [Releases](https://github.com/LazizbekDeveloper/TelegramLogger/releases)
2. Поместите в папку `plugins` сервера
3. Запустите сервер для генерации конфига
4. Остановите сервер для редактирования конфигурации

### Настройка бота
1. Создайте бота через [@BotFather](https://t.me/botfather):
   ```
   /newbot
   Укажите имя бота
   Укажите username бота
   Скопируйте API токен
   ```
2. Настройки бота (Опционально):
   ```
   /setprivacy - Отключить
   /setjoingroups - Включить
   /setcommands - Добавить команды
   ```

### Получение ID чатов
1. Добавьте [@RawDataBot](https://t.me/rawdatabot) в группу
2. Найдите "chat":{"id": ЧИСЛО} в ответе
3. Для групп с форумом также запишите ID тем

### Настройка конфигурации
1. Отредактируйте `config.yml`:
   ```yaml
   bot_token: "ВАШ_ТОКЕН_БОТА"
   chat_id: "ВАШ_ID_ЧАТА"
   thread_id: "ID_ТЕМЫ" # Если используете форум
   ```
2. Настройте шаблоны сообщений:
   ```yaml
   # Пример настройки сообщения о входе
   join_message: "<blockquote>🎮 Игрок <b>%player%</b> зашел!\nОнлайн: %online%/%max%</blockquote>"
   ```
3. Сохраните и перезагрузите плагин

## 🔧 Расширенное руководство по настройке

### Организация тем
Включение тем форума для разделения событий:
```yaml
# Основные настройки чата
send_to_thread: true
thread_id: "ID_ОСНОВНОЙ_ТЕМЫ"

# Логирование команд
enable_send_command_executes: true
command_executes_chat_id: "ID_ЧАТА_КОМАНД"
send_command_executes_to_thread: true
command_executes_group_thread_id: "ID_ТЕМЫ_КОМАНД"
```

### Настройка фильтра чата
Настройка фильтрации слов и уведомлений:
```yaml
enable_chat_filter: true
filtered_words:
  - "плохоеслово"
  - "ругательство"
  - "мат"
filtered_message: "<b>⚠️ Отфильтровано сообщение от %player%</b>"
```

### Отслеживание команд
Мониторинг определенных команд:
```yaml
enable_send_command_executes: true
ignored_commands:
  - "/login"
  - "/register"
  - "/help"
command_execute_message: "🔧 <b>%player%</b> выполнил: <code>%command%</code>"
```

### Настройка сообщений 
Использование HTML форматирования и эмодзи:
```yaml
# Сообщение о смерти с информацией об убийце
death_message: "<blockquote>💀 <b>%player%</b> был убит!\nПричина: <i>%death_message%</i>\nСервер: <code>%online%/%max% игроков</code></blockquote>"

# Смена мира с эмодзи
world_switch_message: "<blockquote>🌍 <b>%player%</b> перешел:\nИз: <code>%from_world%</code>\nВ: <code>%to_world%</code></blockquote>"
```

## 🎯 Справочник команд

### Команды в игре
| Команда | Право доступа | Описание |
|---------|---------------|----------|
| `/tl reload` | `telegramlogger.admin` | Перезагрузка конфигурации |
| `/tl start` | `telegramlogger.admin` | Запуск отправки сообщений |
| `/tl stop` | `telegramlogger.admin` | Остановка отправки |
| `/tl stats` | `telegramlogger.admin` | Просмотр статистики |
| `/tl debug` | `telegramlogger.admin` | Переключение режима отладки |
| `/tl admin add <id> <имя>` | `telegramlogger.admin` | Добавить админа Telegram |
| `/tl admin remove <id>` | `telegramlogger.admin` | Удалить админа |
| `/tl admin list` | `telegramlogger.admin` | Список админов |
| `/tl help` | `telegramlogger.use` | Показать справку |

### Команды Telegram  
| Команда | Описание |
|---------|----------|
| `/sudo <команда>` | Выполнить серверную команду |
| `/status` | Показать статус сервера |
| `/stats` | Просмотр статистики |
| `/players` | Список онлайн игроков |
| `/start` | Запуск отправки |
| `/stop` | Остановка отправки |
| `/debug` | Режим отладки |
| `/help` | Показать команды |

## 🎨 Руководство по HTML форматированию
```
<b>Жирный текст</b>
<i>Курсив</i>
<u>Подчеркнутый текст</u>
<s>Зачеркнутый текст</s>
<code>Моноширинный текст</code>
<blockquote>Цитата</blockquote>  
```

## 📊 Статистика и метрики
Плагин отслеживает:
- Общее количество отправленных сообщений
- Количество входов/выходов
- Сообщения чата
- Смерти
- Достижения
- Смены миров
- Отфильтрованные сообщения
- Выполненные команды

Просмотр через `/tl stats` или `/stats` в Telegram

## 🔍 Устранение неполадок

### Частые проблемы
1. Бот не отвечает:
   - Проверьте токен бота
   - Убедитесь, что бот является администратором группы
   - Проверьте ID чатов
   
2. Сообщения не форматируются:
   - Проверьте синтаксис HTML
   - Проверьте использование плейсхолдеров
   - Включите режим отладки

### Режим отладки
Включите для подробных логов:  
```yaml
debug_mode: true
```
Или используйте команду `/tl debug`

## 📝 Конфигурация по умолчанию
```yaml
# ===========================================
#        TelegramLogger Configuration
#        Developer by LazizbekDev
#        Telegram: https://t.me/LazizbekDev
# ===========================================



# Конфигурация бота
# -------------------------------------------
bot_token: "BOT_TOKEN"  # Токен вашего бота
chat_id: "CHAT_ID"    # ID вашего чата
thread_id: "THREAD_ID"  # ID темы (для форумов)
send_to_thread: false   # Включите для отправки в тему
send_telegram_messages_to_game: false  # Пересылка сообщений из Telegram в игру



# Префикс сообщений
# -------------------------------------------
plugin_prefix: "&6&lTelegramLogger&7 ➜ &r&a"
telegram_game_message: "&7[&9TG&7] &c%name% &8» &f%message%"
# Доступные плейсхолдеры: %name%, %message%



# Сообщения о входе
# -------------------------------------------
enable_join: true
join_message: "<blockquote>ㅤㅤㅤㅤㅤ\n ➕ <b><u>%player%</u></b> зашел в игру! (Онлайн: %online%/%max%)\nㅤㅤㅤㅤ</blockquote>"
# Доступные плейсхолдеры: %player%, %displayname%, %online%, %max%



# Сообщения о выходе
# -------------------------------------------
enable_leave: true
leave_message: "<blockquote>ㅤㅤㅤㅤㅤ\n ➖ <b><u>%player%</u></b> вышел из игры! (Онлайн: %online%/%max%)\nㅤㅤㅤㅤ</blockquote>"
# Доступные плейсхолдеры: %player%, %displayname%, %online%, %max%



# Сообщения чата
# -------------------------------------------
enable_chat: true
chat_message: "<b><u>%player%</u></b> <b>➥</b> %message%"
# Доступные плейсхолдеры: %player%, %displayname%, %message%, %online%, %max%



# Сообщения о достижениях
# -------------------------------------------
enable_advancement: true
advancement_message: "<blockquote>ㅤㅤㅤㅤㅤ\n 🏆 <b><u>%player%</u></b> получил достижение <u>[%advancement%]</u> (Онлайн: %online%/%max%)\nㅤㅤㅤㅤ</blockquote>"
# Доступные плейсхолдеры: %player%, %displayname%, %advancement%, %online%, %max%



# Сообщения о смерти
# -------------------------------------------
enable_death: true
death_message: "<blockquote>ㅤㅤㅤㅤㅤ\n 💀 <b><u>%player%</u></b> погиб: %death_message% (Онлайн: %online%/%max%)\nㅤㅤㅤㅤ</blockquote>"
# Доступные плейсхолдеры: %player%, %displayname%, %death_message%, %online%, %max%



# Сообщения о смене мира
# -------------------------------------------
enable_world_switch: true
world_switch_message: "<blockquote>ㅤㅤㅤㅤㅤ\n 🌍 <b><u>%player%</u></b> перешел из <u>%from_world%</u> в <u>%to_world%</u> (Онлайн: %online%/%max%)\nㅤㅤㅤㅤ</blockquote>"
# Доступные плейсхолдеры: %player%, %displayname%, %from_world%, %to_world%, %online%, %max%



# Фильтр чата
# -------------------------------------------
enable_chat_filter: true
filtered_words:
  - "плохоеслово1"
  - "плохоеслово2"
filtered_message: "<blockquote>ㅤㅤㅤㅤㅤ\n 🚫 <b><u>%player%</u></b> использовал запрещенное слово. (Онлайн: %online%/%max%)\nㅤㅤㅤㅤ</blockquote>"
# Доступные плейсхолдеры: %player%, %displayname%, %online%, %max%



# Выполнение команд
enable_send_command_executes: false
command_execute_message: "<blockquote>ㅤㅤㅤㅤㅤ\n 💠 <b><u>%player%</u></b> <b>➥</b> %command% . (Онлайн: %online%/%max%)\nㅤㅤㅤㅤ</blockquote>"
command_executes_chat_id: "CHAT_ID"
send_command_executes_to_thread: false
command_executes_group_thread_id: "THREAD_ID"
ignored_commands: 
  - "/login"
  - "/register"
# Доступные плейсхолдеры: %player%, %displayname%, %command%, %online%, %max%



# Команда Sudo в Telegram
enable_sudo_command: false

# Режим отладки (только для опытных пользователей)
# -------------------------------------------
debug_mode: false

# Версия плагина (НЕ ИЗМЕНЯТЬ)
# -------------------------------------------
version: "4.0.0"
```

## 📱 Контакты и поддержка
- Telegram: [@LazizbekDev](https://t.me/LazizbekDev)
- Обновления: [@LazizbekDev_Blog](https://t.me/LazizbekDev_Blog)
- Проблемы: [GitHub](https://github.com/LazizbekDeveloper/TelegramLogger/issues)

## 📜 Лицензия
Этот проект лицензирован под MIT License. См. файл [LICENSE](LICENSE).

## ❤️ Благодарности
- Разработчик: [LazizbekDev](https://t.me/LazizbekDev)
- Фреймворк: [Spigot](https://www.spigotmc.org)

---

<div align="center">
  <b>Сделано с ❤️ от <a href="https://t.me/LazizbekDev">LazizbekDev</a></b>
  <br><br>
  Если плагин оказался полезным, пожалуйста, поставьте ⭐ на GitHub!
</div>