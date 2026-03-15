<div align="center">

[English](README.md) | [Русский](README.ru.md) | [O'zbekcha](README.uz.md)

# TelegramLogger

[![Java](https://img.shields.io/badge/Java-8%2B-orange)](https://www.java.com)
[![Spigot](https://img.shields.io/badge/Spigot-1.16--1.21-yellow)](https://www.spigotmc.org)
[![Version](https://img.shields.io/badge/version-5.0.0-brightgreen)](https://github.com/LazizbekDeveloper/TelegramLogger/releases)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Downloads](https://img.shields.io/badge/downloads-1K%2B-blue)](https://www.spigotmc.org/resources/120590)

<img src="https://minecraft.wiki/images/Mojang_logo.svg" width="96px" />
 --->
<img src="https://telegram.org/img/t_logo.svg" width="96px" />

**Connect your Minecraft server with Telegram!**
Monitor all server activities in real-time through your Telegram group or channel.

</div>

---

## Table of Contents

- [Features Overview](#-features-overview)
- [What's New in v5.0.0](#-whats-new-in-v500)
- [Requirements](#-requirements)
- [Installation Guide](#-installation-guide)
  - [Step 1: Download and Install](#step-1-download-and-install)
  - [Step 2: Create a Telegram Bot](#step-2-create-a-telegram-bot)
  - [Step 3: Get Your Chat ID](#step-3-get-your-chat-id)
  - [Step 4: Configure the Plugin](#step-4-configure-the-plugin)
- [Configuration Reference](#-configuration-reference)
  - [Bot Configuration](#bot-configuration)
  - [Message Prefix](#message-prefix)
  - [Server Start/Stop Notifications](#server-startstop-notifications)
  - [Join/Leave Messages](#joinleave-messages)
  - [First Join (New Player Welcome)](#first-join-new-player-welcome)
  - [Chat Messages](#chat-messages)
  - [Advancement Messages](#advancement-messages)
  - [Death Messages](#death-messages)
  - [World Switch Messages](#world-switch-messages)
  - [Chat Filter](#chat-filter)
  - [Command Execution Logging](#command-execution-logging)
  - [Telegram Sudo Command](#telegram-sudo-command)
  - [Anti-Flood Protection](#anti-flood-protection)
  - [Debug Mode](#debug-mode)
- [Commands Reference](#-commands-reference)
  - [In-Game Commands](#in-game-commands)
  - [Telegram Commands](#telegram-commands)
- [Admin Management](#-admin-management)
- [Message Placeholders](#-message-placeholders)
- [HTML Formatting Guide](#-html-formatting-guide)
- [Forum Thread Organization](#-forum-thread-organization)
- [Statistics and Metrics](#-statistics-and-metrics)
- [Config Auto-Restore System](#-config-auto-restore-system)
- [Architecture](#-architecture)
- [Building from Source](#-building-from-source)
- [Troubleshooting](#-troubleshooting)
- [FAQ](#-faq)
- [Contact and Support](#-contact--support)
- [License](#-license)
- [Credits](#-credits)

---

## Features Overview

### Real-time Event Forwarding
- **Player Join/Leave** — Instant notifications with online player count
- **First Join Detection** — Special welcome messages for new players
- **Chat Monitoring** — All in-game chat forwarded to Telegram with HTML formatting
- **Death Tracking** — Detailed death messages including killer information
- **Advancement System** — Player achievement notifications (recipe unlocks filtered out)
- **World Monitoring** — Dimension change tracking (Overworld, Nether, End, custom worlds)
- **Command Logging** — Monitor command executions with configurable filtering
- **Server Start/Stop** — Notifications when the server starts or shuts down

### Telegram Integration
- **Two-Way Chat** — Send messages from Telegram directly to Minecraft
- **Forum Thread Support** — Organize messages in Telegram forum topics
- **Remote Server Commands** — Execute any server command via `/sudo` with full output capture
- **Server Status** — Check server status, TPS, memory, and online players from Telegram
- **Admin System** — Register Telegram admins with persistent storage
- **HTML Formatting** — Rich message formatting with bold, italic, code blocks, and blockquotes

### Protection and Reliability
- **Anti-Flood** — Rate limiting prevents message flooding in both directions
- **Config Auto-Restore** — Corrupt config files are backed up and automatically regenerated
- **Chat Filtering** — Block unwanted words with admin notifications
- **Sudo Blacklist** — Prevent dangerous commands from being executed remotely
- **Duplicate Message Prevention** — Polling guard eliminates message duplication

### Developer Features
- **Modular Architecture** — Clean package structure with separated concerns
- **Debug Mode** — Detailed logging for troubleshooting
- **Statistics Tracking** — Comprehensive message and event counting
- **Auto-Update Check** — Notifies server admins when a new version is available

---

## What's New in v5.0.0

### Bug Fixes
- **Fixed duplicate Telegram messages** — Messages from Telegram were sometimes delivered 2-3 times to Minecraft. Root cause was overlapping long-poll requests. Fixed with an atomic polling guard.
- **Fixed `/sudo` command crashes** — The sudo command would crash in non-forum groups because it tried to access `message_thread_id` even when it didn't exist. Now handles missing thread IDs gracefully.
- **Fixed `/sudo` not showing output** — Previously, `/sudo plugins` would say "executed successfully" but show no output. Now captures and displays full command output in Telegram.
- **Fixed multi-line message prefix spam** — Multi-line messages from Telegram had the plugin prefix prepended to every line. Now the prefix is applied only once.

### New Features
- **Server Start/Stop Notifications** — Get notified in Telegram when your server starts or stops
- **First Join Detection** — Special welcome messages for players joining for the first time
- **Anti-Flood System** — Configurable rate limiting prevents message flooding
- **Sudo Output Capture** — `/sudo` commands now show their full output in the Telegram response
- **Sudo Blacklist** — Block dangerous commands (`stop`, `op`, `ban-ip`, etc.) from remote execution
- **TPS Command** — Check server TPS and memory usage from Telegram with `/tps`
- **Config Auto-Restore** — If your config becomes corrupt, the plugin automatically backs it up, creates a fresh config, and migrates recoverable values

### Code Improvements
- **Full code restructuring** — Monolithic 3000-line file split into 11 focused classes across 6 packages
- **Clean architecture** — Separated concerns: config, telegram API, event handling, commands, data management
- **Better error handling** — Graceful failure instead of silent crashes
- **HTML escaping** — User input is properly escaped to prevent HTML injection in Telegram messages

---

## Requirements

| Requirement | Details |
|---|---|
| Java | 8 or newer |
| Server | Spigot or Paper 1.16.x - 1.21.x |
| Telegram Bot Token | From [@BotFather](https://t.me/botfather) |
| Telegram Chat ID | Group or channel numeric ID |
| Thread ID | Only if using forum topics |

---

## Installation Guide

### Step 1: Download and Install

1. Download the latest `TelegramLogger-5.0.0.jar` from [Releases](https://github.com/LazizbekDeveloper/TelegramLogger/releases)
2. Place the `.jar` file in your server's `plugins/` folder
3. Start the server once to generate the default configuration
4. Stop the server to edit the configuration

```
plugins/
  TelegramLogger-5.0.0.jar
  TelegramLogger/
    config.yml          <-- Edit this file
    data.json           <-- Auto-generated statistics
    admins.json         <-- Auto-generated admin registry
```

### Step 2: Create a Telegram Bot

1. Open Telegram and find [@BotFather](https://t.me/botfather)
2. Send `/newbot` and follow the prompts:
   ```
   /newbot
   > Enter bot name: My Server Bot
   > Enter bot username: my_server_bot
   ```
3. Copy the **API Token** that BotFather gives you (looks like `123456:ABC-DEF1234ghIkl-zyx57W2v1u123ew11`)

4. **Important Bot Settings** (send these to BotFather):
   ```
   /setprivacy    -> Choose your bot -> Disable
   /setjoingroups -> Choose your bot -> Enable
   ```

5. (Optional) Set bot commands for a nice command menu:
   ```
   /setcommands -> Choose your bot -> Paste this:
   status - Show server status
   stats - Message statistics
   players - Online players
   reload - Reload plugin
   start - Start forwarding
   stop - Stop forwarding
   debug - Toggle debug mode
   tps - Server performance
   sudo - Execute server command
   help - Show commands
   ```

### Step 3: Get Your Chat ID

1. Create a Telegram group (or use an existing one)
2. Add your bot to the group **as an administrator**
3. Add [@RawDataBot](https://t.me/rawdatabot) to the group temporarily
4. Look for `"chat":{"id": -123456789}` in its response — that number is your **Chat ID**
5. Remove RawDataBot from the group

**For Forum Groups (with topics):**
- Send a message in the specific topic where you want notifications
- RawDataBot will show a `"message_thread_id"` — that's your **Thread ID**

### Step 4: Configure the Plugin

Edit `plugins/TelegramLogger/config.yml`:

```yaml
# Required: Your bot token from BotFather
bot_token: "123456:ABC-DEF1234ghIkl-zyx57W2v1u123ew11"

# Required: Your group/channel chat ID
chat_id: "-1001234567890"

# Optional: Thread ID for forum topics
thread_id: "123"
send_to_thread: false

# Enable two-way chat (Telegram messages appear in Minecraft)
send_telegram_messages_to_game: true
```

Save the file and run `/tl reload` in-game, or restart the server.

**Register yourself as an admin** (required for Telegram commands):
```
/tl admin add YOUR_TELEGRAM_USER_ID YourName
```
To find your Telegram user ID, send a message in the group with RawDataBot and look for `"from":{"id": 123456}`.

---

## Configuration Reference

### Bot Configuration

```yaml
bot_token: "BOT_TOKEN"                    # Your Telegram bot API token
chat_id: "CHAT_ID"                        # Target group/channel ID
thread_id: "THREAD_ID"                    # Forum topic thread ID
send_to_thread: false                     # Enable forum topic support
send_telegram_messages_to_game: false     # Forward Telegram messages to Minecraft
```

### Message Prefix

```yaml
plugin_prefix: "&6&lTelegramLogger&7 ➜ &r&a"   # In-game message prefix (supports color codes)
telegram_game_message: "&7[&9TG&7] &c%name% &8» &f%message%"   # Format for TG→MC messages
```

The `telegram_game_message` template controls how Telegram messages appear in Minecraft. Available placeholders: `%name%` (sender name), `%message%` (message text).

### Server Start/Stop Notifications

```yaml
enable_server_start_stop: true
server_start_message: "<blockquote>🟢 <b>Server started!</b>\nVersion: %version% | Max Players: %max%</blockquote>"
server_stop_message: "<blockquote>🔴 <b>Server stopped!</b></blockquote>"
```

Sends a notification to Telegram when the server starts or shuts down. Placeholders: `%version%`, `%max%`.

### Join/Leave Messages

```yaml
enable_join: true
join_message: "<blockquote>➕ <b>%player%</b> joined the game! (%online%/%max%)</blockquote>"

enable_leave: true
leave_message: "<blockquote>➖ <b>%player%</b> left the game! (%online%/%max%)</blockquote>"
```

### First Join (New Player Welcome)

```yaml
enable_first_join: true
first_join_message: "<blockquote>🌟 <b>%player%</b> joined for the first time! Welcome! (%online%/%max%)</blockquote>"
```

Triggers only when a player joins the server for the very first time (detected via `player.hasPlayedBefore()`).

### Chat Messages

```yaml
enable_chat: true
chat_message: "💬 <b>%player%</b> ➥ %message%"
```

All in-game chat messages are forwarded to Telegram. User messages are HTML-escaped to prevent injection.

### Advancement Messages

```yaml
enable_advancement: true
advancement_message: "<blockquote>🏆 <b>%player%</b> earned <b>[%advancement%]</b> (%online%/%max%)</blockquote>"
```

Recipe unlock advancements are automatically filtered out.

### Death Messages

```yaml
enable_death: true
death_message: "<blockquote>💀 %death_message% (%online%/%max%)</blockquote>"
```

Includes the full Minecraft death message with killer information.

### World Switch Messages

```yaml
enable_world_switch: true
world_switch_message: "<blockquote>🌍 <b>%player%</b> moved: %from_world% → %to_world% (%online%/%max%)</blockquote>"
```

World names are automatically formatted with emojis:
| World Name | Display |
|---|---|
| `world` | 🌍 Overworld |
| `world_nether` | 🔥 The Nether |
| `world_the_end` | 🌌 The End |
| `spawn` | 🏔️ Spawn |
| `lobby` | 🏔️ Lobby |
| `custom_world` | 🌎 Custom World |

### Chat Filter

```yaml
enable_chat_filter: true
filtered_words:
  - "badword1"
  - "badword2"
  - "offensive_term"
filtered_message: "<blockquote>🚫 <b>%player%</b> used a filtered word. (%online%/%max%)</blockquote>"
```

When a filtered word is detected:
1. The original message is **not** forwarded to Telegram
2. A filtered notification is sent instead
3. In-game admins receive a warning

### Command Execution Logging

```yaml
enable_send_command_executes: false
command_execute_message: "<blockquote>💠 <b>%player%</b> ➥ <code>%command%</code> (%online%/%max%)</blockquote>"
command_executes_chat_id: "CHAT_ID"           # Can be different from main chat
send_command_executes_to_thread: false
command_executes_group_thread_id: "THREAD_ID"
ignored_commands:
  - "/login"
  - "/register"
  - "/l"
  - "/reg"
```

Commands in the `ignored_commands` list will not be logged. You can use a separate Telegram chat/thread for command logs to keep your main chat clean.

### Telegram Sudo Command

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

When enabled, registered admins can execute server commands from Telegram:

```
/sudo list          → Shows online players
/sudo plugins       → Lists installed plugins with output
/sudo kill Steve    → Kills the player Steve
/sudo say Hello!    → Broadcasts a message
/sudo gamemode creative Steve → Changes gamemode
```

The response shows:
- Whether the command succeeded
- The actual command that was executed
- Which admin ran it
- The full command output (if `sudo_show_output: true`)

Commands in `sudo_blacklist` are blocked from execution for safety.

### Anti-Flood Protection

```yaml
anti_flood_enabled: true
anti_flood_max_messages: 20     # Maximum messages allowed per window
anti_flood_window_seconds: 10   # Window duration in seconds
```

Prevents flooding in situations like:
- Mass player join/leave events
- Chat spam
- Rapid advancement unlocks
- Event storms

Messages that exceed the rate limit are silently dropped.

### Debug Mode

```yaml
debug_mode: false
```

Enable with `/tl debug` in-game or `/debug` from Telegram. Shows detailed logs for:
- Message processing
- Telegram API calls
- Config loading
- Rate limiting decisions
- Admin checks

---

## Commands Reference

### In-Game Commands

All commands require the `telegramlogger.admin` permission (default: op).

| Command | Aliases | Description |
|---|---|---|
| `/telegramlogger reload` | `/tl reload` | Reload configuration from disk |
| `/telegramlogger start` | `/tl start` | Start message forwarding |
| `/telegramlogger stop` | `/tl stop` | Stop message forwarding |
| `/telegramlogger stats` | `/tl stats` | View detailed message statistics |
| `/telegramlogger status` | `/tl status` | Show plugin and bot status |
| `/telegramlogger debug` | `/tl debug` | Toggle debug mode |
| `/telegramlogger admin add <id> <name>` | `/tl admin add <id> <name>` | Register a Telegram admin |
| `/telegramlogger admin remove <id>` | `/tl admin remove <id>` | Remove a Telegram admin |
| `/telegramlogger admin list` | `/tl admin list` | List all registered admins |
| `/telegramlogger help` | `/tl help` | Show help message |

### Telegram Commands

All commands require the sender to be a registered admin.

| Command | Description |
|---|---|
| `/status` | Show plugin status, features, server info, and memory |
| `/stats` | View message statistics with counts and percentages |
| `/players` or `/online` | List online players (up to 20, with admin crowns) |
| `/tps` | Show server TPS (1m/5m/15m) and memory usage |
| `/reload` | Reload plugin configuration |
| `/start` | Start message forwarding |
| `/stop` | Stop message forwarding |
| `/debug` | Toggle debug mode |
| `/sudo <command>` | Execute a server command (if enabled) |
| `/help` | Show available commands |

---

## Admin Management

Admins are Telegram users who can:
- Send messages to the Minecraft chat from Telegram
- Use Telegram commands (/status, /stats, /reload, etc.)
- Execute server commands via /sudo (if enabled)

### Registering an Admin

```
/tl admin add 123456789 Steve
```

Where `123456789` is the Telegram user ID and `Steve` is a display name.

### Removing an Admin

```
/tl admin remove 123456789
```

### Listing Admins

```
/tl admin list
```

Admin data is stored in `plugins/TelegramLogger/admins.json` and persists across restarts.

**Important:** An admin must be both:
1. Registered via `/tl admin add`
2. A group administrator or creator in the Telegram group

---

## Message Placeholders

Available placeholders vary by message type:

| Placeholder | Available In | Description |
|---|---|---|
| `%player%` | All player events | Player username |
| `%displayname%` | All player events | Player display name (may include colors) |
| `%online%` | All events | Current online player count |
| `%max%` | All events | Maximum player slots |
| `%message%` | Chat messages | The chat message content |
| `%command%` | Command logging | The executed command |
| `%death_message%` | Death messages | Full death message from Minecraft |
| `%advancement%` | Advancement messages | Achievement name |
| `%from_world%` | World switch | Previous world (with emoji) |
| `%to_world%` | World switch | New world (with emoji) |
| `%version%` | Server start | Server version string |
| `%name%` | Telegram→MC messages | Telegram sender name |

---

## HTML Formatting Guide

Telegram supports HTML formatting in messages. Use these tags in your message templates:

```html
<b>Bold text</b>
<i>Italic text</i>
<u>Underlined text</u>
<s>Strikethrough text</s>
<code>Monospace/code text</code>
<pre>Preformatted block</pre>
<blockquote>Quoted text block</blockquote>
<a href="https://example.com">Link text</a>
```

### Example Custom Messages

```yaml
# Fancy join message
join_message: "<blockquote>🎮 <b>%player%</b> has joined!\n👥 Players: <code>%online%/%max%</code></blockquote>"

# Death message with formatting
death_message: "<blockquote>💀 <i>%death_message%</i>\n👥 <code>%online%/%max%</code></blockquote>"

# Minimal chat format
chat_message: "<b>%player%</b>: %message%"

# Detailed command log
command_execute_message: "🔧 <b>%player%</b> ran: <code>%command%</code>"
```

---

## Forum Thread Organization

Telegram forum groups (supergroups with topics) let you organize different event types into separate threads:

```yaml
# Main events go to one thread
send_to_thread: true
thread_id: "12345"

# Command logs go to a different thread (or even a different group)
enable_send_command_executes: true
command_executes_chat_id: "-1001234567890"
send_command_executes_to_thread: true
command_executes_group_thread_id: "67890"
```

This keeps your server events organized and easy to navigate.

---

## Statistics and Metrics

The plugin tracks comprehensive statistics:

| Metric | Description |
|---|---|
| Total Messages | All messages sent to Telegram |
| Join Messages | Player join event count |
| Leave Messages | Player leave event count |
| Chat Messages | Chat messages forwarded |
| Advancement Messages | Achievements forwarded |
| Death Messages | Death events forwarded |
| World Switch Messages | Dimension changes forwarded |
| Filtered Messages | Messages blocked by chat filter |
| First Join Messages | New player welcome messages |
| Command Messages | Commands logged |
| Ignored Commands | Commands in the ignore list that were skipped |
| Server Start Count | Number of server starts logged |

View statistics:
- **In-game:** `/tl stats` — Shows progress bars and percentages
- **Telegram:** `/stats` — Shows clean HTML-formatted statistics

Statistics are stored in `plugins/TelegramLogger/data.json` and persist across restarts.

---

## Config Auto-Restore System

If your `config.yml` becomes corrupt (invalid YAML, encoding issues, etc.), the plugin automatically:

1. **Detects** the corrupt file during loading
2. **Backs up** the broken file as `config.yml.broken.2026-03-15_14-30-00`
3. **Creates** a fresh `config.yml` with all default values
4. **Recovers** any valid `key: value` pairs from the broken file
5. **Merges** the recovered values into the new config

This means you never lose your bot token, chat IDs, or custom messages due to a YAML syntax error.

---

## Architecture

The plugin is organized into clean, modular packages:

```
uz.lazizbekdev.telegramlogger/
├── TelegramLogger.java              # Main plugin (lifecycle, manager coordination)
├── config/
│   └── ConfigManager.java           # Config load/save/validate/restore
├── telegram/
│   ├── TelegramAPI.java             # HTTP communication with Telegram Bot API
│   ├── TelegramHandler.java         # Polling, command routing, message processing
│   └── OutputCapturingSender.java   # Command output capture for /sudo
├── listeners/
│   └── EventListener.java           # All Minecraft event handlers
├── commands/
│   └── CommandHandler.java          # /telegramlogger command handler + tab complete
├── managers/
│   ├── DataManager.java             # Statistics persistence (data.json)
│   └── AdminManager.java            # Admin registry persistence (admins.json)
└── utils/
    ├── MessageUtils.java            # Color codes, formatting, placeholders
    └── AntiFloodManager.java        # Rate limiting with sliding window
```

---

## Building from Source

### Prerequisites
- Java Development Kit (JDK) 8 or newer
- Apache Maven 3.6+
- Git

### Build Steps

```bash
# Clone the repository
git clone https://github.com/LazizbekDeveloper/TelegramLogger.git
cd TelegramLogger

# Build the plugin
mvn clean package

# The output JAR will be at:
# target/TelegramLogger-5.0.0.jar
```

### Build Output

The build process:
1. Compiles all Java sources against Spigot API 1.16.5
2. Shades Google GSON into the jar (relocated to avoid conflicts)
3. Produces a single self-contained JAR file

The output JAR is ready to use — just drop it in your `plugins/` folder.

---

## Troubleshooting

### Bot Not Responding

1. **Check your bot token** — Make sure it's copied correctly from BotFather
2. **Bot must be a group admin** — Add the bot as an administrator in your Telegram group
3. **Verify the chat ID** — Use RawDataBot to confirm the correct group ID
4. **Privacy mode** — Send `/setprivacy` to BotFather and set it to `Disable` for your bot
5. **Check `/tl status`** — Shows if the bot connection is active

### Messages Not Appearing in Telegram

1. **Check feature toggles** — Make sure `enable_join`, `enable_chat`, etc. are `true`
2. **Check anti-flood** — If too many events fire at once, some may be rate-limited
3. **Enable debug mode** — Run `/tl debug` and check console for errors
4. **Verify config is valid** — Run `/tl reload` and check for error messages

### Messages Not Appearing in Minecraft

1. **Enable the feature** — Set `send_telegram_messages_to_game: true`
2. **Register as admin** — Use `/tl admin add <your_telegram_id> <name>`
3. **Must be group admin** — Your Telegram account must be a group admin AND registered

### `/sudo` Not Working

1. **Enable sudo** — Set `enable_sudo_command: true` in config
2. **Check blacklist** — The command might be in `sudo_blacklist`
3. **Register as admin** — Must be registered via `/tl admin add`
4. **Check output** — The command might execute but produce no visible output

### Duplicate Messages

This was a known issue in v4.x and is fixed in v5.0.0. If you're still experiencing it:
1. Make sure you're running v5.0.0 or newer
2. Try `/tl reload` to restart the polling system

### Forum Thread Issues

1. **Thread ID must match** — Use RawDataBot in the specific topic to get the correct thread ID
2. **`send_to_thread` must be `true`** — Otherwise thread_id is ignored
3. **Bot must have topic permissions** — Ensure the bot can post in the specific topic

### Config Corrupted

The plugin handles this automatically in v5.0.0:
1. The broken config is saved as `config.yml.broken.<timestamp>`
2. A fresh config is generated
3. Recoverable values are migrated

You can also manually fix issues by:
1. Checking the broken file for YAML syntax errors
2. Using a YAML validator (e.g., yamllint.com)
3. Common issues: missing quotes, tab characters (use spaces), unclosed strings

---

## FAQ

**Q: Does this plugin work with Paper?**
A: Yes! TelegramLogger works with any Spigot-based server including Paper, Purpur, and forks.

**Q: Can I use this with multiple Telegram groups?**
A: The main events go to one group/thread. Command logging can go to a separate group via `command_executes_chat_id`.

**Q: Is the bot token stored securely?**
A: The bot token is stored in `config.yml` on your server. Make sure your server files are properly secured.

**Q: How do I find my Telegram user ID?**
A: Add [@RawDataBot](https://t.me/rawdatabot) to a group, send a message, and look for `"from":{"id": YOUR_ID}`.

**Q: Can regular players use Telegram commands?**
A: No. Only users who are both registered admins AND Telegram group admins can use commands.

**Q: Does `/sudo` have any limitations?**
A: Yes — commands in `sudo_blacklist` are blocked. Output is truncated to 3000 characters. The command runs as console (full permissions).

**Q: What happens if Telegram is down?**
A: The plugin gracefully handles API failures. Events continue to be tracked locally. Messages are simply not sent until the API is available again.

**Q: How much server performance does this use?**
A: Minimal. All Telegram communication is asynchronous and runs off the main thread. The anti-flood system prevents excessive API calls.

**Q: Can I customize the emoji in world names?**
A: Currently, world name formatting is built-in. Custom world names are auto-formatted. You can customize the message templates to add your own emoji.

---

## Default Configuration

<details>
<summary>Click to expand the full default config.yml</summary>

```yaml
# ===========================================
#        TelegramLogger v5.0.0
#        Developed by LazizbekDev
#        Telegram: https://t.me/LazizbekDev
# ===========================================

bot_token: "BOT_TOKEN"
chat_id: "CHAT_ID"
thread_id: "THREAD_ID"
send_to_thread: false
send_telegram_messages_to_game: false

plugin_prefix: "&6&lTelegramLogger&7 ➜ &r&a"
telegram_game_message: "&7[&9TG&7] &c%name% &8» &f%message%"

enable_server_start_stop: true
server_start_message: "<blockquote>🟢 <b>Server started!</b>\nVersion: %version% | Max Players: %max%</blockquote>"
server_stop_message: "<blockquote>🔴 <b>Server stopped!</b></blockquote>"

enable_join: true
join_message: "<blockquote>➕ <b>%player%</b> joined the game! (%online%/%max%)</blockquote>"

enable_first_join: true
first_join_message: "<blockquote>🌟 <b>%player%</b> joined for the first time! Welcome! (%online%/%max%)</blockquote>"

enable_leave: true
leave_message: "<blockquote>➖ <b>%player%</b> left the game! (%online%/%max%)</blockquote>"

enable_chat: true
chat_message: "💬 <b>%player%</b> ➥ %message%"

enable_advancement: true
advancement_message: "<blockquote>🏆 <b>%player%</b> earned <b>[%advancement%]</b> (%online%/%max%)</blockquote>"

enable_death: true
death_message: "<blockquote>💀 %death_message% (%online%/%max%)</blockquote>"

enable_world_switch: true
world_switch_message: "<blockquote>🌍 <b>%player%</b> moved: %from_world% → %to_world% (%online%/%max%)</blockquote>"

enable_chat_filter: true
filtered_words:
  - "badword1"
  - "badword2"
filtered_message: "<blockquote>🚫 <b>%player%</b> used a filtered word. (%online%/%max%)</blockquote>"

enable_send_command_executes: false
command_execute_message: "<blockquote>💠 <b>%player%</b> ➥ <code>%command%</code> (%online%/%max%)</blockquote>"
command_executes_chat_id: "CHAT_ID"
send_command_executes_to_thread: false
command_executes_group_thread_id: "THREAD_ID"
ignored_commands:
  - "/login"
  - "/register"
  - "/l"
  - "/reg"

enable_sudo_command: false
sudo_show_output: true
sudo_blacklist:
  - "stop"
  - "restart"
  - "op"
  - "deop"
  - "ban-ip"

anti_flood_enabled: true
anti_flood_max_messages: 20
anti_flood_window_seconds: 10

error_not_admin: "<blockquote>❌ You are not registered as an admin!</blockquote>"

debug_mode: false

version: "5.0.0"
```

</details>

---

## Contact & Support

- **Telegram:** [@LazizbekDev](https://t.me/LazizbekDev)
- **Updates:** [@LazizbekDev_Blog](https://t.me/LazizbekDev_Blog)
- **Issues:** [GitHub Issues](https://github.com/LazizbekDeveloper/TelegramLogger/issues)
- **SpigotMC:** [Resource Page](https://www.spigotmc.org/resources/120590)

---

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

## Credits

- **Developer:** [LazizbekDev](https://t.me/LazizbekDev)
- **Framework:** [Spigot API](https://www.spigotmc.org)
- **JSON Library:** [Google GSON](https://github.com/google/gson)

---

<div align="center">
  <b>Made with ❤️ by <a href="https://t.me/LazizbekDev">LazizbekDev</a></b>
  <br><br>
  If you find this plugin helpful, please give it a ⭐ on GitHub!
</div>
