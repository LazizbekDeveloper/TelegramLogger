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

Connect your Minecraft server with Telegram! Monitor all server activities in real-time through your Telegram group or channel.

## 🌟 Features Overview

### 📡 Real-time Event Forwarding
- **Player Activity**: Join/Leave messages with online player count
- **Chat Monitoring**: All in-game chat messages with admin badges
- **Death Tracking**: Detailed death messages with killer info
- **Advancement System**: Player achievements with formatting
- **World Monitoring**: Track player world changes between dimensions
- **Command Logging**: Monitor command executions with filtering
- **Thread Support**: Organize messages in forum topics
- **Auto-Update**: Automatic version checking and update notifications

### 🛡️ Advanced Chat & Command System
- **Chat Filtering**: Block unwanted words with admin notifications
- **Command Tracking**: Monitor specific commands with statistics
- **Thread Support**: Organize different events in separate topics
- **Sudo Commands**: Execute server commands directly via Telegram
- **Channel Separation**: Different channels for different event types
- **Rich Formatting**: HTML formatting with emojis and styling

### 🎮 Admin Features
- **Remote Control**: Start/Stop/Reload plugin via Telegram
- **Admin System**: Register and manage Telegram admins
- **Statistics**: Detailed message and event tracking
- **Debug Mode**: Advanced troubleshooting with detailed logs
- **Real-time Status**: Live server status and player counts
- **Admin Notifications**: Important event notifications

## 📋 Requirements
- Java 8 or newer
- Spigot/Paper 1.16 - 1.21.x
- Telegram Bot Token
- Telegram Group/Channel ID
- Thread IDs (if using forum topics)

## ⚡ Detailed Installation Guide

