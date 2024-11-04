# TelegramLogger 🚀
[![Java](https://img.shields.io/badge/Java-8%2B-orange)](https://www.java.com)
[![Spigot](https://img.shields.io/badge/Spigot-1.16--1.21-yellow)](https://www.spigotmc.org)
[![Version](https://img.shields.io/badge/version-3.0.0-brightgreen)](https://github.com/LazizbekDeveloper/TelegramLogger/releases)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Downloads](https://img.shields.io/badge/downloads-1K%2B-blue)](https://www.spigotmc.org/resources/120590)

<div align="center">
  <img src="https://minecraft.wiki/images/Mojang_logo.svg" width="100px" />
   ---> 
  <img src="https://telegram.org/img/t_logo.svg" width="100px" />
</div>

Connect your Minecraft server with Telegram! Monitor all server activities in real-time through your Telegram group or channel.

## 🌟 Features Overview

### 📡 Real-time Event Forwarding
- **Player Activity**: Join/Leave messages with online player count
- **Chat Monitoring**: All in-game chat messages
- **Death Tracking**: Player deaths with detailed messages
- **Advancement System**: Player achievements and advancements
- **World Monitoring**: Track player world changes
- **Command Logging**: Monitor command executions (NEW!)
- **Auto-Update**: Version checking and update notifications

### 🛡️ Advanced Chat & Command System
- **Chat Filtering**: Block unwanted words and phrases
- **Command Tracking**: Monitor specific commands
- **Thread Support**: Organize messages in forum topics
- **Sudo Commands**: Execute server commands via Telegram
- **Channel Separation**: Different channels for different events

### 🎮 Admin Features
- **Remote Control**: Manage server through Telegram
- **Admin System**: Register Telegram admins
- **Statistics**: Detailed message and event statistics 
- **Debug Mode**: Advanced troubleshooting options
- **Real-time Monitoring**: Live server status updates

### 🎨 Customization
- **Message Templates**: Fully customizable messages
- **HTML Formatting**: Rich text support with HTML
- **Emoji Support**: Built-in emoji for messages
- **Multi-language**: Supports all languages
- **Placeholder System**: Dynamic message variables

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

## 📝 Default Configuration
```yaml
# ===========================================
#        TelegramLogger Configuration       
# ===========================================

# Bot Settings
bot_token: "BOT_TOKEN"         # Your bot token from @BotFather
chat_id: "CHAT_ID"            # Main chat/channel ID
thread_id: "THREAD_ID"        # Optional forum topic ID
send_to_thread: false         # Use thread for messages
send_telegram_messages_to_game: true

# Message Settings
plugin_prefix: "&6&lTelegramLogger&7 ➜ &r&a"
telegram_game_message: "&7[&9TG&7] &c%name% &8» &f%message%"

# Event Settings
enable_join: true             # Player join messages
enable_leave: true            # Player leave messages  
enable_chat: true            # Chat messages
enable_advancement: true      # Achievement messages
enable_death: true           # Death messages
enable_world_switch: true    # World change messages

# Message Templates [With HTML Support]
join_message: "<blockquote>ㅤㅤㅤㅤㅤ\n ➕ <b><u>%player%</u></b> joined the game! (Online: %online%/%max%)\nㅤㅤㅤㅤ</blockquote>"
leave_message: "<blockquote>ㅤㅤㅤㅤㅤ\n ➖ <b><u>%player%</u></b> left the game! (Online: %online%/%max%)\nㅤㅤㅤㅤ</blockquote>"
chat_message: "<b><u>%player%</u></b> <b>➥</b> %message%"
advancement_message: "<blockquote>ㅤㅤㅤㅤㅤ\n 🏆 <b><u>%player%</u></b> made the advancement <u>[%advancement%]</u>\nㅤㅤㅤㅤ</blockquote>"
death_message: "<blockquote>ㅤㅤㅤㅤㅤ\n 💀 <b><u>%player%</u></b> death: %death_message%\nㅤㅤㅤㅤ</blockquote>"
world_switch_message: "<blockquote>ㅤㅤㅤㅤㅤ\n 🌍 <b><u>%player%</u></b> moved from <u>%from_world%</u> to <u>%to_world%</u>\nㅤㅤㅤㅤ</blockquote>"

# Chat Filter
enable_chat_filter: true
filtered_words:
  - "badword1"
  - "badword2"
filtered_message: "<blockquote>ㅤㅤㅤㅤㅤ\n 🚫 <b><u>%player%</u></b> used a filtered word.\nㅤㅤㅤㅤ</blockquote>"

# Command Executes
enable_send_command_executes: false  # Enable command logging
command_executes_chat_id: "CHAT_ID"  # Separate chat for commands
send_command_executes_to_thread: false
command_executes_group_thread_id: "THREAD_ID"
command_execute_message: "<blockquote>ㅤㅤㅤㅤㅤ\n 💠 <b><u>%player%</u></b> <b>➥</b> %command%\nㅤㅤㅤㅤ</blockquote>"

# Ignored Commands
ignored_commands:
  - "/login"
  - "/register"

# Advanced Settings
debug_mode: false            # Enable debug logging
version: "3.0.0"            # DO NOT EDIT
```

## 🎯 Commands & Permissions

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

## 📖 Placeholders
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

## 🔧 Configuration Guide

### Basic Setup
1. Create Telegram bot via [@BotFather](https://t.me/botfather)
2. Get bot token
3. Add bot to your group/channel
4. Get chat ID using [@RawDataBot](https://t.me/rawdatabot)
5. Configure `config.yml`
6. Reload plugin

### Thread Support
1. Enable forum in your group
2. Create topic
3. Get thread ID from message URL
4. Set `send_to_thread: true`
5. Configure `thread_id`

### Command Tracking
1. Set `enable_send_command_executes: true`  
2. Configure `command_executes_chat_id`
3. Add commands to `ignored_commands`
4. Customize `command_execute_message`

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