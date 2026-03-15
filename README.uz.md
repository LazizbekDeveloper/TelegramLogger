<div align="center">

[English](README.md) | [Русский](README.ru.md) | [O'zbekcha](README.uz.md)

# TelegramLogger

[![Java](https://img.shields.io/badge/Java-8%2B-orange)](https://www.java.com)
[![Spigot](https://img.shields.io/badge/Spigot-1.16--1.21-yellow)](https://www.spigotmc.org)
[![Version](https://img.shields.io/badge/version-5.0.1-brightgreen)](https://github.com/LazizbekDeveloper/TelegramLogger/releases)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Downloads](https://img.shields.io/badge/downloads-1K%2B-blue)](https://www.spigotmc.org/resources/120590)

<img src="https://minecraft.wiki/images/Mojang_logo.svg" width="96px" />
 --->
<img src="https://telegram.org/img/t_logo.svg" width="96px" />

**Minecraft serveringizni Telegram bilan bog'lang!**
Serveringizdagi barcha harakatlarni Telegram guruh yoki kanalingiz orqali real vaqtda kuzating.

</div>

---

## Mundarija

- [Imkoniyatlar](#imkoniyatlar)
- [v5.0.0 dagi yangiliklar](#v500-dagi-yangiliklar)
- [Talablar](#talablar)
- [O'rnatish qo'llanmasi](#ornatish-qollanmasi)
  - [1-qadam: Yuklab olish va o'rnatish](#1-qadam-yuklab-olish-va-ornatish)
  - [2-qadam: Telegram bot yaratish](#2-qadam-telegram-bot-yaratish)
  - [3-qadam: Chat ID olish](#3-qadam-chat-id-olish)
  - [4-qadam: Pluginni sozlash](#4-qadam-pluginni-sozlash)
- [Konfiguratsiya ma'lumotnomasi](#konfiguratsiya-malumotnomasi)
  - [Bot sozlamalari](#bot-sozlamalari)
  - [Xabar prefiksi](#xabar-prefiksi)
  - [Prefiks almashtirishlari](#prefiks-almashtirishlari)
  - [Server ishga tushishi/to'xtashi](#server-ishga-tushishitoxtashi)
  - [Kirish/Chiqish xabarlari](#kirishchiqish-xabarlari)
  - [Birinchi kirish (Yangi o'yinchi)](#birinchi-kirish-yangi-oyinchi)
  - [Chat xabarlari](#chat-xabarlari)
  - [Yutuq xabarlari](#yutuq-xabarlari)
  - [O'lim xabarlari](#olim-xabarlari)
  - [Dunyo almashtirish xabarlari](#dunyo-almashtirish-xabarlari)
  - [Chat filtri](#chat-filtri)
  - [Buyruq bajarilishi logi](#buyruq-bajarilishi-logi)
  - [Telegram Sudo buyrug'i](#telegram-sudo-buyrugi)
  - [Flood himoyasi](#flood-himoyasi)
  - [Debug rejimi](#debug-rejimi)
- [Buyruqlar ma'lumotnomasi](#buyruqlar-malumotnomasi)
  - [O'yin ichidagi buyruqlar](#oyin-ichidagi-buyruqlar)
  - [Telegram buyruqlari](#telegram-buyruqlari)
- [Admin boshqaruvi](#admin-boshqaruvi)
- [Xabar placeholderlari](#xabar-placeholderlari)
- [HTML formatlash qo'llanmasi](#html-formatlash-qollanmasi)
- [Forum mavzularini tashkil qilish](#forum-mavzularini-tashkil-qilish)
- [Statistika va metrikalar](#statistika-va-metrikalar)
- [Konfiguratsiyani avtomatik tiklash](#konfiguratsiyani-avtomatik-tiklash)
- [Arxitektura](#arxitektura)
- [Manba koddan yig'ish](#manba-koddan-yigish)
- [Muammolarni bartaraf etish](#muammolarni-bartaraf-etish)
- [Ko'p so'raladigan savollar](#kop-soraladigan-savollar)
- [Aloqa va qo'llab-quvvatlash](#aloqa-va-qollab-quvvatlash)
- [Litsenziya](#litsenziya)
- [Minnatdorchilik](#minnatdorchilik)

---

## Imkoniyatlar

### Real vaqtda hodisalarni uzatish
- **O'yinchi kirishi/chiqishi** — Onlayn o'yinchilar soni bilan tezkor bildirishnomalar
- **Birinchi kirish aniqlash** — Yangi o'yinchilar uchun maxsus xush kelibsiz xabarlar
- **Chat nazorati** — Barcha o'yin ichidagi chat HTML formatlash bilan Telegramga uzatiladi
- **O'lim kuzatuvi** — O'ldiruvchi ma'lumoti bilan batafsil o'lim xabarlari
- **Yutuqlar tizimi** — O'yinchi yutuqlari haqida bildirishnomalar (retsept ochilishlari filtrlanadi)
- **Dunyo nazorati** — O'lchamlar o'rtasida ko'chish kuzatuvi (Overworld, Nether, End, maxsus dunyolar)
- **Buyruqlarni loglash** — Sozlanishi mumkin bo'lgan filtrlash bilan buyruq bajarilishini nazorat qilish
- **Server ishga tushishi/to'xtashi** — Server ishga tushganda yoki to'xtaganda bildirishnomalar

### Telegram integratsiyasi
- **Ikki tomonlama chat** — Telegramdan to'g'ridan-to'g'ri Minecraft ga xabar yuborish
- **Forum mavzulari qo'llab-quvvatlashi** — Xabarlarni Telegram forum mavzularida tashkil qilish
- **Masofaviy server buyruqlari** — `/sudo` orqali istalgan server buyrug'ini to'liq natija bilan bajarish
- **Server holati** — Telegramdan server holati, TPS, xotira va onlayn o'yinchilarni tekshirish
- **Admin tizimi** — Doimiy saqlash bilan Telegram adminlarini ro'yxatdan o'tkazish
- **HTML formatlash** — Qalin, kursiv, kod bloklari va iqtiboslar bilan boy xabar formatlash

### Himoya va ishonchlilik
- **Flood himoyasi** — Tezlik cheklash ikkala yo'nalishda ham xabar to'lib ketishining oldini oladi
- **Konfiguratsiyani avtomatik tiklash** — Buzilgan konfiguratsiya fayllari zaxiralanadi va avtomatik qayta yaratiladi
- **Chat filtrlash** — Admin bildirishnomalari bilan keraksiz so'zlarni bloklash
- **Sudo qora ro'yxat** — Xavfli buyruqlarning masofadan bajarilishining oldini olish
- **Takroriy xabar oldini olish** — Polling himoyasi xabar takrorlanishini yo'q qiladi

### Dasturchilar uchun
- **Modulli arxitektura** — Ajratilgan mas'uliyatlar bilan toza paket tuzilmasi
- **Debug rejimi** — Muammolarni bartaraf etish uchun batafsil loglash
- **Statistika kuzatuvi** — Keng qamrovli xabar va hodisa hisoblash
- **Avtomatik yangilanish tekshiruvi** — Yangi versiya mavjud bo'lganda server adminlarini xabardor qiladi

---

## v5.0.0 dagi yangiliklar

### Xato tuzatishlar
- **Takroriy Telegram xabarlari tuzatildi** — Telegramdan xabarlar ba'zan Minecraft ga 2-3 marta yetkazilardi. Sababi bir-biriga o'xshagan uzoq so'rov (long-poll) so'rovlari edi. Atomik so'rov himoyasi bilan tuzatildi.
- **`/sudo` buyrug'i nosozligi tuzatildi** — Sudo buyrug'i forum bo'lmagan guruhlarda ishlamay qolardi, chunki `message_thread_id` mavjud bo'lmasa ham unga murojaat qilardi. Endi yo'q bo'lgan thread ID larni to'g'ri boshqaradi.
- **`/sudo` natija ko'rsatmasligi tuzatildi** — Oldin `/sudo plugins` "muvaffaqiyatli bajarildi" deb ko'rsatar, lekin natijani ko'rsatmasdi. Endi Telegramda to'liq buyruq natijasini ko'rsatadi.
- **Ko'p qatorli xabar prefiks muammosi tuzatildi** — Telegramdan ko'p qatorli xabarlarda plugin prefiksi har bir qatorga qo'shilardi. Endi prefiks faqat bir marta qo'llaniladi.
- **HTML entity buzilishi tuzatildi** — `&lt;` kabi HTML entitylar rang kodi tozalagich tomonidan buzilardi, ularni `t;` ga aylantirardi. Endi HTML entitylar butun xabar pipeline orqali to'g'ri saqlanadi.
- **`/reload` Telegram xabar to'lib ketishiga sabab bo'lishi tuzatildi** — Telegramdan `/reload` ishlatish cheksiz siklga sabab bo'lardi, chunki yangilanish offseti reload paytida yo'qolardi. Endi reload faqat o'yinda mavjud.
- **`/sudo` vanilla buyruqlar bilan nosozligi tuzatildi** — Maxsus CommandSender yondashuvi vanilla Minecraft buyruqlari bilan nosozlikka sabab bo'lardi. Endi ishonchli natija olish uchun konsol yuboruvchisi va logger yozuvchisi ishlatiladi.
- **Chat mention plugin axlati tuzatildi** — Mention plaginlari xabarlarga `<chat=UUID:text>` kabi komponent teglarini kiritadi. Bular endi Telegramga uzatishdan oldin tozalanadi.

### Yangi imkoniyatlar
- **Server ishga tushishi/to'xtashi bildirishnomalari** — Server ishga tushganda yoki to'xtaganda Telegramda xabar olasiz
- **Birinchi kirish aniqlash** — Birinchi marta kirayotgan o'yinchilar uchun maxsus xush kelibsiz xabarlar
- **Flood himoya tizimi** — Sozlanishi mumkin tezlik cheklash xabar to'lib ketishining oldini oladi
- **Sudo natija olish** — `/sudo` buyruqlari endi Telegram javobida to'liq natijani ko'rsatadi
- **Sudo qora ro'yxat** — Xavfli buyruqlarni (`stop`, `op`, `ban-ip` va h.k.) masofadan bajarishni bloklash
- **TPS buyrug'i** — Telegramdan `/tps` bilan server TPS va xotira ishlatilishini tekshirish
- **Konfiguratsiyani avtomatik tiklash** — Konfiguratsiya buzilsa, plugin avtomatik ravishda zaxiralaydi, yangi konfiguratsiya yaratadi va tiklanadigan qiymatlarni ko'chiradi
- **Prefiks almashtirishlari** — O'yin ichidagi xunuk unvon prefikslarini `prefix_replacements` konfiguratsiyasi orqali Telegram uchun toza matnga aylantiring
- **Prefiks/Suffiks placeholderlari** — Yangi `%prefix%` va `%suffix%` placeholderlari o'yinchi ko'rsatish nomlaridan unvon teglarini ajratib oladi
- **Chat komponent tozalash** — Plugin tomonidan kiritilgan komponent teglarini uzatishdan oldin avtomatik tozalaydi

### Kod yaxshilanishlari
- **To'liq kod qayta tuzilmasi** — Monolitik 3000 qatorlik fayl 6 ta paketdagi 11 ta mustaqil sinfga bo'lindi
- **Toza arxitektura** — Ajratilgan mas'uliyatlar: konfiguratsiya, telegram API, hodisa boshqarish, buyruqlar, ma'lumotlar boshqaruvi
- **Yaxshilangan xato boshqarish** — Jimgina nosozlik o'rniga aniq xato xabarlari
- **HTML qochish** — Foydalanuvchi kiritishi Telegram xabarlarida HTML in'ektsiyani oldini olish uchun to'g'ri qochiriladi

---

## Talablar

| Talab | Tafsilotlar |
|---|---|
| Java | 8 yoki yangirog'i |
| Server | Spigot yoki Paper 1.16.x - 1.21.x |
| Telegram Bot Tokeni | [@BotFather](https://t.me/botfather) dan |
| Telegram Chat ID | Guruh yoki kanal raqamli ID |
| Thread ID | Faqat forum mavzularidan foydalanilganda |

---

## O'rnatish qo'llanmasi

### 1-qadam: Yuklab olish va o'rnatish

1. So'nggi `TelegramLogger-5.0.1.jar` ni [Releases](https://github.com/LazizbekDeveloper/TelegramLogger/releases) dan yuklab oling
2. `.jar` faylni serveringizning `plugins/` papkasiga joylashtiring
3. Standart konfiguratsiya yaratish uchun serverni bir marta ishga tushiring
4. Konfiguratsiyani tahrirlash uchun serverni to'xtating

```
plugins/
  TelegramLogger-5.0.1.jar
  TelegramLogger/
    config.yml          <-- Ushbu faylni tahrirlang
    data.json           <-- Avtomatik yaratilgan statistika
    admins.json         <-- Avtomatik yaratilgan admin ro'yxati
```

### 2-qadam: Telegram bot yaratish

1. Telegram ni oching va [@BotFather](https://t.me/botfather) ni toping
2. `/newbot` yuboring va ko'rsatmalarga amal qiling:
   ```
   /newbot
   > Bot nomini kiriting: Mening Server Botim
   > Bot username kiriting: my_server_bot
   ```
3. BotFather bergan **API Tokenni** nusxalang (ko'rinishi: `123456:ABC-DEF1234ghIkl-zyx57W2v1u123ew11`)

4. **Muhim Bot Sozlamalari** (bularni BotFather ga yuboring):
   ```
   /setprivacy    -> Botingizni tanlang -> Disable
   /setjoingroups -> Botingizni tanlang -> Enable
   ```

5. (Ixtiyoriy) Chiroyli buyruq menyusi uchun bot buyruqlarini o'rnating:
   ```
   /setcommands -> Botingizni tanlang -> Buni joylashtiring:
   status - Server holatini ko'rsatish
   stats - Xabar statistikasi
   players - Onlayn o'yinchilar
   start - Uzatishni boshlash
   stop - Uzatishni to'xtatish
   debug - Debug rejimini almashtirish
   tps - Server ishlashi
   sudo - Server buyrug'ini bajarish
   help - Buyruqlarni ko'rsatish
   ```

### 3-qadam: Chat ID olish

1. Telegram guruh yarating (yoki mavjudini ishlating)
2. Botingizni guruhga **administrator sifatida** qo'shing
3. [@RawDataBot](https://t.me/rawdatabot) ni guruhga vaqtincha qo'shing
4. Javobida `"chat":{"id": -123456789}` ni toping — bu raqam sizning **Chat ID** ngiz
5. RawDataBot ni guruhdan olib tashlang

**Forum guruhlari (mavzulari bor) uchun:**
- Bildirishnomalar kerak bo'lgan mavzuda xabar yuboring
- RawDataBot `"message_thread_id"` ni ko'rsatadi — bu sizning **Thread ID** ngiz

### 4-qadam: Pluginni sozlash

`plugins/TelegramLogger/config.yml` ni tahrirlang:

```yaml
# Majburiy: BotFather dan olingan bot tokeningiz
bot_token: "123456:ABC-DEF1234ghIkl-zyx57W2v1u123ew11"

# Majburiy: Guruh/kanal chat ID ngiz
chat_id: "-1001234567890"

# Ixtiyoriy: Forum mavzulari uchun Thread ID
thread_id: "123"
send_to_thread: false

# Ikki tomonlama chat (Telegram xabarlari Minecraft da ko'rinadi)
send_telegram_messages_to_game: true
```

Faylni saqlang va o'yinda `/tl reload` ni bajaring yoki serverni qayta ishga tushiring.

**O'zingizni admin sifatida ro'yxatdan o'tkazing** (Telegram buyruqlari uchun zarur):
```
/tl admin add TELEGRAM_FOYDALANUVCHI_ID IsminGIZ
```
Telegram foydalanuvchi ID ni topish uchun guruhda RawDataBot bilan xabar yuboring va `"from":{"id": 123456}` ni toping.

---

## Konfiguratsiya ma'lumotnomasi

### Bot sozlamalari

```yaml
bot_token: "BOT_TOKEN"                    # Telegram bot API tokeningiz
chat_id: "CHAT_ID"                        # Maqsad guruh/kanal ID
thread_id: "THREAD_ID"                    # Forum mavzu thread ID
send_to_thread: false                     # Forum mavzu qo'llab-quvvatlashini yoqish
send_telegram_messages_to_game: false     # Telegram xabarlarini Minecraft ga uzatish
```

### Xabar prefiksi

```yaml
plugin_prefix: "&6&lTelegramLogger&7 ➜ &r&a"   # O'yin ichidagi xabar prefiksi (rang kodlarini qo'llab-quvvatlaydi)
telegram_game_message: "&7[&9TG&7] &c%name% &8» &f%message%"   # TG→MC xabar formati
```

`telegram_game_message` shabloni Telegram xabarlarining Minecraft da qanday ko'rinishini boshqaradi. Mavjud placeholderlar: `%name%` (yuboruvchi ismi), `%message%` (xabar matni).

### Prefiks almashtirishlari

```yaml
prefix_replacements:
  "VIP": "[VIP]"
  "ADMIN": "[ADMIN]"
```

O'yin ichidagi xunuk unvon prefikslarini Telegram xabarlari uchun toza matnga almashtiring. Kalit — o'yinchining ko'rsatish nomida topiladigan matn, qiymat — uni nima bilan almashtirish kerak. Almashtirilgan prefiksni ko'rsatish uchun xabar shablonlarida `%prefix%` placeholderini ishlating.

### Server ishga tushishi/to'xtashi

```yaml
enable_server_start_stop: true
server_start_message: "<blockquote>🟢 <b>Server ishga tushdi!</b>\nVersiya: %version% | Maks o'yinchilar: %max%</blockquote>"
server_stop_message: "<blockquote>🔴 <b>Server to'xtadi!</b></blockquote>"
```

Server ishga tushganda yoki to'xtaganda Telegram ga bildirishnoma yuboradi. Placeholderlar: `%version%`, `%max%`.

### Kirish/Chiqish xabarlari

```yaml
enable_join: true
join_message: "<blockquote>➕ <b>%player%</b> o'yinga kirdi! (%online%/%max%)</blockquote>"

enable_leave: true
leave_message: "<blockquote>➖ <b>%player%</b> o'yindan chiqdi! (%online%/%max%)</blockquote>"
```

### Birinchi kirish (Yangi o'yinchi)

```yaml
enable_first_join: true
first_join_message: "<blockquote>🌟 <b>%player%</b> birinchi marta kirdi! Xush kelibsiz! (%online%/%max%)</blockquote>"
```

Faqat o'yinchi serverga birinchi marta kirganda ishga tushadi (`player.hasPlayedBefore()` orqali aniqlanadi).

### Chat xabarlari

```yaml
enable_chat: true
chat_message: "💬 <b>%player%</b> ➥ %message%"
```

Barcha o'yin ichidagi chat xabarlari Telegram ga uzatiladi. Foydalanuvchi xabarlari in'ektsiyani oldini olish uchun HTML-qochiriladi.

### Yutuq xabarlari

```yaml
enable_advancement: true
advancement_message: "<blockquote>🏆 <b>%player%</b> <b>[%advancement%]</b> yutuqqa erishdi (%online%/%max%)</blockquote>"
```

Retsept ochilish yutuqlari avtomatik filtrlanadi.

### O'lim xabarlari

```yaml
enable_death: true
death_message: "<blockquote>💀 %death_message% (%online%/%max%)</blockquote>"
```

O'ldiruvchi ma'lumoti bilan to'liq Minecraft o'lim xabarini o'z ichiga oladi.

### Dunyo almashtirish xabarlari

```yaml
enable_world_switch: true
world_switch_message: "<blockquote>🌍 <b>%player%</b> ko'chdi: %from_world% → %to_world% (%online%/%max%)</blockquote>"
```

Dunyo nomlari avtomatik ravishda emojilar bilan formatlanadi:
| Dunyo nomi | Ko'rinishi |
|---|---|
| `world` | 🌍 Overworld |
| `world_nether` | 🔥 The Nether |
| `world_the_end` | 🌌 The End |
| `spawn` | 🏔️ Spawn |
| `lobby` | 🏔️ Lobby |
| `maxsus_dunyo` | 🌎 Maxsus Dunyo |

### Chat filtri

```yaml
enable_chat_filter: true
filtered_words:
  - "yomonso'z1"
  - "yomonso'z2"
  - "nojo'yaso'z"
filtered_message: "<blockquote>🚫 <b>%player%</b> filtrlangan so'z ishlatdi. (%online%/%max%)</blockquote>"
```

Filtrlangan so'z aniqlanganda:
1. Asl xabar Telegram ga **uzatilmaydi**
2. Filtrlangan bildirishnoma yuboriladi
3. O'yin ichidagi adminlar ogohlantirish oladi

### Buyruq bajarilishi logi

```yaml
enable_send_command_executes: false
command_execute_message: "<blockquote>💠 <b>%player%</b> ➥ <code>%command%</code> (%online%/%max%)</blockquote>"
command_executes_chat_id: "CHAT_ID"           # Asosiy chatdan farqli bo'lishi mumkin
send_command_executes_to_thread: false
command_executes_group_thread_id: "THREAD_ID"
ignored_commands:
  - "/login"
  - "/register"
  - "/l"
  - "/reg"
```

`ignored_commands` ro'yxatidagi buyruqlar loglanmaydi. Asosiy chatni toza saqlash uchun buyruq loglari uchun alohida Telegram chat/mavzu ishlatishingiz mumkin.

### Telegram Sudo buyrug'i

```yaml
enable_sudo_command: false
sudo_show_output: true
sudo_blacklist:
  - "stop"
  - "restart"
  - "op"
  - "deop"
  - "ban-ip"
```

Yoqilganda, ro'yxatdan o'tgan adminlar Telegramdan server buyruqlarini bajara oladi:

```
/sudo list          → Onlayn o'yinchilarni ko'rsatadi
/sudo plugins       → O'rnatilgan plaginlarni natija bilan ko'rsatadi
/sudo kill Steve    → Steve o'yinchisini o'ldiradi
/sudo say Salom!    → Xabar tarqatadi
/sudo gamemode creative Steve → O'yin rejimini o'zgartiradi
```

Javob quyidagilarni ko'rsatadi:
- Buyruq muvaffaqiyatli bo'ldimi
- Qaysi buyruq bajarildi
- Qaysi admin bajargan
- To'liq buyruq natijasi (`sudo_show_output: true` bo'lsa)

`sudo_blacklist` dagi buyruqlar xavfsizlik uchun bajarilishi bloklanadi.

### Flood himoyasi

```yaml
anti_flood_enabled: true
anti_flood_max_messages: 20     # Oynada ruxsat etilgan maksimal xabarlar soni
anti_flood_window_seconds: 10   # Oyna davomiyligi soniyalarda
```

Quyidagi holatlarda to'lib ketishning oldini oladi:
- Ommaviy o'yinchi kirish/chiqish hodisalari
- Chat spam
- Tez yutuq ochilishlari
- Hodisa bo'ronlari

Tezlik chegarasidan oshgan xabarlar jimgina tashlab yuboriladi.

### Debug rejimi

```yaml
debug_mode: false
```

O'yinda `/tl debug` yoki Telegramdan `/debug` bilan yoqing. Quyidagilar uchun batafsil loglarni ko'rsatadi:
- Xabarlarni qayta ishlash
- Telegram API chaqiruvlari
- Konfiguratsiya yuklash
- Tezlik cheklash qarorlari
- Admin tekshiruvlari

---

## Buyruqlar ma'lumotnomasi

### O'yin ichidagi buyruqlar

Barcha buyruqlar `telegramlogger.admin` ruxsatini talab qiladi (standart: op).

| Buyruq | Qisqartma | Tavsif |
|---|---|---|
| `/telegramlogger reload` | `/tl reload` | Konfiguratsiyani diskdan qayta yuklash |
| `/telegramlogger start` | `/tl start` | Xabar uzatishni boshlash |
| `/telegramlogger stop` | `/tl stop` | Xabar uzatishni to'xtatish |
| `/telegramlogger stats` | `/tl stats` | Batafsil xabar statistikasini ko'rish |
| `/telegramlogger status` | `/tl status` | Plugin va bot holatini ko'rsatish |
| `/telegramlogger debug` | `/tl debug` | Debug rejimini almashtirish |
| `/telegramlogger admin add <id> <ism>` | `/tl admin add <id> <ism>` | Telegram admin ro'yxatdan o'tkazish |
| `/telegramlogger admin remove <id>` | `/tl admin remove <id>` | Telegram adminni o'chirish |
| `/telegramlogger admin list` | `/tl admin list` | Barcha ro'yxatdan o'tgan adminlarni ko'rsatish |
| `/telegramlogger help` | `/tl help` | Yordam xabarini ko'rsatish |

### Telegram buyruqlari

Barcha buyruqlar yuboruvchining ro'yxatdan o'tgan admin bo'lishini talab qiladi.

| Buyruq | Tavsif |
|---|---|
| `/status` | Plugin holati, imkoniyatlar, server ma'lumoti va xotirani ko'rsatish |
| `/stats` | Sonlar va foizlar bilan xabar statistikasini ko'rish |
| `/players` yoki `/online` | Onlayn o'yinchilar ro'yxati (20 tagacha, admin tojlari bilan) |
| `/tps` | Server TPS (1d/5d/15d) va xotira ishlatilishini ko'rsatish |
| `/start` | Xabar uzatishni boshlash |
| `/stop` | Xabar uzatishni to'xtatish |
| `/debug` | Debug rejimini almashtirish |
| `/sudo <buyruq>` | Server buyrug'ini bajarish (agar yoqilgan bo'lsa) |
| `/help` | Mavjud buyruqlarni ko'rsatish |

---

## Admin boshqaruvi

Adminlar — bu quyidagi imkoniyatlarga ega Telegram foydalanuvchilari:
- Telegramdan Minecraft chatiga xabar yuborish
- Telegram buyruqlarini ishlatish (/status, /stats, va h.k.)
- /sudo orqali server buyruqlarini bajarish (agar yoqilgan bo'lsa)

### Admin ro'yxatdan o'tkazish

```
/tl admin add 123456789 Steve
```

Bu yerda `123456789` — Telegram foydalanuvchi ID, `Steve` — ko'rsatiladigan ism.

### Adminni o'chirish

```
/tl admin remove 123456789
```

### Adminlar ro'yxati

```
/tl admin list
```

Admin ma'lumotlari `plugins/TelegramLogger/admins.json` da saqlanadi va qayta ishga tushirishlar orasida saqlanib qoladi.

**Muhim:** Admin bir vaqtda ikkalasi ham bo'lishi kerak:
1. `/tl admin add` orqali ro'yxatdan o'tgan
2. Telegram guruhida administrator yoki yaratuvchi

---

## Xabar placeholderlari

Mavjud placeholderlar xabar turiga qarab farq qiladi:

| Placeholder | Mavjud joyi | Tavsif |
|---|---|---|
| `%player%` | Barcha o'yinchi hodisalari | O'yinchi foydalanuvchi nomi |
| `%displayname%` | Barcha o'yinchi hodisalari | O'yinchi ko'rsatiladigan nomi (ranglarni o'z ichiga olishi mumkin) |
| `%prefix%` | Barcha o'yinchi hodisalari | O'yinchi unvon prefiksi (ko'rsatish nomidan ajratilgan) |
| `%suffix%` | Barcha o'yinchi hodisalari | O'yinchi unvon suffiksi (ko'rsatish nomidan ajratilgan) |
| `%online%` | Barcha hodisalar | Joriy onlayn o'yinchilar soni |
| `%max%` | Barcha hodisalar | Maksimal o'yinchi joylari |
| `%message%` | Chat xabarlari | Chat xabari mazmuni |
| `%command%` | Buyruq loglash | Bajarilgan buyruq |
| `%death_message%` | O'lim xabarlari | Minecraft dan to'liq o'lim xabari |
| `%advancement%` | Yutuq xabarlari | Yutuq nomi |
| `%from_world%` | Dunyo almashtirish | Oldingi dunyo (emoji bilan) |
| `%to_world%` | Dunyo almashtirish | Yangi dunyo (emoji bilan) |
| `%version%` | Server ishga tushishi | Server versiya matni |
| `%name%` | Telegram→MC xabarlari | Telegram yuboruvchi ismi |

---

## HTML formatlash qo'llanmasi

Telegram xabarlarda HTML formatlashni qo'llab-quvvatlaydi. Xabar shablonlaringizda ushbu teglarni ishlating:

```html
<b>Qalin matn</b>
<i>Kursiv matn</i>
<u>Tagiga chizilgan matn</u>
<s>O'chirilgan matn</s>
<code>Monospace/kod matni</code>
<pre>Oldindan formatlangan blok</pre>
<blockquote>Iqtibos matn bloki</blockquote>
<a href="https://example.com">Havola matni</a>
```

### Namuna maxsus xabarlar

```yaml
# Chiroyli kirish xabari
join_message: "<blockquote>🎮 <b>%player%</b> kirdi!\n👥 O'yinchilar: <code>%online%/%max%</code></blockquote>"

# Formatlangan o'lim xabari
death_message: "<blockquote>💀 <i>%death_message%</i>\n👥 <code>%online%/%max%</code></blockquote>"

# Minimal chat formati
chat_message: "<b>%player%</b>: %message%"

# Batafsil buyruq logi
command_execute_message: "🔧 <b>%player%</b> bajardi: <code>%command%</code>"
```

---

## Forum mavzularini tashkil qilish

Telegram forum guruhlari (mavzulari bo'lgan superguruhlar) turli xil hodisa turlarini alohida mavzularga tashkil qilish imkonini beradi:

```yaml
# Asosiy hodisalar bir mavzuga boradi
send_to_thread: true
thread_id: "12345"

# Buyruq loglari boshqa mavzuga (yoki boshqa guruhga) boradi
enable_send_command_executes: true
command_executes_chat_id: "-1001234567890"
send_command_executes_to_thread: true
command_executes_group_thread_id: "67890"
```

Bu server hodisalarini tartibli va navigatsiya qilish oson qiladi.

---

## Statistika va metrikalar

Plugin keng qamrovli statistikani kuzatadi:

| Metrika | Tavsif |
|---|---|
| Jami xabarlar | Telegram ga yuborilgan barcha xabarlar |
| Kirish xabarlari | O'yinchi kirish hodisalari soni |
| Chiqish xabarlari | O'yinchi chiqish hodisalari soni |
| Chat xabarlari | Uzatilgan chat xabarlari |
| Yutuq xabarlari | Uzatilgan yutuqlar |
| O'lim xabarlari | Uzatilgan o'lim hodisalari |
| Dunyo almashtirish | Uzatilgan o'lcham o'zgarishlari |
| Filtrlangan xabarlar | Chat filtri tomonidan bloklangan xabarlar |
| Birinchi kirish xabarlari | Yangi o'yinchi xush kelibsiz xabarlari |
| Buyruq xabarlari | Loglangan buyruqlar |
| E'tiborsiz qoldirilgan buyruqlar | O'tkazib yuborilgan ro'yxatdagi buyruqlar |
| Server ishga tushish soni | Loglangan server ishga tushishlari soni |

Statistikani ko'rish:
- **O'yinda:** `/tl stats` — Progress barlari va foizlarni ko'rsatadi
- **Telegramda:** `/stats` — Toza HTML-formatlangan statistikani ko'rsatadi

Statistika `plugins/TelegramLogger/data.json` da saqlanadi va qayta ishga tushirishlar orasida saqlanib qoladi.

---

## Konfiguratsiyani avtomatik tiklash

Agar `config.yml` buzilsa (noto'g'ri YAML, kodlash muammolari va h.k.), plugin avtomatik ravishda:

1. Yuklash paytida buzilgan faylni **aniqlaydi**
2. Buzilgan faylni `config.yml.broken.2026-03-15_14-30-00` sifatida **zaxiralaydi**
3. Barcha standart qiymatlar bilan yangi `config.yml` **yaratadi**
4. Buzilgan fayldan har qanday yaroqli `kalit: qiymat` juftliklarini **tiklaydi**
5. Tiklangan qiymatlarni yangi konfiguratsiyaga **birlashtiradi**

Bu YAML sintaksis xatosi tufayli bot tokeningiz, chat ID laringiz yoki maxsus xabarlaringizni hech qachon yo'qotmasligingizni anglatadi.

---

## Arxitektura

Plugin toza, modulli paketlarga tashkil etilgan:

```
uz.lazizbekdev.telegramlogger/
├── TelegramLogger.java              # Asosiy plugin (hayot sikli, menejer koordinatsiyasi)
├── config/
│   └── ConfigManager.java           # Konfiguratsiya yuklash/saqlash/tekshirish/tiklash
├── telegram/
│   ├── TelegramAPI.java             # Telegram Bot API bilan HTTP aloqa
│   ├── TelegramHandler.java         # Polling, buyruq yo'naltirish, xabar qayta ishlash
├── listeners/
│   └── EventListener.java           # Barcha Minecraft hodisa boshqaruvchilari
├── commands/
│   └── CommandHandler.java          # /telegramlogger buyruq boshqaruvchisi + tab to'ldirish
├── managers/
│   ├── DataManager.java             # Statistika saqlash (data.json)
│   └── AdminManager.java            # Admin ro'yxati saqlash (admins.json)
└── utils/
    ├── MessageUtils.java            # Rang kodlari, formatlash, placeholderlar
    └── AntiFloodManager.java        # Sirpanuvchi oyna bilan tezlik cheklash
```

---

## Manba koddan yig'ish

### Talablar
- Java Development Kit (JDK) 8 yoki yangirog'i
- Apache Maven 3.6+
- Git

### Yig'ish bosqichlari

```bash
# Repozitoriyani klonlash
git clone https://github.com/LazizbekDeveloper/TelegramLogger.git
cd TelegramLogger

# Pluginni yig'ish
mvn clean package

# Natija JAR fayl quyidagi manzilda:
# target/TelegramLogger-5.0.1.jar
```

### Yig'ish natijasi

Yig'ish jarayoni:
1. Barcha Java manbalarini Spigot API 1.16.5 ga qarshi kompilyatsiya qiladi
2. Google GSON ni jar ichiga joylashtiradi (ziddiyatlarni oldini olish uchun ko'chiriladi)
3. Bitta mustaqil JAR fayl ishlab chiqaradi

Natija JAR foydalanishga tayyor — uni `plugins/` papkasiga tashlang.

---

## Muammolarni bartaraf etish

### Bot javob bermayapti

1. **Bot tokenini tekshiring** — BotFather dan to'g'ri nusxalanganiga ishonch hosil qiling
2. **Bot guruh admini bo'lishi kerak** — Telegram guruhingizda botni administrator sifatida qo'shing
3. **Chat ID ni tekshiring** — To'g'ri guruh ID ni tasdiqlash uchun RawDataBot dan foydalaning
4. **Privacy rejimi** — BotFather ga `/setprivacy` yuboring va botingiz uchun `Disable` qilib o'rnating
5. **`/tl status` ni tekshiring** — Bot ulanishi faolligini ko'rsatadi

### Xabarlar Telegramda ko'rinmayapti

1. **Imkoniyat almashtirgichlarini tekshiring** — `enable_join`, `enable_chat` va h.k. `true` ekanligiga ishonch hosil qiling
2. **Flood himoyasini tekshiring** — Bir vaqtda ko'p hodisa yuzaga kelsa, ba'zilari tezlik chegarasidan oshishi mumkin
3. **Debug rejimini yoqing** — `/tl debug` ni bajaring va konsolda xatolarni tekshiring
4. **Konfiguratsiya yaroqliligini tekshiring** — `/tl reload` ni bajaring va xato xabarlarini tekshiring

### Xabarlar Minecraft da ko'rinmayapti

1. **Imkoniyatni yoqing** — `send_telegram_messages_to_game: true` qiling
2. **Admin sifatida ro'yxatdan o'ting** — `/tl admin add <telegram_id> <ism>` ishlating
3. **Guruh admini bo'lishingiz kerak** — Telegram akkauntingiz guruh admini VA ro'yxatdan o'tgan bo'lishi kerak

### `/sudo` ishlamayapti

1. **Sudo ni yoqing** — Konfiguratsiyada `enable_sudo_command: true` qiling
2. **Qora ro'yxatni tekshiring** — Buyruq `sudo_blacklist` da bo'lishi mumkin
3. **Admin sifatida ro'yxatdan o'ting** — `/tl admin add` orqali ro'yxatdan o'tgan bo'lishingiz kerak
4. **Natijani tekshiring** — Buyruq bajarilishi mumkin, lekin ko'rinadigan natija bermasligi mumkin

### Takroriy xabarlar

Bu v4.x da ma'lum muammo edi va v5.0.0 da tuzatildi. Agar hali ham duch kelayotgan bo'lsangiz:
1. v5.0.0 yoki yangirog'ini ishlatayotganingizga ishonch hosil qiling
2. Polling tizimini qayta boshlash uchun `/tl reload` ni sinab ko'ring

### Forum mavzu muammolari

1. **Thread ID mos kelishi kerak** — To'g'ri thread ID ni olish uchun muayyan mavzuda RawDataBot dan foydalaning
2. **`send_to_thread` `true` bo'lishi kerak** — Aks holda thread_id e'tiborga olinmaydi
3. **Botda mavzu ruxsatlari bo'lishi kerak** — Bot muayyan mavzuda xabar yubora olishiga ishonch hosil qiling

### Konfiguratsiya buzilgan

Plugin buni v5.0.0 da avtomatik boshqaradi:
1. Buzilgan konfiguratsiya `config.yml.broken.<vaqt_tamg'asi>` sifatida saqlanadi
2. Yangi konfiguratsiya yaratiladi
3. Tiklanadigan qiymatlar ko'chiriladi

Muammolarni qo'lda ham tuzatishingiz mumkin:
1. Buzilgan faylni YAML sintaksis xatolari uchun tekshiring
2. YAML tekshirgich ishlating (masalan, yamllint.com)
3. Ko'p uchraydigan muammolar: yo'q qo'shtirnoqlar, tab belgilari (probel ishlating), yopilmagan satrlar

---

## Ko'p so'raladigan savollar

**S: Bu plugin Paper bilan ishlaydia?**
J: Ha! TelegramLogger Paper, Purpur va forklarni o'z ichiga olgan har qanday Spigot asosidagi server bilan ishlaydi.

**S: Bir nechta Telegram guruh bilan ishlata olamanmi?**
J: Asosiy hodisalar bitta guruh/mavzuga boradi. Buyruq loglash `command_executes_chat_id` orqali alohida guruhga yuborilishi mumkin.

**S: Bot tokeni xavfsiz saqlanadimi?**
J: Bot tokeni serveringizdagi `config.yml` da saqlanadi. Server fayllaringiz to'g'ri himoyalanganiga ishonch hosil qiling.

**S: Telegram foydalanuvchi ID ni qanday topaman?**
J: [@RawDataBot](https://t.me/rawdatabot) ni guruhga qo'shing, xabar yuboring va `"from":{"id": SIZNING_ID}` ni toping.

**S: Oddiy o'yinchilar Telegram buyruqlarini ishlata oladimi?**
J: Yo'q. Faqat ro'yxatdan o'tgan adminlar VA Telegram guruh adminlari bo'lgan foydalanuvchilar buyruqlarni ishlata oladi.

**S: `/sudo` ning cheklovlari bormi?**
J: Ha — `sudo_blacklist` dagi buyruqlar bloklanadi. Natija 3000 belgigacha qisqartiriladi. Buyruq konsol sifatida (to'liq ruxsatlar bilan) bajariladi.

**S: Telegram ishlamay qolsa nima bo'ladi?**
J: Plugin API nosozliklarini xatosiz boshqaradi. Hodisalar lokal ravishda kuzatilishda davom etadi. Xabarlar API qayta mavjud bo'lguncha yuborilmaydi.

**S: Bu qancha server ishlashini sarflaydi?**
J: Minimal. Barcha Telegram aloqasi asinxron va asosiy thread dan tashqarida ishlaydi. Flood himoya tizimi ortiqcha API chaqiruvlarning oldini oladi.

**S: Dunyo nomlaridagi emojilarni sozlash mumkinmi?**
J: Hozircha dunyo nomi formatlash o'rnatilgan. Maxsus dunyo nomlari avtomatik formatlanadi. O'z emojilaringizni qo'shish uchun xabar shablonlarini sozlashingiz mumkin.

---

## Standart konfiguratsiya

<details>
<summary>To'liq standart config.yml ni ko'rish uchun bosing</summary>

```yaml
# TelegramLogger v5.0.1
# Developed by LazizbekDev
# https://t.me/LazizbekDev

# ---------------------
# Bot sozlamalari
# ---------------------
bot_token: "BOT_TOKEN"                          # Telegram Bot API tokeningiz @BotFather dan
chat_id: "CHAT_ID"                              # Maqsad Telegram guruh/kanal ID
thread_id: "THREAD_ID"                          # Forum mavzu thread ID (faqat send_to_thread true bo'lsa)
send_to_thread: false                           # Muayyan forum mavzu threadiga xabar yuborish
send_telegram_messages_to_game: false           # Telegram admin xabarlarini Minecraft chatiga uzatish

# ---------------------
# Xabar prefiksi
# ---------------------
plugin_prefix: "&6&lTelegramLogger&7 ➜ &r&a"
telegram_game_message: "&7[&9TG&7] &c%name% &8» &f%message%"
# Mavjud placeholderlar: %name%, %message%

# ---------------------
# Prefiks almashtirishlari
# ---------------------
prefix_replacements: {}

# ---------------------
# Server ishga tushishi/to'xtashi
# ---------------------
enable_server_start_stop: true
server_start_message: "<blockquote>🟢 <b>Server ishga tushdi!</b>\nVersiya: %version% | Maks o'yinchilar: %max%</blockquote>"
server_stop_message: "<blockquote>🔴 <b>Server to'xtadi!</b></blockquote>"
# Mavjud placeholderlar: %version%, %max%

# ---------------------
# Kirish xabarlari
# ---------------------
enable_join: true
join_message: "<blockquote>➕ <b>%player%</b> o'yinga kirdi! (%online%/%max%)</blockquote>"
# Mavjud placeholderlar: %player%, %displayname%, %online%, %max%

# ---------------------
# Birinchi kirish (yangi o'yinchilar)
# ---------------------
enable_first_join: true
first_join_message: "<blockquote>🌟 <b>%player%</b> birinchi marta kirdi! Xush kelibsiz! (%online%/%max%)</blockquote>"
# Mavjud placeholderlar: %player%, %displayname%, %online%, %max%

# ---------------------
# Chiqish xabarlari
# ---------------------
enable_leave: true
leave_message: "<blockquote>➖ <b>%player%</b> o'yindan chiqdi! (%online%/%max%)</blockquote>"
# Mavjud placeholderlar: %player%, %displayname%, %online%, %max%

# ---------------------
# Chat xabarlari
# ---------------------
enable_chat: true
chat_message: "💬 <b>%player%</b> ➥ %message%"
# Mavjud placeholderlar: %player%, %displayname%, %message%, %online%, %max%

# ---------------------
# Yutuq xabarlari
# ---------------------
enable_advancement: true
advancement_message: "<blockquote>🏆 <b>%player%</b> <b>[%advancement%]</b> yutuqqa erishdi (%online%/%max%)</blockquote>"
# Mavjud placeholderlar: %player%, %displayname%, %advancement%, %online%, %max%

# ---------------------
# O'lim xabarlari
# ---------------------
enable_death: true
death_message: "<blockquote>💀 %death_message% (%online%/%max%)</blockquote>"
# Mavjud placeholderlar: %player%, %displayname%, %death_message%, %online%, %max%

# ---------------------
# Dunyo almashtirish xabarlari
# ---------------------
enable_world_switch: true
world_switch_message: "<blockquote>🌍 <b>%player%</b> ko'chdi: %from_world% → %to_world% (%online%/%max%)</blockquote>"
# Mavjud placeholderlar: %player%, %displayname%, %from_world%, %to_world%, %online%, %max%

# ---------------------
# Chat filtri
# ---------------------
enable_chat_filter: true
filtered_words:
  - "yomonso'z1"
  - "yomonso'z2"
filtered_message: "<blockquote>🚫 <b>%player%</b> filtrlangan so'z ishlatdi. (%online%/%max%)</blockquote>"
# Mavjud placeholderlar: %player%, %displayname%, %online%, %max%

# ---------------------
# Buyruq bajarilishi logi
# ---------------------
enable_send_command_executes: false
command_execute_message: "<blockquote>💠 <b>%player%</b> ➥ <code>%command%</code> (%online%/%max%)</blockquote>"
command_executes_chat_id: "CHAT_ID"             # Buyruq loglari uchun alohida chat ID (asosiy bilan bir xil bo'lishi mumkin)
send_command_executes_to_thread: false
command_executes_group_thread_id: "THREAD_ID"
ignored_commands:
  - "/login"
  - "/register"
  - "/l"
  - "/reg"
# Mavjud placeholderlar: %player%, %displayname%, %command%, %online%, %max%

# ---------------------
# Telegram Sudo buyrug'i
# ---------------------
enable_sudo_command: false                      # Adminlarga /sudo orqali server buyruqlarini bajarishga ruxsat berish
sudo_show_output: true                          # Telegram javobida buyruq natijasini ko'rsatish
sudo_blacklist:                                 # /sudo orqali bajarib bo'lmaydigan buyruqlar
  - "stop"
  - "restart"
  - "op"
  - "deop"
  - "ban-ip"

# ---------------------
# Flood himoyasi
# ---------------------
anti_flood_enabled: true                        # To'lib ketishning oldini olish uchun xabarlarni tezlik cheklash
anti_flood_max_messages: 20                     # Oynada maksimal xabarlar
anti_flood_window_seconds: 10                   # Oyna davomiyligi soniyalarda

# ---------------------
# Xato xabarlari
# ---------------------
error_not_admin: "<blockquote>❌ Siz admin sifatida ro'yxatdan o'tmagansiz!</blockquote>"

# ---------------------
# Debug rejimi
# ---------------------
debug_mode: false                               # Batafsil debug loglashni yoqish

# ---------------------
# Plugin versiyasi (TAHRIRLAMANG)
# ---------------------
version: "5.0.1"
```

</details>

---

## Aloqa va qo'llab-quvvatlash

- **Telegram:** [@LazizbekDev](https://t.me/LazizbekDev)
- **Yangilanishlar:** [@LazizbekDev_Blog](https://t.me/LazizbekDev_Blog)
- **Muammolar:** [GitHub Issues](https://github.com/LazizbekDeveloper/TelegramLogger/issues)
- **SpigotMC:** [Resurs sahifasi](https://www.spigotmc.org/resources/120590)

---

## Litsenziya

Ushbu loyiha MIT litsenziyasi ostida litsenziyalangan. Batafsil ma'lumot uchun [LICENSE](LICENSE) faylini ko'ring.

---

## Minnatdorchilik

- **Ishlab chiquvchi:** [LazizbekDev](https://t.me/LazizbekDev)
- **Framework:** [Spigot API](https://www.spigotmc.org)
- **JSON kutubxonasi:** [Google GSON](https://github.com/google/gson)

---

<div align="center">
  <b><a href="https://t.me/LazizbekDev">LazizbekDev</a> tomonidan ❤️ bilan yaratildi</b>
  <br><br>
  Agar plugin foydali bo'lsa, iltimos GitHub da ⭐ tugmasini bosing!
</div>
