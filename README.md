# TelegramLogger 📱

[![Java](https://img.shields.io/badge/Java-8%2B-orange)](https://www.java.com)
[![Spigot](https://img.shields.io/badge/Spigot-1.16%2B-yellow)](https://www.spigotmc.org)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

A powerful Minecraft plugin that forwards server events to Telegram in real-time! Keep track of your server activity from anywhere.

<div align="center">
  <img src="https://minecraft.wiki/images/Mojang_logo.svg" width="100px" />
  ➡️
  <img src="https://telegram.org/img/t_logo.svg" width="100px" />
</div>

## 🌟 Features

- **Real-time Event Forwarding**
  - Player Join/Leave 👋
  - Chat Messages 💬
  - Deaths ☠️
  - Achievements 🏆
  - World Changes 🌍
  - ... and more!

- **Advanced Chat Filtering**
  - Block unwanted words
  - Customizable filter list
  - Admin notifications

- **Admin Controls**
  - Telegram admin management
  - Remote server commands
  - Status monitoring

- **Detailed Statistics**
  - Message counts
  - Visual progress bars
  - Event distribution

- **Customization**
  - Message templates
  - Emoji support
  - Color formatting
  - Multi-world support

## 📋 Requirements

- Java 8 or higher
- Spigot/Paper 1.16+
- Telegram Bot Token
- Telegram Group/Channel ID

## 🚀 Installation

1. Download the latest release from [Releases](https://github.com/LazizbekDeveloper/TelegramLogger/releases)
2. Place the jar file in your server's `plugins` folder
3. Start/restart your server
4. Configure the plugin in `plugins/TelegramLogger/config.yml`

## ⚙️ Configuration

1. Create a Telegram bot using [@BotFather](https://t.me/botfather)
2. Get your bot token
3. Add the bot to your group/channel
4. Get the chat ID
5. Configure in `config.yml`:

```yaml
bot_token: "YOUR_BOT_TOKEN"
chat_id: "YOUR_CHAT_ID"
thread_id: "THREAD_ID" # Optional, for forum topics
send_to_thread: false
```

## 📝 Commands

| Command | Description |
|---------|-------------|
| `/telegramlogger reload` | Reload the plugin |
| `/telegramlogger start` | Start message forwarding |
| `/telegramlogger stop` | Stop message forwarding |
| `/telegramlogger stats` | View message statistics |
| `/telegramlogger status` | Show plugin status |
| `/telegramlogger debug` | Toggle debug mode |
| `/telegramlogger admin add <id> <name>` | Add new admin |
| `/telegramlogger admin remove <id>` | Remove admin |
| `/telegramlogger admin list` | List all admins |
| `/telegramlogger help` | Show help message |

## 🔒 Permissions

- `telegramlogger.admin` - Access to all commands (Default: OP)

## 🎨 Message Templates

Customize your messages using placeholders:

- `%player%` - Player name
- `%displayname%` - Player display name
- `%message%` - Chat message
- `%online%` - Online players count
- `%max%` - Maximum players
- And more!

## 🏗️ Building from Source

1. Clone the repository
```bash
git clone https://github.com/LazizbekDeveloper/TelegramLogger.git
```

2. Build using Maven
```bash
cd TelegramLogger
mvn clean package
```

3. Find the jar in `target/TelegramLogger-1.0.jar`

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch
```bash
git checkout -b feature/AmazingFeature
```
3. Commit your changes
```bash
git commit -m 'Add some AmazingFeature'
```
4. Push to the branch
```bash
git push origin feature/AmazingFeature
```
5. Open a Pull Request

## 📄 License

Distributed under the MIT License. See [LICENSE](LICENSE) for more information.

## 📞 Support

- Create an [Issue](https://github.com/LazizbekDeveloper/TelegramLogger/issues)
- Join our [Telegram Channel](https://t.me/LazizbekDev_Blog)
- Contact [Developer](https://t.me/LazizbekDev)

## 🌟 Credits

- Author: [LazizbekDev](https://t.me/LazizbekDev)
- Powered by [Spigot](https://www.spigotmc.org)
- Icons from [Flaticon](https://www.flaticon.com)

---

<div align="center">
  Made with ❤️ by <a href="https://t.me/LazizbekDev">LazizbekDev</a>
  <br>
  Star ⭐ this repository if you find it helpful!
</div>