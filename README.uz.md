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

Minecraft serveringizni Telegram bilan bog'lang! Serveringizdagi barcha harakatlarni Telegram guruh yoki kanalingiz orqali real vaqtda kuzating.

## 🌟 Asosiy imkoniyatlar

### 📡 Real vaqtda xabarlar yuborish
- **O'yinchilar harakati**: Kirish/Chiqish xabarlari onlayn o'yinchilar soni bilan
- **Chat nazorati**: Administrator belgilari bilan barcha o'yin ichidagi xabarlar
- **O'lim kuzatuvi**: O'ldiruvchi haqida ma'lumot bilan batafsil o'lim xabarlari
- **Yutuqlar tizimi**: Formatlangan o'yinchi yutuqlari
- **Dunyo kuzatuvi**: O'yinchilarning dunyo o'rtasida harakatlanishini kuzatish
- **Buyruqlarni kuzatish**: Filtr bilan buyruqlarni bajarilishini nazorat qilish
- **Forum mavzulari**: Forum mavzularida xabarlarni tashkil qilish
- **Avto-yangilanish**: Versiyani avtomatik tekshirish va yangilanish xabarlari

### 🛡️ Rivojlangan chat va buyruqlar tizimi
- **Chat filtri**: Administrator xabardorligi bilan keraksiz so'zlarni bloklash
- **Buyruqlarni kuzatish**: Statistika bilan ma'lum buyruqlarni nazorat qilish
- **Mavzu qo'llab-quvvatlash**: Turli xabarlarni alohida mavzularda tashkil qilish
- **Sudo buyruqlari**: Server buyruqlarini Telegram orqali to'g'ridan-to'g'ri bajarish
- **Kanallar bo'linishi**: Turli xabar turlari uchun turli kanallar
- **Boy formatlash**: HTML formatlash va emojilar bilan

### 🎮 Administrator imkoniyatlari
- **Masofadan boshqarish**: Telegram orqali pluginni Ishga tushirish/To'xtatish/Qayta yuklash
- **Administrator tizimi**: Telegram administratorlarini ro'yxatga olish va boshqarish
- **Statistika**: Batafsil xabar va hodisalarni kuzatish
- **Debug rejimi**: Batafsil loglar bilan rivojlangan nosozliklarni bartaraf etish
- **Real vaqt holati**: Server holati va o'yinchilar sonini jonli ko'rsatish
- **Administrator xabarlari**: Muhim hodisalar haqida xabarlar

## 📋 Talablar
- Java 8 yoki yangirog'i
- Spigot/Paper 1.16 - 1.21.x
- Telegram Bot Tokeni
- Telegram Guruh/Kanal ID si
- Mavzu ID lari (forum mavzularidan foydalanganda)

## ⚡ Batafsil o'rnatish qo'llanmasi

### Asosiy sozlash
1. So'nggi .jar faylni [Releases](https://github.com/LazizbekDeveloper/TelegramLogger/releases) dan yuklab oling
2. Server `plugins` papkasiga joylang
3. Konfiguratsiya yaratish uchun serverni ishga tushiring
4. Konfiguratsiyani tahrirlash uchun serverni to'xtating

