# TelegramLogger 📱
[![Java](https://img.shields.io/badge/Java-8%2B-orange)](https://www.java.com)
[![Spigot](https://img.shields.io/badge/Spigot-1.16%2B-yellow)](https://www.spigotmc.org)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Version](https://img.shields.io/badge/version-2.0.0-brightgreen)](https://github.com/LazizbekDeveloper/TelegramLogger/releases)

A powerful Minecraft plugin that forwards server events to Telegram in real-time! Keep track of your server activity from anywhere.

<div align="center">
  <img src="https://minecraft.wiki/images/Mojang_logo.svg" width="100px" />
   ---> 
  <img src="https://telegram.org/img/t_logo.svg" width="100px" />
</div>

## ✨ Features

### 📡 Real-time Event Forwarding
- Player Join/Leave Messages 👋
- Chat Messages 💬
- Deaths & Respawns ☠️
- Advancements & Achievements 🏆
- World Changes 🌍
- Command Executions 💠

### 🛡️ Advanced Chat Filtering
- Block unwanted words
- Customizable filter list
- Admin notifications
- Command filtering options

### 🎮 Admin Controls
- Telegram admin registration
- Remote command execution
- Real-time status monitoring
- Command tracking & logs

### 📊 Detailed Statistics
- Message counters
- Visual progress bars
- Event distribution charts
- Command usage analytics

### 🎨 Full Customization
- Custom message templates
- Emoji support
- Color formatting
- Multi-world support
- Separate command channels

## 📋 Requirements
- Java 8 or higher
- Spigot/Paper 1.16+
- Telegram Bot Token
- Telegram Group/Channel ID

## ⚡ Quick Start
1. Download from [Releases](https://github.com/LazizbekDeveloper/TelegramLogger/releases)
2. Put in `plugins` folder
3. Start server
4. Edit `config.yml`

## 🔧 Configuration

### 1️⃣ Bot Setup
```yaml
# Basic Settings
bot_token: "YOUR_BOT_TOKEN"
chat_id: "YOUR_CHAT_ID"
thread_id: "THREAD_ID"  # Optional
send_to_thread: false

# Command Tracking
enable_send_command_executes: true
command_executes_chat_id: "COMMAND_CHAT_ID"
send_command_executes_to_thread: false
command_executes_group_thread_id: "COMMAND_THREAD_ID"

# Ignored Commands
ignored_commands:
  - "/login"
  - "/register"
  - "/help"
```

### 2️⃣ Message Templates
```yaml
# Join Message
join_message: "<blockquote>ㅤㅤㅤㅤㅤ\n ➕ <b><u>%player%</u></b> joined the game! (Online: %online%/%max%)\nㅤㅤㅤㅤ</blockquote>"

# Command Message
command_execute_message: "<blockquote>ㅤㅤㅤㅤㅤ\n 💠 <b><u>%player%</u></b> <b>➥</b> %command% . (Online: %online%/%max%)\nㅤㅤㅤㅤ</blockquote>"
```

## 🎯 Commands

| Command | Permission | Description |
|---------|------------|-------------|
| `/telegramlogger reload` | Admin | Reload configuration |
| `/telegramlogger start` | Admin | Start forwarding |
| `/telegramlogger stop` | Admin | Stop forwarding |
| `/telegramlogger stats` | Admin | View statistics |
| `/telegramlogger status` | Admin | Show plugin status |
| `/telegramlogger debug` | Admin | Toggle debug mode |
| `/telegramlogger admin add <id> <name>` | Admin | Add Telegram admin |
| `/telegramlogger admin remove <id>` | Admin | Remove admin |
| `/telegramlogger admin list` | Admin | List admins |
| `/telegramlogger help` | Any | Show help |

## 🔒 Permissions
- `telegramlogger.admin` - Full access (Default: OP)
- `telegramlogger.use` - Basic commands (Default: All)

## 📝 Placeholders
- `%player%` - Player name
- `%displayname%` - Display name
- `%message%` - Chat message
- `%command%` - Executed command
- `%online%` - Online players
- `%max%` - Max players
- More in [Wiki](../../wiki)

## 🛠️ Building
```bash
# Clone
git clone https://github.com/LazizbekDeveloper/TelegramLogger.git

# Build
cd TelegramLogger
mvn clean package

# Find in target/TelegramLogger-2.0.0.jar
```

## 🤝 Contributing
1. Fork it
2. Create feature branch
3. Commit changes
4. Push to branch
5. Create Pull Request

## 📞 Support
- [Issues](https://github.com/LazizbekDeveloper/TelegramLogger/issues)
- [Channel](https://t.me/LazizbekDev_Blog)
- [Developer](https://t.me/LazizbekDev)

## 📜 License
MIT License - [LICENSE](LICENSE)

## ❤️ Credits
- Developer: [LazizbekDev](https://t.me/LazizbekDev)
- Framework: [Spigot](https://www.spigotmc.org)
- Icons: [Flaticon](https://www.flaticon.com)

---
<div align="center">
  <b>Made with ❤️ by <a href="https://t.me/LazizbekDev">LazizbekDev</a></b>
  <br><br>
  ⭐ Star if you find it helpful!
</div>