### Basic Setup
1. Download latest .jar from [Releases](https://github.com/LazizbekDeveloper/TelegramLogger/releases)
2. Place in server's `plugins` folder
3. Start server to generate config
4. Stop server to edit configuration

### Bot Setup
1. Create bot via [@BotFather](https://t.me/botfather):
   ```
   /newbot
   Give bot name
   Give bot username
   Copy API Token
   ```
2. Bot Settings (Optional):
   ```
   /setprivacy - Disable
   /setjoingroups - Enable
   /setcommands - Add commands
   ```

### Get Chat IDs
1. Add [@RawDataBot](https://t.me/rawdatabot) to group
2. Look for "chat":{"id": NUMBER} in response
3. For forum groups, also note thread IDs

### Configuration Setup
1. Edit `config.yml`:
   ```yaml
   bot_token: "YOUR_BOT_TOKEN"
   chat_id: "YOUR_CHAT_ID"
   thread_id: "THREAD_ID" # If using forums
   ```
2. Configure message templates:
   ```yaml
   # Example custom join message
   join_message: "<blockquote>🎮 Player <b>%player%</b> joined!\nPlayers Online: %online%/%max%</blockquote>"
   ```
3. Save and reload plugin

## 🔧 Advanced Configuration Guide

### Thread Organization
Enable forum topics to separate different events:
```yaml
# Main chat settings
send_to_thread: true
thread_id: "MAIN_THREAD_ID"

# Command logging
enable_send_command_executes: true
command_executes_chat_id: "CMDS_CHAT_ID"
send_command_executes_to_thread: true
command_executes_group_thread_id: "CMDS_THREAD_ID"
```

### Chat Filter Setup
Configure word filtering and notifications:
```yaml
enable_chat_filter: true
filtered_words:
  - "badword"
  - "swear"
  - "inappropriate"
filtered_message: "<b>⚠️ Filtered message from %player%</b>"
```

### Command Tracking
Monitor specific commands:
```yaml
enable_send_command_executes: true
ignored_commands:
  - "/login"
  - "/register"
  - "/help"
command_execute_message: "🔧 <b>%player%</b> executed: <code>%command%</code>"
```

### Message Customization
Using HTML formatting and emojis:
```yaml
# Death message with killer info
death_message: "<blockquote>💀 <b>%player%</b> was slain!\nCause: <i>%death_message%</i>\nServer: <code>%online%/%max% players</code></blockquote>"

# World change with emojis
world_switch_message: "<blockquote>🌍 <b>%player%</b> traveled:\nFrom: <code>%from_world%</code>\nTo: <code>%to_world%</code></blockquote>"
```

## 🎯 Commands Reference

### In-Game Commands
| Command | Permission | Description |
|---------|------------|-------------|
| `/tl reload` | `telegramlogger.admin` | Reload configuration |
| `/tl start` | `telegramlogger.admin` | Start forwarding |
| `/tl stop` | `telegramlogger.admin` | Stop forwarding |
| `/tl stats` | `telegramlogger.admin` | View statistics |
| `/tl debug` | `telegramlogger.admin` | Toggle debug mode |
| `/tl admin add <id> <name>` | `telegramlogger.admin` | Add Telegram admin |
| `/tl admin remove <id>` | `telegramlogger.admin` | Remove admin |
| `/tl admin list` | `telegramlogger.admin` | List admins |
| `/tl help` | `telegramlogger.use` | Show help |

### Telegram Commands
| Command | Description |
|---------|-------------|
| `/sudo <command>` | Execute server command |
| `/status` | Show server status |
| `/stats` | View message statistics |
| `/players` | List online players |
| `/start` | Start forwarding |
| `/stop` | Stop forwarding |
| `/debug` | Toggle debug mode |
| `/help` | Show commands |

## 🎨 HTML Formatting Guide
```
<b>Bold text</b>
<i>Italic text</i>
<u>Underlined text</u>
<s>Strikethrough text</s>
<code>Monospace text</code>
<blockquote>Quote text</blockquote>
```

## 📊 Statistics & Metrics
The plugin tracks:
- Total messages sent
- Join/Leave counts
- Chat messages
- Deaths
- Advancements
- World changes
- Filtered messages
- Command executions

View with `/tl stats` or `/stats` in Telegram

## 🔍 Troubleshooting

### Common Issues
1. Bot not responding:
   - Check bot token
   - Ensure bot is group admin
   - Verify chat IDs
   
2. Messages not formatting:
   - Check HTML syntax
   - Verify placeholder usage
   - Enable debug mode

### Debug Mode
Enable for detailed logs:
```yaml
debug_mode: true
```
Or use `/tl debug` command

## 📝 Default Configuration
```yaml
# ===========================================
#        TelegramLogger Configuration
#        Developer by LazizbekDev
#        Telegram: https://t.me/LazizbekDev
# ===========================================



# Bot Configuration
# -------------------------------------------
bot_token: "BOT_TOKEN"  # Your Telegram bot token
chat_id: "CHAT_ID"    # Your Telegram chat ID
thread_id: "THREAD_ID"  # Thread ID (for forum topics)
send_to_thread: false   # Enable this if messages should be sent to a thread group
send_telegram_messages_to_game: false  # If a message is written to the Telegram group by an admin, it should be sent to minecraft chat, turn it on!



# Message Prefix
# -------------------------------------------
plugin_prefix: "&6&lTelegramLogger&7 ➜ &r&a"
telegram_game_message: "&7[&9TG&7] &c%name% &8» &f%message%"
# Available placeholders: %name%, %message%



# Join Messages
# -------------------------------------------
enable_join: true
join_message: "<blockquote>ㅤㅤㅤㅤㅤ\n ➕ <b><u>%player%</u></b> joined the game! (Online: %online%/%max%)\nㅤㅤㅤㅤ</blockquote>"
# Available placeholders: %player%, %displayname%, %online%, %max%



# Leave Messages
# -------------------------------------------
enable_leave: true
leave_message: "<blockquote>ㅤㅤㅤㅤㅤ\n ➖ <b><u>%player%</u></b> left the game! (Online: %online%/%max%)\nㅤㅤㅤㅤ</blockquote>"
# Available placeholders: %player%, %displayname%, %online%, %max%



# Chat Messages
# -------------------------------------------
enable_chat: true
chat_message: "<b><u>%player%</u></b> <b>➥</b> %message%"
# Available placeholders: %player%, %displayname%, %message%, %online%, %max%



# Advancement Messages
# -------------------------------------------
enable_advancement: true
advancement_message: "<blockquote>ㅤㅤㅤㅤㅤ\n 🏆 <b><u>%player%</u></b> made the advancement <u>[%advancement%]</u> (Online: %online%/%max%)\nㅤㅤㅤㅤ</blockquote>"
# Available placeholders: %player%, %displayname%, %advancement%, %online%, %max%



# Death Messages
# -------------------------------------------
enable_death: true
death_message: "<blockquote>ㅤㅤㅤㅤㅤ\n 💀 <b><u>%player%</u></b> death: %death_message% (Online: %online%/%max%)\nㅤㅤㅤㅤ</blockquote>"
# Available placeholders: %player%, %displayname%, %death_message%, %online%, %max%



# World Switch Messages
# -------------------------------------------
enable_world_switch: true
world_switch_message: "<blockquote>ㅤㅤㅤㅤㅤ\n 🌍 <b><u>%player%</u></b> moved from <u>%from_world%</u> to <u>%to_world%</u> (Online: %online%/%max%)\nㅤㅤㅤㅤ</blockquote>"
# Available placeholders: %player%, %displayname%, %from_world%, %to_world%, %online%, %max%



# Chat Filter
# -------------------------------------------
enable_chat_filter: true
filtered_words:
  - "badword1"
  - "badword2"
filtered_message: "<blockquote>ㅤㅤㅤㅤㅤ\n 🚫 <b><u>%player%</u></b> used a filtered word. (Online: %online%/%max%)\nㅤㅤㅤㅤ</blockquote>"
# Available placeholders: %player%, %displayname%, %online%, %max%



# Command Executes
enable_send_command_executes: false
command_execute_message: "<blockquote>ㅤㅤㅤㅤㅤ\n 💠 <b><u>%player%</u></b> <b>➥</b> %command% . (Online: %online%/%max%)\nㅤㅤㅤㅤ</blockquote>"
command_executes_chat_id: "CHAT_ID"
send_command_executes_to_thread: false
command_executes_group_thread_id: "THREAD_ID"
ignored_commands: 
  - "/login"
  - "/register"
# Available placeholders: %player%, %displayname%, %command%, %online%, %max%



#Telegram Sudo Command
enable_sudo_command: false

# Debug Mode (Advanced users only)
# -------------------------------------------
debug_mode: false

# Plugin Version (DO NOT EDIT)
# -------------------------------------------
version: "4.0.0"
```

## 📱 Contact & Support
- Telegram: [@LazizbekDev](https://t.me/LazizbekDev)
- Updates: [@LazizbekDev_Blog](https://t.me/LazizbekDev_Blog)
- Issues: [GitHub](https://github.com/LazizbekDeveloper/TelegramLogger/issues)

## 📜 License
This project is licensed under the MIT License. See [LICENSE](LICENSE) file.

## ❤️ Credits
- Developer: [LazizbekDev](https://t.me/LazizbekDev)
- Framework: [Spigot](https://www.spigotmc.org)

---

<div align="center">
  <b>Made with ❤️ by <a href="https://t.me/LazizbekDev">LazizbekDev</a></b>
  <br><br>
  If you find this plugin helpful, please give it a ⭐ on GitHub!
</div>