### Bot sozlash
1. [@BotFather](https://t.me/botfather) orqali bot yarating:
   ```
   /newbot
   Bot nomini kiriting
   Bot foydalanuvchi nomini kiriting
   API Token nusxalang
   ```
2. Bot sozlamalari (Ixtiyoriy):
   ```
   /setprivacy - O'chirish
   /setjoingroups - Yoqish
   /setcommands - Buyruqlar qo'shish
   ```

### Chat ID larni olish
1. [@RawDataBot](https://t.me/rawdatabot) ni guruhga qo'shing
2. Javobdan "chat":{"id": RAQAM} toping
3. Forum guruhlari uchun mavzu ID larini ham yozib oling

### Konfiguratsiya sozlash
1. `config.yml` ni tahrirlang:
   ```yaml
   bot_token: "BOT_TOKENINGIZ"
   chat_id: "CHAT_ID_NGIZ"
   thread_id: "MAVZU_ID" # Forum ishlatayotgan bo'lsangiz
   ```
2. Xabar shablonlarini sozlang:
   ```yaml
   # Kirish xabari namunasi
   join_message: "<blockquote>🎮 O'yinchi <b>%player%</b> kirdi!\nOnlayn: %online%/%max%</blockquote>"
   ```
3. Saqlang va pluginni qayta yuklang

## 🔧 Kengaytirilgan sozlash qo'llanmasi

### Mavzularni tashkil qilish
Forum mavzularini hodisalarni ajratish uchun yoqish:
```yaml
# Asosiy chat sozlamalari
send_to_thread: true
thread_id: "ASOSIY_MAVZU_ID"

# Buyruqlarni yuborish
enable_send_command_executes: true
command_executes_chat_id: "BUYRUQLAR_CHAT_ID"
send_command_executes_to_thread: true
command_executes_group_thread_id: "BUYRUQLAR_MAVZU_ID"
```

### Chat filtri sozlash
So'zlarni filtrlash va xabardorlik sozlash:
```yaml
enable_chat_filter: true
filtered_words:
  - "yomonso'z"
  - "so'kinish"
  - "nojo'ya"
filtered_message: "<b>⚠️ %player% dan filtrlangan xabar</b>"
```

### Buyruqlarni kuzatish
Ma'lum buyruqlarni kuzatish:
```yaml
enable_send_command_executes: true
ignored_commands:
  - "/login"
  - "/register"
  - "/help"
command_execute_message: "🔧 <b>%player%</b> bajardi: <code>%command%</code>"
```

### Xabar sozlamalari
HTML formatlash va emoji ishlatish:
```yaml
# O'ldiruvchi ma'lumoti bilan o'lim xabari
death_message: "<blockquote>💀 <b>%player%</b> o'ldirildi!\nSabab: <i>%death_message%</i>\nServer: <code>%online%/%max% o'yinchi</code></blockquote>"

# Emoji bilan dunyo o'zgarishi
world_switch_message: "<blockquote>🌍 <b>%player%</b> o'tdi:\nBundan: <code>%from_world%</code>\nBunga: <code>%to_world%</code></blockquote>"
```

## 🎯 Buyruqlar yo'riqnomasi

### O'yin ichidagi buyruqlar
| Buyruq | Ruxsat | Tavsif |
|--------|---------|---------|
| `/tl reload` | `telegramlogger.admin` | Konfiguratsiyani qayta yuklash |
| `/tl start` | `telegramlogger.admin` | Xabar yuborishni boshlash |
| `/tl stop` | `telegramlogger.admin` | Xabar yuborishni to'xtatish |
| `/tl stats` | `telegramlogger.admin` | Statistikani ko'rish |
| `/tl debug` | `telegramlogger.admin` | Debug rejimini almashtirish |
| `/tl admin add <id> <ism>` | `telegramlogger.admin` | Telegram admin qo'shish |
| `/tl admin remove <id>` | `telegramlogger.admin` | Adminni o'chirish |
| `/tl admin list` | `telegramlogger.admin` | Adminlar ro'yxati |
| `/tl help` | `telegramlogger.use` | Yordam ko'rsatish |

### Telegram buyruqlari
| Buyruq | Tavsif |
|--------|---------|
| `/sudo <buyruq>` | Server buyrug'ini bajarish |
| `/status` | Server holatini ko'rish |
| `/stats` | Statistikani ko'rish |
| `/players` | Onlayn o'yinchilar ro'yxati |
| `/start` | Yuborishni boshlash |
| `/stop` | Yuborishni to'xtatish |
| `/debug` | Debug rejimi |
| `/help` | Buyruqlarni ko'rsatish |

## 🎨 HTML formatlash yo'riqnomasi
```
<b>Qalin matn</b>
<i>Kursiv matn</i>
<u>Tagiga chizilgan matn</u>
<s>O'chirilgan matn</s>
<code>Kod matni</code>
<blockquote>Iqtibos</blockquote>
```

## 📊 Statistika va o'lchovlar
Plugin kuzatadi:
- Yuborilgan jami xabarlar
- Kirish/Chiqish sonlari
- Chat xabarlari
- O'limlar
- Yutuqlar
- Dunyo o'zgarishlari
- Filtrlangan xabarlar
- Bajarilgan buyruqlar

`/tl stats` yoki Telegramda `/stats` orqali ko'ring

## 🔍 Muammolarni bartaraf etish

### Ko'p uchraydigan muammolar
1. Bot javob bermayapti:
   - Bot tokenini tekshiring
   - Bot guruh administratori ekanini tekshiring
   - Chat ID larni tekshiring
   
2. Xabarlar formatlanmayapti:
   - HTML sintaksisni tekshiring
   - Placeholder ishlatishni tekshiring
   - Debug rejimini yoqing

### Debug rejimi
Batafsil loglar uchun yoqing:
```yaml
debug_mode: true
```
Yoki `/tl debug` buyrug'idan foydalaning

## 📝 Asosiy konfiguratsiya
```yaml
# ===========================================
#        TelegramLogger Configuration
#        Developer by LazizbekDev
#        Telegram: https://t.me/LazizbekDev
# ===========================================



# Bot Sozlamalari
# -------------------------------------------
bot_token: "BOT_TOKEN"  # Bot tokeningiz
chat_id: "CHAT_ID"    # Chat ID ngiz
thread_id: "THREAD_ID"  # Mavzu ID (forum uchun)
send_to_thread: false   # Mavzuga yuborish uchun yoqing
send_telegram_messages_to_game: false  # Telegramdan o'yinga xabar yuborish



# Xabar prefiksi
# -------------------------------------------
plugin_prefix: "&6&lTelegramLogger&7 ➜ &r&a"
telegram_game_message: "&7[&9TG&7] &c%name% &8» &f%message%"
# Mavjud placeholderlar: %name%, %message%



# Kirish xabarlari
# -------------------------------------------
enable_join: true
join_message: "<blockquote>ㅤㅤㅤㅤㅤ\n ➕ <b><u>%player%</u></b> o'yinga kirdi! (Onlayn: %online%/%max%)\nㅤㅤㅤㅤ</blockquote>"
# Mavjud placeholderlar: %player%, %displayname%, %online%, %max%



# Chiqish xabarlari
# -------------------------------------------
enable_leave: true
leave_message: "<blockquote>ㅤㅤㅤㅤㅤ\n ➖ <b><u>%player%</u></b> o'yindan chiqdi! (Onlayn: %online%/%max%)\nㅤㅤㅤㅤ</blockquote>"
# Mavjud placeholderlar: %player%, %displayname%, %online%, %max%



# Chat xabarlari
# -------------------------------------------
enable_chat: true
chat_message: "<b><u>%player%</u></b> <b>➥</b> %message%"
# Mavjud placeholderlar: %player%, %displayname%, %message%, %online%, %max%



# Yutuq xabarlari
# -------------------------------------------
enable_advancement: true
advancement_message: "<blockquote>ㅤㅤㅤㅤㅤ\n 🏆 <b><u>%player%</u></b> yutuqqa erishdi <u>[%advancement%]</u> (Onlayn: %online%/%max%)\nㅤㅤㅤㅤ</blockquote>"
# Mavjud placeholderlar: %player%, %displayname%, %advancement%, %online%, %max%



# O'lim xabarlari
# -------------------------------------------
enable_death: true
death_message: "<blockquote>ㅤㅤㅤㅤㅤ\n 💀 <b><u>%player%</u></b> o'lim: %death_message% (Onlayn: %online%/%max%)\nㅤㅤㅤㅤ</blockquote>"
# Mavjud placeholderlar: %player%, %displayname%, %death_message%, %online%, %max%



# Dunyo o'zgarish xabarlari
# -------------------------------------------
enable_world_switch: true
world_switch_message: "<blockquote>ㅤㅤㅤㅤㅤ\n 🌍 <b><u>%player%</u></b> dunyo almashtirdi <u>%from_world%</u> dan <u>%to_world%</u> ga (Onlayn: %online%/%max%)\nㅤㅤㅤㅤ</blockquote>"
# Mavjud placeholderlar: %player%, %displayname%, %from_world%, %to_world%, %online%, %max%



# Chat filtri
# -------------------------------------------
enable_chat_filter: true
filtered_words:
  - "yomonso'z1"
  - "yomonso'z2"
filtered_message: "<blockquote>ㅤㅤㅤㅤㅤ\n 🚫 <b><u>%player%</u></b> taqiqlangan so'z ishlatdi. (Onlayn: %online%/%max%)\nㅤㅤㅤㅤ</blockquote>"
# Mavjud placeholderlar: %player%, %displayname%, %online%, %max%



# Buyruqlar bajarilishi
enable_send_command_executes: false
command_execute_message: "<blockquote>ㅤㅤㅤㅤㅤ\n 💠 <b><u>%player%</u></b> <b>➥</b> %command% . (Onlayn: %online%/%max%)\nㅤㅤㅤㅤ</blockquote>"
command_executes_chat_id: "CHAT_ID"
send_command_executes_to_thread: false
command_executes_group_thread_id: "THREAD_ID"
ignored_commands: 
  - "/login"
  - "/register"
# Mavjud placeholderlar: %player%, %displayname%, %command%, %online%, %max%



# Telegram Sudo buyrug'i
enable_sudo_command: false

# Debug rejimi (tajribali foydalanuvchilar uchun)
# -------------------------------------------
debug_mode: false

# Plugin versiyasi (O'ZGARTIRMANG)
# -------------------------------------------
version: "4.0.0"
```

## 📱 Aloqa va qo'llab-quvvatlash
- Telegram: [@LazizbekDev](https://t.me/LazizbekDev)
- Yangilanishlar: [@LazizbekDev_Blog](https://t.me/LazizbekDev_Blog)
- Muammolar: [GitHub](https://github.com/LazizbekDeveloper/TelegramLogger/issues)

## 📜 Litsenziya
Ushbu loyiha MIT litsenziyasi ostida tarqatiladi. [LICENSE](LICENSE) faylini ko'ring.

## ❤️ Minnatdorchilik
- Ishlab chiquvchi: [LazizbekDev](https://t.me/LazizbekDev)
- Framework: [Spigot](https://www.spigotmc.org)

---

<div align="center">
  <b><a href="https://t.me/LazizbekDev">LazizbekDev</a> tomonidan ❤️ bilan yaratildi</b>
  <br><br>
  Agar plugin foydali bo'lsa, iltimos GitHub da ⭐ tugmasini bosing!
</div>