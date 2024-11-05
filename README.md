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
- **Chat Monitoring**: All in-game chat messages
- **Death Tracking**: Player deaths with detailed messages
- **Advancement System**: Player achievements and advancements
- **World Monitoring**: Track player world changes
- **Command Logging**: Monitor command executions
- **Console Logging**: Real-time console output forwarding (NEW!)
- **Auto-Update**: Version checking and update notifications

### 🛡️ Advanced Chat & Command System
- **Chat Filtering**: Block unwanted words and phrases
- **Command Tracking**: Monitor specific commands
- **Thread Support**: Organize messages in forum topics
- **Sudo Commands**: Execute server commands via Telegram
- **Channel Separation**: Different channels for different events
- **Console Monitoring**: Separate channel for console logs (NEW!)

### 🎮 Admin Features
- **Remote Control**: Manage server through Telegram
- **Admin System**: Register Telegram admins
- **Statistics**: Detailed message and event statistics 
- **Debug Mode**: Advanced troubleshooting options
- **Real-time Monitoring**: Live server status updates
- **Multi-Channel**: Separate channels for different logs (NEW!)

### 🎨 Customization
- **Message Templates**: Fully customizable messages
- **HTML Formatting**: Rich text support with HTML
- **Emoji Support**: Built-in emoji for messages
- **Multi-language**: Supports all languages
- **Placeholder System**: Dynamic message variables
- **Thread Organization**: Forum topic support for each feature (NEW!)

## 📋 Requirements
- Java 8 or newer
- Spigot/Paper 1.16+
- Telegram Bot Token
- Telegram Group/Channel ID

## ⚡ Installation
1. Download latest .jar from [Releases](https://github.com/LazizbekDeveloper/TelegramLogger/releases)
2. Place in server's `plugins` folder
3. Start/restart server
4. Edit `config.yml`
5. Get bot token from [@BotFather](https://t.me/botfather)
6. Get chat ID (Add [@RawDataBot](https://t.me/rawdatabot) to group)
7. Configure `config.yml`
8. Reload plugin with `/tl reload`

## 🔄 New Features in v4.0.0

### 🖥️ Console Logging
- Real-time console output forwarding
- Separate channel support
- Thread organization
- Custom message format
- Filtered logging options

### 📊 Enhanced Statistics
- Console log statistics
- Command execution tracking
- Filtered message counts
- Thread usage metrics

### 🧩 Multi-Channel Support
- Different channels for:
  - Console logs
  - Command executes
  - Chat messages
  - Player events

### 🎯 Commands & Permissions

### Admin Commands
| Command | Permission | Description |
|---------|------------|-------------|
| `/tl reload` | `telegramlogger.admin` | Reload configuration |
| `/tl start` | `telegramlogger.admin` | Start message forwarding |
| `/tl stop` | `telegramlogger.admin` | Stop message forwarding |
| `/tl stats` | `telegramlogger.admin` | View message statistics |
| `/tl status` | `telegramlogger.admin` | Show plugin status |
| `/tl debug` | `telegramlogger.admin` | Toggle debug mode |

### Telegram Admin Management
| Command | Permission | Description |
|---------|------------|-------------|
| `/tl admin add <id> <name>` | `telegramlogger.admin` | Add Telegram admin |
| `/tl admin remove <id>` | `telegramlogger.admin` | Remove admin |  
| `/tl admin list` | `telegramlogger.admin` | List all admins |

### User Commands
| Command | Permission | Description |  
|---------|------------|-------------|
| `/tl help` | `telegramlogger.use` | Show help message |

### Telegram Commands
| Command | Description |
|---------|-------------|
| `/sudo <command>` | Execute server command |
| `/status` | Show server status |
| `/players` | List online players |
| `/help` | Show Telegram commands |

## 📖 Extended Placeholders
| Placeholder | Description |
|-------------|-------------|
| `%player%` | Player username |
| `%displayname%` | Player display name |
| `%message%` | Chat/command message |
| `%command%` | Executed command |
| `%online%` | Online player count |
| `%max%` | Maximum players |
| `%advancement%` | Achievement name |
| `%death_message%` | Death message |
| `%from_world%` | Previous world |
| `%to_world%` | New world |
| `%log%` | Console log message (NEW!) |
| `%time%` | Current time (NEW!) |

## 🔧 Advanced Configuration Guide

### Console Logging Setup
1. Enable console logging:
```yaml
enable_send_console_logs: true
```
2. Configure console log channel:
```yaml
console_log_chat_id: "YOUR_CHAT_ID"
```
3. Optional thread support:
```yaml
send_console_log_to_thread: true
console_log_group_thread_id: "THREAD_ID"
```
4. Customize message format:
```yaml
console_log_message: "<blockquote>ㅤㅤㅤㅤㅤ\n 🖥️ <b><u>Console Log</u></b> <b>➥</b> %log% . (Online: %online%/%max%)\nㅤㅤㅤㅤ</blockquote>"
```

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
send_to_thread: false
send_telegram_messages_to_game: true

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

# Console Logs
enable_send_console_logs: false
console_log_message: "<blockquote>ㅤㅤㅤㅤㅤ\n 🖥️ <b><u>Console Log</u></b> <b>➥</b> %log% . (Online: %online%/%max%)\nㅤㅤㅤㅤ</blockquote>"
console_log_chat_id: "CHAT_ID"
send_console_log_to_thread: false
console_log_group_thread_id: "THREAD_ID"
ignored_log_tags: []
# Available placeholders: %log%, %online%, %max%

#Telegram Sudo Command
enable_sudo_command: true

# Debug Mode (Advanced users only)
# -------------------------------------------
debug_mode: false

# Plugin Version (DO NOT EDIT)
# -------------------------------------------
version: "4.0.0"
```

### Thread Support
1. Enable forum in your group
2. Create topics for:
   - General messages
   - Console logs
   - Command executes
3. Get thread IDs
4. Configure in `config.yml`

### HTML Formatting
- `<b>text</b>` - Bold
- `<u>text</u>` - Underline  
- `<i>text</i>` - Italic
- `<s>text</s>` - Strikethrough
- `<code>text</code>` - Code Copy
- `<blockquote>text</blockquote>` - Quote

## 📞 Support
- Discord: [Join](https://discord.gg/your-discord)
- Telegram: [@LazizbekDev](https://t.me/LazizbekDev)
- Issues: [GitHub](https://github.com/LazizbekDeveloper/TelegramLogger/issues)
- Updates: [@LazizbekDev_Blog](https://t.me/LazizbekDev_Blog)

## 📜 License
This project is licensed under the MIT License. See [LICENSE](LICENSE) file.

## ❤️ Credits
- Developer: [LazizbekDev](https://t.me/LazizbekDev)
- Framework: [Spigot](https://www.spigotmc.org)
- Icons: [Flaticon](https://www.flaticon.com)

---

<div align="center">
  <b>Made with ❤️ by <a href="https://t.me/LazizbekDev">LazizbekDev</a></b>
  <br><br>
  If you find this plugin helpful, please give it a ⭐ on GitHub!
</div>
